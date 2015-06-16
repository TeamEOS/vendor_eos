LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_SRC_FILES := org.teameos.navigation.jar
LOCAL_MODULE_TAGS := optional          
LOCAL_MODULE := org.teameos.navigation
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX := $(COMMON_JAVA_PACKAGE_SUFFIX)
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    org.teameos.navigation-static:org.teameos.navigation-static.jar \
    trail-drawing:trail-core-lib-1.0.4-SNAPSHOT.jar

include $(BUILD_MULTI_PREBUILT)

