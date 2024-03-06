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
public:
    std::queue<T> queue;
    pthread_mutex_t mutex;
    pthread_cond_t cond;
    bool isExit = false;
public:
    DZQueue();

    ~DZQueue();

    T pop();

    bool push(T item);

    bool isEmpty();

    int size();

    void clear();

};




#endif //FIRSTPROJECT_DZQUEUE_H
