LOCAL_PATH:=$(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := com.playstation.playstationcertified
LOCAL_SRC_FILES := com.playstation.playstationcertified.jar
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_SUFFIX := .jar
include $(BUILD_PREBUILT)
