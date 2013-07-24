LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := SmallSystemStub
LOCAL_OVERRIDES_PACKAGES := \
    HoloSpiralWallpaper \
    MagicSmokeWallpapers \
    NoiseField \
    Galaxy4 \
    VisualizationWallpapers \
    PhaseBeam

LOCAL_OVERRIDES_PACKAGES += \
    LiveWallpapers \
    LiveWallpapersPicker \
    Calculator \
    Calendar \
    CMFileManager \
    DeskClock \
    Apollo \
    Basic \
    VideoEditor \
    VoiceDialer \
    SoundRecorder \
    ZeroXBenchmark \
    SpareParts \
    LockClock \
    Development \
    Email \
    WAPPushManager \
    MusicFX \
    Email2 \
    Exchange2 \
    QuickSearchBox

LOCAL_OVERRIDES_PACKAGES += \
    PinyinIME \
    OpenWnn \
    libWnnEngDic \
    libWnnJpnDic \
    libwnndict \
    libfwdlockengine

# rsync
LOCAL_OVERRIDES_PACKAGES += \
    rsync

LOCAL_SDK_VERSION := current

include $(BUILD_PACKAGE)
