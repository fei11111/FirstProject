package com.fei.firstproject.download.inter;

/**
 * Created by Fei on 2018/1/4.
 */

public interface ProgressListener {

    /**
     * @param progress 已经下载或上传字节数
     * @param total    总字节数
     * @param done     是否完成
     */
    void onProgress(long progress, long total, boolean done);

}
