LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := SignCheck
LOCAL_SRC_FILES := SignCheck.cpp
include $(BUILD_SHARED_LIBRARY)