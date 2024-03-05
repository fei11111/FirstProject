//
// Created by huangjf on 2024/3/5.
//

#ifndef FIRSTPROJECT_DZOPENSLES_H

#define FIRSTPROJECT_DZOPENSLES_H

#include "SLES/OpenSLES.h"
#include "SLES/OpenSLES_Android.h"


class DZOpensles {
private:
    SLPlayItf pPlayItf = NULL;
public:
    DZOpensles();

    ~DZOpensles();

    void play(void *pContext);

    void pause();

    void stop();

    void release();

};


#endif //FIRSTPROJECT_DZOPENSLES_H
