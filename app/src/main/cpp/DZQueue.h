//
// Created by huangjf on 2024/2/29.
//

#ifndef FIRSTPROJECT_DZQUEUE_H
#define FIRSTPROJECT_DZQUEUE_H


#include "pthread.h"
#include "queue"
#include "DZConstDefine.h"

extern "C" {
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
}

template<typename T>
class DZQueue {
private:
    std::queue<T> *queue;
    pthread_mutex_t mutex;
    pthread_cond_t cond;
public:
    DZQueue();

    ~DZQueue();

    T pop();

    bool push(T item);

};

template<typename T>
DZQueue<T>::DZQueue() {
    queue = new std::queue<T>();
    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init(&cond, NULL);
}

template<typename T>
DZQueue<T>::~DZQueue() {

}

template<typename T>
T DZQueue<T>::pop() {
    pthread_mutex_lock(&mutex);
    while (queue->empty()) {
        pthread_cond_wait(&cond, &mutex);
    }
    T pData = queue->front();

    queue->pop();
    if (queue->size() == 20) {
        pthread_cond_signal(&cond);
    }
    pthread_mutex_unlock(&mutex);

    return pData;
}

template<typename T>
bool DZQueue<T>::push(T item) {
    pthread_mutex_lock(&mutex);
    while (queue->size() >= 100) {
        pthread_cond_wait(&cond, &mutex);
    }
    queue->push(item);
    pthread_cond_signal(&cond);
    pthread_mutex_unlock(&mutex);
    return true;
}


#endif //FIRSTPROJECT_DZQUEUE_H
