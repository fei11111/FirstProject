package com.fei.firstproject.jniTest;

public class SignCheck {

    static {
        System.loadLibrary("SignCheck");
    }

    public static native boolean isRight(Object contextObject);

}
