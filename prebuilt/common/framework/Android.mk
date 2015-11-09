LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    org.teameos.navigation-static:org.teameos.navigation-static.jar

include $(BUILD_MULTI_PREBUILT)

