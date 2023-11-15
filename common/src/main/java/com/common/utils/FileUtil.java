package com.common.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
}
