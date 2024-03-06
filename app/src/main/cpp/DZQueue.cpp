//
// Created by huangjf on 2024/3/1.
//
#include "DZQueue.h"

template<>
DZQueue<AVFrame *>::DZQueue() {
    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init(&cond, NULL);
}

template<>
DZQueue<AVFrame *>::~DZQueue() {
    pthread_mutex_destroy(&mutex);
    pthread_cond_destroy(&cond);
}

template<>
AVFrame *DZQueue<AVFrame *>::pop() {
    pthread_mutex_lock(&mutex);
    while (queue.empty()) {
        pthread_cond_wait(&cond, &mutex);
    }
    AVFrame *pData = queue.front();

    queue.pop();
    if (queue.size() == 20 && !isExit) {
        pthread_cond_signal(&cond);
    }
    pthread_mutex_unlock(&mutex);
    return pData;
}

template<>
bool DZQueue<AVFrame *>::push(AVFrame *item) {
    if (isExit) return false;
    pthread_mutex_lock(&mutex);
    while (queue.size() >= 100 && !isExit) {
        pthread_cond_wait(&cond, &mutex);
    }
    if (!isExit) {
        queue.push(item);
        pthread_cond_signal(&cond);
    }
    pthread_mutex_unlock(&mutex);
    return true;
}

template<>
bool DZQueue<AVFrame *>::isEmpty() {
    pthread_mutex_lock(&mutex);
    bool result = queue.empty();
    pthread_mutex_unlock(&mutex);
    return result;
}

template<>
int DZQueue<AVFrame *>::size() {
    pthread_mutex_lock(&mutex);
    int result = queue.size();
    pthread_mutex_unlock(&mutex);
    return result;
}

template<>
void DZQueue<AVFrame *>::clear() {
    isExit = true;
    pthread_cond_signal(&cond);
    pthread_mutex_lock(&mutex);
    while (!queue.empty()) {
        AVFrame *frame = queue.front();
        queue.pop();
        if (frame != NULL) {
            av_frame_unref(frame);
            av_frame_free(&frame);
            frame = NULL;
        }
    }
    pthread_mutex_unlock(&mutex);
}
