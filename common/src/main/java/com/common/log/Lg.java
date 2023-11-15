package com.common.log;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

import java.io.File;

public final class Lg {

    private static String defaultDir;// log默认存储目录
    private static String dir;       // log存储目录
    private static String cacheDir;//log缓存目录

    private static boolean sLogSwitch = true;   // log总开关，默认开
    private static boolean sLog2ConsoleSwitch = true;   // logcat是否打印，默认打印
    private static String sGlobalTag = "XLog";   // log标签
    private static int sLogLevel = Log.getLogLevel();
    private static String sFilePrefix = "deep";// log文件前缀
    private static int sCacheDays = 0;//log文件默认缓存天数

    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String LINE_SEP = System.getProperty("line.separator");

    private volatile static Lg sLg;

    private Lg(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && context.getExternalCacheDir() != null)
            defaultDir = context.getExternalCacheDir() + FILE_SEP + "log" + FILE_SEP;
        else {
            defaultDir = context.getFilesDir() + FILE_SEP + "log" + FILE_SEP;
        }
        cacheDir = context.getFilesDir() + FILE_SEP + "log" + FILE_SEP;
    }

    public static Lg config(Context context) {
        if (sLg == null) {
            synchronized (Lg.class) {
                if (sLg == null) {
                    sLg = new Lg(context);
                }
            }
        }
        return sLg;
    }

    public Lg setLogSwitch(boolean logSwitch) {
        Lg.sLogSwitch = logSwitch;
        return this;
    }

    public Lg setConsoleSwitch(boolean consoleSwitch) {
        Lg.sLog2ConsoleSwitch = consoleSwitch;
        return this;
    }

    public Lg setGlobalTag(final String tag) {
        if (isSpace(tag)) {
            Lg.sGlobalTag = "";
        } else {
            Lg.sGlobalTag = tag;
        }
        return this;
    }

    public Lg setDir(final String dir) {
        if (isSpace(dir)) {
            Lg.dir = null;
        } else {
            Lg.dir = dir.endsWith(FILE_SEP) ? dir : dir + FILE_SEP;
        }
        return this;
    }

    public Lg setDir(final File dir) {
        Lg.dir = dir == null ? null : dir.getAbsolutePath() + FILE_SEP;
        return this;
    }

    public Lg setFilePrefix(String filePrefix) {
        if (!isSpace(filePrefix)) Lg.sFilePrefix = filePrefix;
        return this;
    }

    public Lg setLogLevel(int logLevel) {
        sLogLevel = logLevel;
        return this;
    }

    public Lg setCacheDays(int cacheDays) {
        Lg.sCacheDays = cacheDays;
        return this;
    }

    public void build() {
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");
        if (sLogSwitch) {
            Xlog.setMaxAliveTime(sCacheDays * 24 * 60 * 60);
            Xlog.appenderOpen(sLogLevel, Xlog.AppednerModeAsync, cacheDir, dir == null ? defaultDir : dir, sFilePrefix, 0, "");
            Xlog.setConsoleLogOpen(sLog2ConsoleSwitch);
            Log.setLogImp(new Xlog());
            Log.setLevel(sLogLevel, true);
        } else {
            Log.setLevel(Log.LEVEL_NONE, false);
        }
    }

    public static String getConfig() {
        return "switch: " + sLogSwitch
                + LINE_SEP + "console: " + sLog2ConsoleSwitch
                + LINE_SEP + "tag: " + sGlobalTag
                + LINE_SEP + "dir: " + (dir == null ? defaultDir : dir)
                + LINE_SEP + "prefix: " + sFilePrefix
                + LINE_SEP + "logLevel: " + sLogLevel
                + LINE_SEP + "cacheDays: " + sCacheDays;
    }


    private static boolean isSpace(String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * use f(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void f(final String tag, final String msg) {
        Log.f(tag, msg, (Object[]) null);
    }

    public static void f(final String msg) {
        f(sGlobalTag, msg);
    }

    /**
     * use e(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void e(final String tag, final String msg) {
        Log.f(tag, msg, (Object[]) null);
    }

    public static void e(final String msg) {
        e(sGlobalTag, msg);
    }

    /**
     * use w(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void w(final String tag, final String msg) {
        Log.w(tag, msg, (Object[]) null);
    }

    public static void w(final String msg) {
        w(sGlobalTag, msg);
    }

    /**
     * use i(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void i(final String tag, final String msg) {
        Log.i(tag, msg, (Object[]) null);
    }

    public static void i(final String msg) {
        i(sGlobalTag, msg);
    }

    /**
     * use d(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void d(final String tag, final String msg) {
        Log.d(tag, msg, (Object[]) null);
    }

    public static void d(final String msg) {
        d(sGlobalTag, msg);
    }

    /**
     * use v(tag, format, obj) instead
     *
     * @param tag
     * @param msg
     */
    public static void v(final String tag, final String msg) {
        Log.v(tag, msg, (Object[]) null);
    }

    public static void v(final String msg) {
        v(sGlobalTag, msg);
    }

    /**
     * Uninitialized xlog before your app exits
     */
    public static void logClose() {
        Log.appenderClose();
    }

    /**
     * flush she cache log
     * @param isSync
     */
    public static void logAppenderFlush(boolean isSync) {
        Log.appenderFlush(isSync);
    }

    /**
     * print the detailed error log
     * @param tag
     * @param throwable
     */
    public static void e(final String tag, @NonNull Throwable throwable) {
        String msg = throwable.getMessage();
        StringBuilder sb = new StringBuilder(msg == null ? "" : msg);
        StackTraceElement[] elements = throwable.getStackTrace();
        if (elements != null) {
            for (StackTraceElement element : elements) {
                sb.append("\n");
                for (int i = 0; i < 22 + tag.length(); i++) {
                    sb.append(" ");
                }
                sb.append(element.toString());
            }
        }
        Log.e(tag,sb.toString());
    }


    public static void e(@NonNull Throwable throwable) {
        e(sGlobalTag,throwable);
    }
}