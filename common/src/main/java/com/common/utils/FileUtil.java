package com.common.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import com.common.log.Lg;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileUtil {

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
                        return uri.getPath();
                    } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                        //把文件复制到沙盒目录
                        ContentResolver contentResolver = context.getContentResolver();
                        String displayName = System.currentTimeMillis() + Math.round((Math.random() + 1) * 1000)
                                + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));
                        try {
                            InputStream is = contentResolver.openInputStream(uri);
                            File cache = new File(context.getCacheDir().getAbsolutePath(), displayName);
                            FileOutputStream fos = new FileOutputStream(cache);
                            //android.os.FileUtils不是工具类，是Android的SDK方法
                            android.os.FileUtils.copy(is, fos);
                            fos.close();
                            is.close();
                            return cache.getAbsolutePath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }


            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndex(projection[0]);
                if (columnIndex >= 0) {
                    return cursor.getString(columnIndex);  //获取照片路径
                } else if (TextUtils.equals(uri.getAuthority(), getFileProviderName(context))) {
                    return parseOwnUri(context, uri);
                }
                return cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @Title: installApk

     * @Description: 安装APK

     * @return: void 适配7.0(AndroidManifest加provider)

     */

    /**
     * 安装
     *
     * @param mContext
     * @param path
     */
    public static void installApkNew(Context mContext, String path) {
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致  参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", file);//包名
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }


    public static void openFile(Context context, String path) {
        //调用系统文件管理器打开指定路径目录
        //获取到指定文件夹，这里为：/storage/emulated/0/Android/data/你的包	名/files/Download
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        //7.0以上跳转系统文件需用FileProvider，参考链接：https://blog.csdn.net/growing_tree/article/details/71190741
//        Uri uri =
//                FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
//        intent.setData(uri);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        context.startActivity(intent);
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:" + path);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");//想要展示的文件类型
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        context.startActivity(intent);

    }

    /**
     * 获取文件大小
     *
     * @param size
     * @return
     */
    public static String formatSize(long size) {
        String suffix = "B";
        double fSize = size;
        if (fSize > 1024) {
            suffix = "KB";
            fSize /= 1024;
        }
        if (fSize > 1024) {
            suffix = "MB";
            fSize /= 1024;
        }
        if (fSize > 1024) {
            suffix = "GB";
            fSize /= 1024;
        }
        return String.format(Locale.getDefault(), "%.2f%s", fSize, suffix);
    }


    /**
     * 将TakePhoto 提供的Uri 解析出文件绝对路径
     *
     * @param uri
     * @return
     */
    public static String parseOwnUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String path;
        if (!TextUtils.equals(uri.getAuthority(), getFileProviderName(context))) {
            path = uri.getPath();
        } else {
            path = new File(uri.getPath().replace("root/", "")).getAbsolutePath();
        }
        return path;
    }

    public final static String getFileProviderName(Context context) {
        return context.getPackageName() + ".fileprovider";
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 获取图片
     * @param context android10以上
     * @return
     */
    public static List < Uri > loadPhotoFiles(Context context) {
        List< Uri > photoUris = new ArrayList< Uri >();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {
                        MediaStore.Images.Media._ID
                }, null, null, null);
        while (cursor.moveToNext()) {
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            if(columnIndex!=-1) {
                int id = cursor.getInt(columnIndex);
                Uri photoUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
                photoUris.add(photoUri);
            }
        }
        return photoUris;
    }

    /**
     * 通过Uri读取文件
     * @param context
     * @param uri
     * @return
     * @throws IOException
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri, Rect outPadding, BitmapFactory.Options opts) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor,outPadding,opts);
        parcelFileDescriptor.close();
        return image;
    }

    /**
     *
     * @param context
     * @param file
     * @param saveDir
     * @return
     */
    public static boolean savePictureFile(Context context, File file,String saveDir) {
        if (file == null) {
            return false;
        }
        Uri uri = insertFileIntoMediaStore(context, file,saveDir, true);
        return saveFile(context, file, uri);
    }

    public static boolean saveVideoFile(Context context, File file,String saveDir) {
        if (file == null) {
            return false;
        }
        Uri uri = insertFileIntoMediaStore(context, file, saveDir,false);
        return saveFile(context, file, uri);
    }

    private static boolean saveFile(Context context, File file, Uri uri) {
        if (uri == null) {
            Lg.e("url is null");
            return false;
        }
        Lg.i("SaveFile: " + file.getName());
        ContentResolver contentResolver = context.getContentResolver();

        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "w");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (parcelFileDescriptor == null) {
            Lg.e("parcelFileDescriptor is null");
            return false;
        }

        FileOutputStream outputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Lg.e(e.toString());
            try {
                outputStream.close();
            } catch (IOException ex) {
                Lg.e(ex.toString());
            }
            return false;
        }

        try {
            copy(inputStream, outputStream);
        } catch (IOException e) {
            Lg.e(e.toString());
            return false;
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                Lg.e(e.toString());
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                Lg.e(e.toString());
            }
        }

        return true;
    }

    //注意当文件比较大时该方法耗时会比较大
    private static void copy(InputStream ist, OutputStream ost) throws IOException {
        byte[] buffer = new byte[4096];
        int byteCount;
        while ((byteCount = ist.read(buffer)) != -1) {
            ost.write(buffer, 0, byteCount);
        }
        ost.flush();
    }

    //创建视频或图片的URI
    private static Uri insertFileIntoMediaStore(Context context, File file,String saveDir, boolean isPicture) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, getMimeType(file.getName()));
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,saveDir);
        if (Build.VERSION.SDK_INT >= 29) {
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, file.lastModified());
        }
        Uri uri = null;
        try {
            uri = context.getContentResolver().insert(
                    (isPicture ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    , contentValues
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    /**
     * MIME 列表
     */
    private static final String[][] MIME_MapTable =
            {
                    // {后缀名， MIME类型}
                    {".3gp", "video/3gpp"},
                    {".3gpp", "video/3gpp"},
                    {".aac", "audio/x-mpeg"},
                    {".amr", "audio/x-mpeg"},
                    {".apk", "application/vnd.Android.package-archive"},
                    {".avi", "video/x-msvideo"},
                    {".aab", "application/x-authoware-bin"},
                    {".aam", "application/x-authoware-map"},
                    {".aas", "application/x-authoware-seg"},
                    {".ai", "application/postscript"},
                    {".aif", "audio/x-aiff"},
                    {".aifc", "audio/x-aiff"},
                    {".aiff", "audio/x-aiff"},
                    {".als", "audio/X-Alpha5"},
                    {".amc", "application/x-mpeg"},
                    {".ani", "application/octet-stream"},
                    {".asc", "text/plain"},
                    {".asd", "application/astound"},
                    {".asf", "video/x-ms-asf"},
                    {".asn", "application/astound"},
                    {".asp", "application/x-asap"},
                    {".asx", " video/x-ms-asf"},
                    {".au", "audio/basic"},
                    {".avb", "application/octet-stream"},
                    {".awb", "audio/amr-wb"},
                    {".bcpio", "application/x-bcpio"},
                    {".bld", "application/bld"},
                    {".bld2", "application/bld2"},
                    {".bpk", "application/octet-stream"},
                    {".bz2", "application/x-bzip2"},
                    {".bin", "application/octet-stream"},
                    {".bmp", "image/bmp"},
                    {".c", "text/plain"},
                    {".class", "application/octet-stream"},
                    {".conf", "text/plain"},
                    {".cpp", "text/plain"},
                    {".cal", "image/x-cals"},
                    {".ccn", "application/x-cnc"},
                    {".cco", "application/x-cocoa"},
                    {".cdf", "application/x-netcdf"},
                    {".cgi", "magnus-internal/cgi"},
                    {".chat", "application/x-chat"},
                    {".clp", "application/x-msclip"},
                    {".cmx", "application/x-cmx"},
                    {".co", "application/x-cult3d-object"},
                    {".cod", "image/cis-cod"},
                    {".cpio", "application/x-cpio"},
                    {".cpt", "application/mac-compactpro"},
                    {".crd", "application/x-mscardfile"},
                    {".csh", "application/x-csh"},
                    {".csm", "chemical/x-csml"},
                    {".csml", "chemical/x-csml"},
                    {".css", "text/css"},
                    {".cur", "application/octet-stream"},
                    {".doc", "application/msword"},
                    {".dcm", "x-lml/x-evm"},
                    {".dcr", "application/x-director"},
                    {".dcx", "image/x-dcx"},
                    {".dhtml", "text/html"},
                    {".dir", "application/x-director"},
                    {".dll", "application/octet-stream"},
                    {".dmg", "application/octet-stream"},
                    {".dms", "application/octet-stream"},
                    {".dot", "application/x-dot"},
                    {".dvi", "application/x-dvi"},
                    {".dwf", "drawing/x-dwf"},
                    {".dwg", "application/x-autocad"},
                    {".dxf", "application/x-autocad"},
                    {".dxr", "application/x-director"},
                    {".ebk", "application/x-expandedbook"},
                    {".emb", "chemical/x-embl-dl-nucleotide"},
                    {".embl", "chemical/x-embl-dl-nucleotide"},
                    {".eps", "application/postscript"},
                    {".epub", "application/epub+zip"},
                    {".eri", "image/x-eri"},
                    {".es", "audio/echospeech"},
                    {".esl", "audio/echospeech"},
                    {".etc", "application/x-earthtime"},
                    {".etx", "text/x-setext"},
                    {".evm", "x-lml/x-evm"},
                    {".evy", "application/x-envoy"},
                    {".exe", "application/octet-stream"},
                    {".fh4", "image/x-freehand"},
                    {".fh5", "image/x-freehand"},
                    {".fhc", "image/x-freehand"},
                    {".fif", "image/fif"},
                    {".fm", "application/x-maker"},
                    {".fpx", "image/x-fpx"},
                    {".fvi", "video/isivideo"},
                    {".flv", "video/x-msvideo"},
                    {".gau", "chemical/x-gaussian-input"},
                    {".gca", "application/x-gca-compressed"},
                    {".gdb", "x-lml/x-gdb"},
                    {".gif", "image/gif"},
                    {".gps", "application/x-gps"},
                    {".gtar", "application/x-gtar"},
                    {".gz", "application/x-gzip"},
                    {".gif", "image/gif"},
                    {".gtar", "application/x-gtar"},
                    {".gz", "application/x-gzip"},
                    {".h", "text/plain"},
                    {".hdf", "application/x-hdf"},
                    {".hdm", "text/x-hdml"},
                    {".hdml", "text/x-hdml"},
                    {".htm", "text/html"},
                    {".html", "text/html"},
                    {".hlp", "application/winhlp"},
                    {".hqx", "application/mac-binhex40"},
                    {".hts", "text/html"},
                    {".ice", "x-conference/x-cooltalk"},
                    {".ico", "application/octet-stream"},
                    {".ief", "image/ief"},
                    {".ifm", "image/gif"},
                    {".ifs", "image/ifs"},
                    {".imy", "audio/melody"},
                    {".ins", "application/x-NET-Install"},
                    {".ips", "application/x-ipscript"},
                    {".ipx", "application/x-ipix"},
                    {".it", "audio/x-mod"},
                    {".itz", "audio/x-mod"},
                    {".ivr", "i-world/i-vrml"},
                    {".j2k", "image/j2k"},
                    {".jad", "text/vnd.sun.j2me.app-descriptor"},
                    {".jam", "application/x-jam"},
                    {".jnlp", "application/x-java-jnlp-file"},
                    {".jpe", "image/jpeg"},
                    {".jpz", "image/jpeg"},
                    {".jwc", "application/jwc"},
                    {".jar", "application/java-archive"},
                    {".java", "text/plain"},
                    {".jpeg", "image/jpeg"},
                    {".jpg", "image/jpeg"},
                    {".js", "application/x-javascript"},
                    {".kjx", "application/x-kjx"},
                    {".lak", "x-lml/x-lak"},
                    {".latex", "application/x-latex"},
                    {".lcc", "application/fastman"},
                    {".lcl", "application/x-digitalloca"},
                    {".lcr", "application/x-digitalloca"},
                    {".lgh", "application/lgh"},
                    {".lha", "application/octet-stream"},
                    {".lml", "x-lml/x-lml"},
                    {".lmlpack", "x-lml/x-lmlpack"},
                    {".log", "text/plain"},
                    {".lsf", "video/x-ms-asf"},
                    {".lsx", "video/x-ms-asf"},
                    {".lzh", "application/x-lzh "},
                    {".m13", "application/x-msmediaview"},
                    {".m14", "application/x-msmediaview"},
                    {".m15", "audio/x-mod"},
                    {".m3u", "audio/x-mpegurl"},
                    {".m3url", "audio/x-mpegurl"},
                    {".ma1", "audio/ma1"},
                    {".ma2", "audio/ma2"},
                    {".ma3", "audio/ma3"},
                    {".ma5", "audio/ma5"},
                    {".man", "application/x-troff-man"},
                    {".map", "magnus-internal/imagemap"},
                    {".mbd", "application/mbedlet"},
                    {".mct", "application/x-mascot"},
                    {".mdb", "application/x-msaccess"},
                    {".mdz", "audio/x-mod"},
                    {".me", "application/x-troff-me"},
                    {".mel", "text/x-vmel"},
                    {".mi", "application/x-mif"},
                    {".mid", "audio/midi"},
                    {".midi", "audio/midi"},
                    {".m4a", "audio/mp4a-latm"},
                    {".m4b", "audio/mp4a-latm"},
                    {".m4p", "audio/mp4a-latm"},
                    {".m4u", "video/vnd.mpegurl"},
                    {".m4v", "video/x-m4v"},
                    {".mov", "video/quicktime"},
                    {".mp2", "audio/x-mpeg"},
                    {".mp3", "audio/x-mpeg"},
                    {".mp4", "video/mp4"},
                    {".mpc", "application/vnd.mpohun.certificate"},
                    {".mpe", "video/mpeg"},
                    {".mpeg", "video/mpeg"},
                    {".mpg", "video/mpeg"},
                    {".mpg4", "video/mp4"},
                    {".mpga", "audio/mpeg"},
                    {".msg", "application/vnd.ms-outlook"},
                    {".mif", "application/x-mif"},
                    {".mil", "image/x-cals"},
                    {".mio", "audio/x-mio"},
                    {".mmf", "application/x-skt-lbs"},
                    {".mng", "video/x-mng"},
                    {".mny", "application/x-msmoney"},
                    {".moc", "application/x-mocha"},
                    {".mocha", "application/x-mocha"},
                    {".mod", "audio/x-mod"},
                    {".mof", "application/x-yumekara"},
                    {".mol", "chemical/x-mdl-molfile"},
                    {".mop", "chemical/x-mopac-input"},
                    {".movie", "video/x-sgi-movie"},
                    {".mpn", "application/vnd.mophun.application"},
                    {".mpp", "application/vnd.ms-project"},
                    {".mps", "application/x-mapserver"},
                    {".mrl", "text/x-mrml"},
                    {".mrm", "application/x-mrm"},
                    {".ms", "application/x-troff-ms"},
                    {".mts", "application/metastream"},
                    {".mtx", "application/metastream"},
                    {".mtz", "application/metastream"},
                    {".mzv", "application/metastream"},
                    {".nar", "application/zip"},
                    {".nbmp", "image/nbmp"},
                    {".nc", "application/x-netcdf"},
                    {".ndb", "x-lml/x-ndb"},
                    {".ndwn", "application/ndwn"},
                    {".nif", "application/x-nif"},
                    {".nmz", "application/x-scream"},
                    {".nokia-op-logo", "image/vnd.nok-oplogo-color"},
                    {".npx", "application/x-netfpx"},
                    {".nsnd", "audio/nsnd"},
                    {".nva", "application/x-neva1"},
                    {".oda", "application/oda"},
                    {".oom", "application/x-AtlasMate-Plugin"},
                    {".ogg", "audio/ogg"},
                    {".pac", "audio/x-pac"},
                    {".pae", "audio/x-epac"},
                    {".pan", "application/x-pan"},
                    {".pbm", "image/x-portable-bitmap"},
                    {".pcx", "image/x-pcx"},
                    {".pda", "image/x-pda"},
                    {".pdb", "chemical/x-pdb"},
                    {".pdf", "application/pdf"},
                    {".pfr", "application/font-tdpfr"},
                    {".pgm", "image/x-portable-graymap"},
                    {".pict", "image/x-pict"},
                    {".pm", "application/x-perl"},
                    {".pmd", "application/x-pmd"},
                    {".png", "image/png"},
                    {".pnm", "image/x-portable-anymap"},
                    {".pnz", "image/png"},
                    {".pot", "application/vnd.ms-powerpoint"},
                    {".ppm", "image/x-portable-pixmap"},
                    {".pps", "application/vnd.ms-powerpoint"},
                    {".ppt", "application/vnd.ms-powerpoint"},
                    {".pptx", "application/vnd.ms-powerpoint"},
                    {".pqf", "application/x-cprplayer"},
                    {".pqi", "application/cprplayer"},
                    {".prc", "application/x-prc"},
                    {".proxy", "application/x-ns-proxy-autoconfig"},
                    {".prop", "text/plain"},
                    {".ps", "application/postscript"},
                    {".ptlk", "application/listenup"},
                    {".pub", "application/x-mspublisher"},
                    {".pvx", "video/x-pv-pvx"},
                    {".qcp", "audio/vnd.qcelp"},
                    {".qt", "video/quicktime"},
                    {".qti", "image/x-quicktime"},
                    {".qtif", "image/x-quicktime"},
                    {".r3t", "text/vnd.rn-realtext3d"},
                    {".ra", "audio/x-pn-realaudio"},
                    {".ram", "audio/x-pn-realaudio"},
                    {".ras", "image/x-cmu-raster"},
                    {".rdf", "application/rdf+xml"},
                    {".rf", "image/vnd.rn-realflash"},
                    {".rgb", "image/x-rgb"},
                    {".rlf", "application/x-richlink"},
                    {".rm", "audio/x-pn-realaudio"},
                    {".rmf", "audio/x-rmf"},
                    {".rmm", "audio/x-pn-realaudio"},
                    {".rnx", "application/vnd.rn-realplayer"},
                    {".roff", "application/x-troff"},
                    {".rp", "image/vnd.rn-realpix"},
                    {".rpm", "audio/x-pn-realaudio-plugin"},
                    {".rt", "text/vnd.rn-realtext"},
                    {".rte", "x-lml/x-gps"},
                    {".rtf", "application/rtf"},
                    {".rtg", "application/metastream"},
                    {".rtx", "text/richtext"},
                    {".rv", "video/vnd.rn-realvideo"},
                    {".rwc", "application/x-rogerwilco"},
                    {".rar", "application/x-rar-compressed"},
                    {".rc", "text/plain"},
                    {".rmvb", "audio/x-pn-realaudio"},
                    {".s3m", "audio/x-mod"},
                    {".s3z", "audio/x-mod"},
                    {".sca", "application/x-supercard"},
                    {".scd", "application/x-msschedule"},
                    {".sdf", "application/e-score"},
                    {".sea", "application/x-stuffit"},
                    {".sgm", "text/x-sgml"},
                    {".sgml", "text/x-sgml"},
                    {".shar", "application/x-shar"},
                    {".shtml", "magnus-internal/parsed-html"},
                    {".shw", "application/presentations"},
                    {".si6", "image/si6"},
                    {".si7", "image/vnd.stiwap.sis"},
                    {".si9", "image/vnd.lgtwap.sis"},
                    {".sis", "application/vnd.symbian.install"},
                    {".sit", "application/x-stuffit"},
                    {".skd", "application/x-Koan"},
                    {".skm", "application/x-Koan"},
                    {".skp", "application/x-Koan"},
                    {".skt", "application/x-Koan"},
                    {".slc", "application/x-salsa"},
                    {".smd", "audio/x-smd"},
                    {".smi", "application/smil"},
                    {".smil", "application/smil"},
                    {".smp", "application/studiom"},
                    {".smz", "audio/x-smd"},
                    {".sh", "application/x-sh"},
                    {".snd", "audio/basic"},
                    {".spc", "text/x-speech"},
                    {".spl", "application/futuresplash"},
                    {".spr", "application/x-sprite"},
                    {".sprite", "application/x-sprite"},
                    {".sdp", "application/sdp"},
                    {".spt", "application/x-spt"},
                    {".src", "application/x-wais-source"},
                    {".stk", "application/hyperstudio"},
                    {".stm", "audio/x-mod"},
                    {".sv4cpio", "application/x-sv4cpio"},
                    {".sv4crc", "application/x-sv4crc"},
                    {".svf", "image/vnd"},
                    {".svg", "image/svg-xml"},
                    {".svh", "image/svh"},
                    {".svr", "x-world/x-svr"},
                    {".swf", "application/x-shockwave-flash"},
                    {".swfl", "application/x-shockwave-flash"},
                    {".t", "application/x-troff"},
                    {".tad", "application/octet-stream"},
                    {".talk", "text/x-speech"},
                    {".tar", "application/x-tar"},
                    {".taz", "application/x-tar"},
                    {".tbp", "application/x-timbuktu"},
                    {".tbt", "application/x-timbuktu"},
                    {".tcl", "application/x-tcl"},
                    {".tex", "application/x-tex"},
                    {".texi", "application/x-texinfo"},
                    {".texinfo", "application/x-texinfo"},
                    {".tgz", "application/x-tar"},
                    {".thm", "application/vnd.eri.thm"},
                    {".tif", "image/tiff"},
                    {".tiff", "image/tiff"},
                    {".tki", "application/x-tkined"},
                    {".tkined", "application/x-tkined"},
                    {".toc", "application/toc"},
                    {".toy", "image/toy"},
                    {".tr", "application/x-troff"},
                    {".trk", "x-lml/x-gps"},
                    {".trm", "application/x-msterminal"},
                    {".tsi", "audio/tsplayer"},
                    {".tsp", "application/dsptype"},
                    {".tsv", "text/tab-separated-values"},
                    {".ttf", "application/octet-stream"},
                    {".ttz", "application/t-time"},
                    {".txt", "text/plain"},
                    {".ult", "audio/x-mod"},
                    {".ustar", "application/x-ustar"},
                    {".uu", "application/x-uuencode"},
                    {".uue", "application/x-uuencode"},
                    {".vcd", "application/x-cdlink"},
                    {".vcf", "text/x-vcard"},
                    {".vdo", "video/vdo"},
                    {".vib", "audio/vib"},
                    {".viv", "video/vivo"},
                    {".vivo", "video/vivo"},
                    {".vmd", "application/vocaltec-media-desc"},
                    {".vmf", "application/vocaltec-media-file"},
                    {".vmi", "application/x-dreamcast-vms-info"},
                    {".vms", "application/x-dreamcast-vms"},
                    {".vox", "audio/voxware"},
                    {".vqe", "audio/x-twinvq-plugin"},
                    {".vqf", "audio/x-twinvq"},
                    {".vql", "audio/x-twinvq"},
                    {".vre", "x-world/x-vream"},
                    {".vrml", "x-world/x-vrml"},
                    {".vrt", "x-world/x-vrt"},
                    {".vrw", "x-world/x-vream"},
                    {".vts", "workbook/formulaone"},
                    {".wax", "audio/x-ms-wax"},
                    {".wbmp", "image/vnd.wap.wbmp"},
                    {".web", "application/vnd.xara"},
                    {".wav", "audio/x-wav"},
                    {".wma", "audio/x-ms-wma"},
                    {".wmv", "audio/x-ms-wmv"},
                    {".wi", "image/wavelet"},
                    {".wis", "application/x-InstallShield"},
                    {".wm", "video/x-ms-wm"},
                    {".wmd", "application/x-ms-wmd"},
                    {".wmf", "application/x-msmetafile"},
                    {".wml", "text/vnd.wap.wml"},
                    {".wmlc", "application/vnd.wap.wmlc"},
                    {".wmls", "text/vnd.wap.wmlscript"},
                    {".wmlsc", "application/vnd.wap.wmlscriptc"},
                    {".wmlscript", "text/vnd.wap.wmlscript"},
                    {".wmv", "video/x-ms-wmv"},
                    {".wmx", "video/x-ms-wmx"},
                    {".wmz", "application/x-ms-wmz"},
                    {".wpng", "image/x-up-wpng"},
                    {".wps", "application/vnd.ms-works"},
                    {".wpt", "x-lml/x-gps"},
                    {".wri", "application/x-mswrite"},
                    {".wrl", "x-world/x-vrml"},
                    {".wrz", "x-world/x-vrml"},
                    {".ws", "text/vnd.wap.wmlscript"},
                    {".wsc", "application/vnd.wap.wmlscriptc"},
                    {".wv", "video/wavelet"},
                    {".wvx", "video/x-ms-wvx"},
                    {".wxl", "application/x-wxl"},
                    {".x-gzip", "application/x-gzip"},
                    {".xar", "application/vnd.xara"},
                    {".xbm", "image/x-xbitmap"},
                    {".xdm", "application/x-xdma"},
                    {".xdma", "application/x-xdma"},
                    {".xdw", "application/vnd.fujixerox.docuworks"},
                    {".xht", "application/xhtml+xml"},
                    {".xhtm", "application/xhtml+xml"},
                    {".xhtml", "application/xhtml+xml"},
                    {".xla", "application/vnd.ms-excel"},
                    {".xlc", "application/vnd.ms-excel"},
                    {".xll", "application/x-excel"},
                    {".xlm", "application/vnd.ms-excel"},
                    {".xls", "application/vnd.ms-excel"},
                    {".xlsx", "application/vnd.ms-excel"},
                    {".xlt", "application/vnd.ms-excel"},
                    {".xlw", "application/vnd.ms-excel"},
                    {".xm", "audio/x-mod"},
                    {".xml", "text/xml"},
                    {".xmz", "audio/x-mod"},
                    {".xpi", "application/x-xpinstall"},
                    {".xpm", "image/x-xpixmap"},
                    {".xsit", "text/xml"},
                    {".xsl", "text/xml"},
                    {".xul", "text/xul"},
                    {".xwd", "image/x-xwindowdump"},
                    {".xyz", "chemical/x-pdb"},
                    {".yz1", "application/x-yz1"},
                    {".z", "application/x-compress"},
                    {".zac", "application/x-zaurus-zac"},
                    {".zip", "application/zip"},
                    {"", "*/*"}
            };

    /**
     * 获取文件类型
     */
    private static String getMimeType(String filePath) {
        String type = "*/*";
        String fName = filePath;

        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }

        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") {
            return type;
        }

        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
            }
        }
        return type;
    }

}
