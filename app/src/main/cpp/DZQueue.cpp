//
// Created by huangjf on 2024/3/1.
//
#include "DZQueue.h"

template<>
DZQueue<AVFrame*>::DZQueue() {
    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init(&cond, NULL);
}

template<>
DZQueue<AVFrame*>::~DZQueue() {
}

template<>
AVFrame* DZQueue<AVFrame*>::pop() {
    pthread_mutex_lock(&mutex);
    while (queue.empty()) {
        pthread_cond_wait(&cond, &mutex);
    }
    AVFrame* pData = queue.front();

    queue.pop();
    if (queue.size() == 20) {
        pthread_cond_signal(&cond);
    }
    pthread_mutex_unlock(&mutex);

    return pData;
}

template<>
bool DZQueue<AVFrame*>::push(AVFrame* item) {
    pthread_mutex_lock(&mutex);
    while (queue.size() >= 100) {
        pthread_cond_wait(&cond, &mutex);
    }
    queue.push(item);
    pthread_cond_signal(&cond);
    pthread_mutex_unlock(&mutex);
    return true;
}

template<>
bool DZQueue<AVFrame*>::isEmpty() {
    return queue.empty();
}