PRODUCT_BRAND ?= codefireX

PRODUCT_BUILD_PROP_OVERRIDES += BUILD_UTC_DATE=0

SUPERUSER_EMBEDDED := true

PRODUCT_PROPERTY_OVERRIDES += \
    keyguard.no_require_sim=true \
    ro.url.legal=http://www.google.com/intl/%s/mobile/android/basic/phone-legal.html \
    ro.url.legal.android_privacy=http://www.google.com/intl/%s/mobile/android/basic/privacy.html \
    ro.com.google.clientidbase=android-google \
    ro.com.android.wifi-watchlist=GoogleGuest \
    ro.setupwizard.enterprise_mode=1 \
    ro.com.android.dateformat=MM-dd-yyyy \
    ro.com.android.dataroaming=false

# Proprietary LatinIME Gesture Support
PRODUCT_COPY_FILES += \
    vendor/cm/prebuilt/common/lib/libjni_latinime.so:obj/lib/libjni_latinime.so \
    vendor/cm/prebuilt/common/lib/libjni_latinime.so:system/lib/libjni_latinime.so

# Backup Tool
PRODUCT_COPY_FILES += \
	vendor/cm/prebuilt/common/bin/backuptool.sh:system/bin/backuptool.sh \
	vendor/cm/prebuilt/common/bin/backuptool.functions:system/bin/backuptool.functions \
	vendor/cm/prebuilt/common/bin/50-hosts.sh:system/addon.d/50-hosts.sh \
	vendor/cm/prebuilt/common/bin/blacklist:system/addon.d/blacklist

# init.d support
PRODUCT_COPY_FILES += \
    vendor/cm/prebuilt/common/etc/init.d/00banner:system/etc/init.d/00banner \
    vendor/cm/prebuilt/common/bin/sysinit:system/bin/sysinit

# userinit support
PRODUCT_COPY_FILES += \
    vendor/cm/prebuilt/common/etc/init.d/90userinit:system/etc/init.d/90userinit

# cfX-specific init file
PRODUCT_COPY_FILES += \
    vendor/cm/prebuilt/common/etc/init.local.rc:root/init.cfx.rc

# Compcache/Zram support
PRODUCT_COPY_FILES += \
    vendor/cm/prebuilt/common/bin/compcache:system/bin/compcache \
    vendor/cm/prebuilt/common/bin/handle_compcache:system/bin/handle_compcache

# swap support
PRODUCT_COPY_FILES += \
    vendor/cm/prebuilt/common/bin/handle_swap:system/bin/handle_swap

# Nam configuration script
PRODUCT_COPY_FILES += \
    vendor/cm/prebuilt/common/bin/modelid_cfg.sh:system/bin/modelid_cfg.sh

# Bring in camera effects
PRODUCT_COPY_FILES +=  \
    vendor/cm/prebuilt/common/media/LMprec_508.emd:system/media/LMprec_508.emd \
    vendor/cm/prebuilt/common/media/PFFprec_600.emd:system/media/PFFprec_600.emd

# Enable SIP+VoIP on all targets
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.software.sip.voip.xml:system/etc/permissions/android.software.sip.voip.xml

PRODUCT_COPY_FILES += \
    vendor/cm/config/permissions/com.cyanogenmod.android.xml:system/etc/permissions/com.cyanogenmod.android.xml

# Don't export PS1 in /system/etc/mkshrc.
PRODUCT_COPY_FILES += \
    vendor/cm/prebuilt/common/etc/mkshrc:system/etc/mkshrc

# Required cfX packages
PRODUCT_PACKAGES += \
    Camera \
    CFXSettings \
    Development \
    Email \
    LatinIME \
    Launcher2 \
    LockClock \
    SpareParts \
    Superuser \
    su

# Optional cfX packages
PRODUCT_PACKAGES += \
    audio_effects.conf \
    Apollo \
    Basic \
    CMFileManager \
    VideoEditor \
    VoiceDialer \
    SoundRecorder \
    ZeroXBenchmark \

# Theme Chooser
PRODUCT_PACKAGES += \
    ThemeManager \
    ThemeChooser \
    com.tmobile.themes

# Extra tools in cfX
PRODUCT_PACKAGES += \
    openvpn \
    e2fsck \
    mke2fs \
    tune2fs \
    bash \
    vim \
    nano \
    htop \
    powertop \
    lsof \
    systembinsh

# Openssh
PRODUCT_PACKAGES += \
    scp \
    sftp \
    ssh \
    sshd \
    sshd_config \
    ssh-keygen \
    start-ssh

# rsync
PRODUCT_PACKAGES += \
    rsync

PRODUCT_PACKAGE_OVERLAYS += vendor/cm/overlay/dictionaries
PRODUCT_PACKAGE_OVERLAYS += vendor/cm/overlay/common

PRODUCT_VERSION_MAJOR = 4.2.2
#PRODUCT_VERSION_MINOR = SR2
PRODUCT_VERSION_MINOR = $(shell date -u +%Y%m%d)
PRODUCT_VERSION_MAINTENANCE = 0-RC0

CFX_VERSION := $(CFX_BUILD)-$(PRODUCT_VERSION_MAJOR)-$(PRODUCT_VERSION_MINOR)

PRODUCT_PROPERTY_OVERRIDES += \
  ro.modversion=cfx_$(CFX_VERSION)
