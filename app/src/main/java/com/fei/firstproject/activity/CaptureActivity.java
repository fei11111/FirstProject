package com.fei.firstproject.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;
import com.fei.firstproject.zxing.camera.CameraManager;
import com.fei.firstproject.zxing.decoding.CaptureActivityHandler;
import com.fei.firstproject.zxing.decoding.InactivityTimer;
import com.fei.firstproject.zxing.decoding.RGBLuminanceSource;
import com.fei.firstproject.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Initial the camera
 *
 * @author zhangguoyu
 */
public class CaptureActivity extends BaseActivity implements Callback {

    @BindView(R.id.preview_view)
    SurfaceView previewView;
    @BindView(R.id.viewfinder_view)
    ViewfinderView viewfinderView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_light)
    Button btnLight;
    @BindView(R.id.btn_openimg)
    Button btnOpenimg;
    @BindView(R.id.ahv)
    AppHeadView ahv;

    private boolean playBeep;
    private boolean vibrate;
    private boolean hasSurface;
    private String characterSet;
    private int ifOpenLight = 0;//判断是否开启闪光灯
    private MediaPlayer mediaPlayer;
    private CaptureActivityHandler handler;
    private Vector<BarcodeFormat> decodeFormats;
    private InactivityTimer inactivityTimer;
    private static final float BEEP_VOLUME = 0.10f;

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = previewView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void requestPermissionsBeforeInit() {

    }

    @Override
    public void permissionsDeniedCallBack(int requestCode) {

    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {

    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_capture;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        initListener();
    }

    private void initListener() {
        ahv.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                onBackPressed();
            }

            @Override
            public void onRight(View view) {

            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {

            }
        });
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {

    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode 获取结果
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        // FIXME
        if (resultString.equals("")) {
            Utils.showToast(this, "扫描失败!");
        } else {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("result", resultString);
            resultIntent.putExtras(bundle);
            this.setResult(RESULT_OK, resultIntent);
        }
        CaptureActivity.this.finish();
    }

    /*
     * 获取带二维码的相片进行扫描
     */
    public void pickPictureFromAblum(View v) {
        Intent mIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(mIntent, 1);

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onActivityResult(int, int,
     * android.content.Intent) 对相册获取的结果进行分析
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Result resultString = scanningImage1(picturePath);
                    if (resultString == null) {
                        Utils.showToast(this, "解析错误，请选择正确的二维码图片");
                    } else {

                        String resultImage = resultString.getText();
                        if (resultImage.equals("")) {
                            Utils.showToast(this, "扫描失败");
                        } else {

                            Intent resultIntent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("result", resultImage);
                            resultIntent.putExtras(bundle);
                            CaptureActivity.this.setResult(RESULT_OK, resultIntent);
                        }

                        CaptureActivity.this.finish();
                    }

                    break;

                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 解析QR图内容
     *
     * @return
     */
    // 解析QR图片
    private Result scanningImage1(String picturePath) {

        if (TextUtils.isEmpty(picturePath)) {
            return null;
        }

        Map<DecodeHintType, String> hints1 = new Hashtable<DecodeHintType, String>();
        hints1.put(DecodeHintType.CHARACTER_SET, "utf-8");

        // 获得待解析的图片
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result;
        try {

            result = reader.decode(bitmap1, (Hashtable<DecodeHintType, String>) hints1);
            return result;
        } catch (NotFoundException e) {
            Utils.showToast(this, "解析错误，请选择正确的二维码图片");
            e.printStackTrace();
        } catch (ChecksumException e) {
            Utils.showToast(this, "解析错误，请选择正确的二维码图片");
            e.printStackTrace();
        } catch (FormatException e) {
            Utils.showToast(this, "解析错误，请选择正确的二维码图片");
            e.printStackTrace();
        }
        return null;
    }

    // 是否开启闪光灯
    public void IfOpenLight(View v) {
        ifOpenLight++;

        switch (ifOpenLight % 2) {
            case 0:
                //关闪光灯
                CameraManager.get().closeLight();
                btnLight.setText(getString(R.string.str_open_light));
                break;
            case 1:
                //开闪光灯
                CameraManager.get().openLight();
                btnLight.setText(getString(R.string.str_close_light));
                break;
            default:
                break;
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {

            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}