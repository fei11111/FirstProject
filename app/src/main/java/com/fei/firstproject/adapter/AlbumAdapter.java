package com.fei.firstproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.common.utils.FileUtil;
import com.fei.firstproject.R;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AlbumAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private Context mContext;
    private List<Uri> list;
    private GridView gridView;
    private LruCache<String, Bitmap> mLruCache;
    private List<ImageLoadAsyTask> tasks;
    private int firstVisibleItem;
    private int visibleItemCount;
    private boolean isFirstEnter = true;

    public AlbumAdapter(Context mContext, List<Uri> list, GridView gridView) {
        this.mContext = mContext;
        this.list = list;
        this.gridView = gridView;

        init();
    }

    private void init() {
        tasks = new CopyOnWriteArrayList<>();
        gridView.setOnScrollListener(this);
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 8);
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    private void stopTasks() {
        for (ImageLoadAsyTask task : tasks) {
            if (task != null) {
                task.cancel(true);
                tasks.remove(task);
            }
        }
    }

    private Bitmap getBitmapFromCache(String key) {
        return mLruCache.get(key);
    }

    private void addBitmapToCache(String key, Bitmap bitmap) {
        mLruCache.put(key, bitmap);
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_album, null);
            viewHolder = new ViewHolder();
            viewHolder.ivAlbum = convertView.findViewById(R.id.iv_album);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Uri uri = list.get(position);
        Bitmap bitmap = getBitmapFromCache(uri.toString());
        viewHolder.ivAlbum.setTag(uri.toString());
        if (bitmap != null) {
            viewHolder.ivAlbum.setImageBitmap(bitmap);
        } else {
            viewHolder.ivAlbum.setImageResource(R.drawable.ic_default);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView ivAlbum;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        LogUtils.i("AlbumAdapter", "onScrollStateChanged");
        if (scrollState == SCROLL_STATE_IDLE) {
            loadBitmap();
        } else {
            stopTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
        if (isFirstEnter && visibleItemCount > 0) {
            isFirstEnter = false;
            loadBitmap();
        }
        LogUtils.i("AlbumAdapter", "onScroll");
    }

    private void loadBitmap() {
        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            Uri uri = list.get(i);
            Bitmap bitmap = getBitmapFromCache(uri.toString());
            if (bitmap == null) {
                ImageLoadAsyTask task = new ImageLoadAsyTask();
                tasks.add(task);
                task.execute(uri);
            } else {
                ImageView imageView = gridView.findViewWithTag(uri.toString());
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    class ImageLoadAsyTask extends AsyncTask<Uri, String, Bitmap> {

        private Uri uri;

        @Override
        protected Bitmap doInBackground(Uri... strings) {
            uri = strings[0];
            if (isCancelled())
                return null;
            return compressBitmap(uri);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            tasks.remove(this);
            if (isCancelled()) return;
            if (bitmap != null) {
                ImageView imageView = gridView.findViewWithTag(uri.toString());
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    addBitmapToCache(uri.toString(), bitmap);
                }
            }
        }
    }

    private Bitmap compressBitmap(Uri uri) {
        int[] screen = Utils.getScreen((Activity) mContext);
        int screenWidth = screen[0];
        int imageWidth = screenWidth / 3;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
           FileUtil.getBitmapFromUri(mContext, uri, null, options);
            int outWidth = options.outWidth;
            int outHeight = options.outHeight;
            if (outWidth > outHeight) {
                options.inSampleSize = outWidth / imageWidth;
            } else {
                options.inSampleSize = outHeight / imageWidth;
            }
            options.outWidth = imageWidth;
            options.outHeight = imageWidth;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = false;
            return FileUtil.getBitmapFromUri(mContext, uri, null, options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
