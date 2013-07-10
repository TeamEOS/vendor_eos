LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := \
    libroottools \
    libroottoolsdoc \
    android-support-v4 \
    android-support-v13

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := CFXSettings
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := libroottools:libs/RootTools-2.6.jar libroottoolsdoc:libs/RootTools-javadoc-2.6.jar

include $(BUILD_MULTI_PREBUILT)
