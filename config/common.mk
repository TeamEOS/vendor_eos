PRODUCT_BRAND ?= EOS

PRODUCT_BUILD_PROP_OVERRIDES += BUILD_UTC_DATE=0

PRODUCT_PROPERTY_OVERRIDES += \
    keyguard.no_require_sim=true \
    ro.url.legal=http://www.google.com/intl/%s/mobile/android/basic/phone-legal.html \
    ro.url.legal.android_privacy=http://www.google.com/intl/%s/mobile/android/basic/privacy.html \
    ro.com.google.clientidbase=android-google \
    ro.com.android.wifi-watchlist=GoogleGuest \
    ro.setupwizard.enterprise_mode=1 \
    ro.com.android.dateformat=MM-dd-yyyy \
    ro.com.android.dataroaming=false

# Disable ADB authentication
ADDITIONAL_DEFAULT_PROPERTIES += ro.adb.secure=0

# Thank you, please drive thru!
PRODUCT_PROPERTY_OVERRIDES += persist.sys.dun.override=0

# Backup Tool
PRODUCT_COPY_FILES += \
	vendor/eos/prebuilt/common/bin/backuptool.sh:system/bin/backuptool.sh \
	vendor/eos/prebuilt/common/bin/backuptool.functions:system/bin/backuptool.functions \
	vendor/eos/prebuilt/common/bin/50-hosts.sh:system/addon.d/50-hosts.sh \
	vendor/eos/prebuilt/common/bin/99-supersu.sh:system/addon.d/99-supersu.sh \
	vendor/eos/prebuilt/common/bin/blacklist:system/addon.d/blacklist

# init.d support
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/init.d/00banner:system/etc/init.d/00banner \
    vendor/eos/prebuilt/common/etc/init.d/01fastcharge:system/etc/init.d/01fastcharge \
    vendor/eos/prebuilt/common/etc/init.d/02oppo:system/etc/init.d/02oppo \
    vendor/eos/prebuilt/common/bin/sysinit:system/bin/sysinit

# userinit support
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/init.d/90userinit:system/etc/init.d/90userinit

# eos-specific init file
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/init.eos.rc:root/init.eos.rc

# Compcache/Zram support
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/bin/compcache:system/bin/compcache \
    vendor/eos/prebuilt/common/bin/handle_compcache:system/bin/handle_compcache

# mounts
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/bin/sysrw:system/bin/sysrw \
    vendor/eos/prebuilt/common/bin/sysro:system/bin/sysro \
    vendor/eos/prebuilt/common/bin/rootrw:system/bin/rootrw \
    vendor/eos/prebuilt/common/bin/rootro:system/bin/rootro

# swap support
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/bin/handle_swap:system/bin/handle_swap

# Nam configuration script
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/bin/modelid_cfg.sh:system/bin/modelid_cfg.sh

# Bring in camera effects
PRODUCT_COPY_FILES +=  \
    vendor/eos/prebuilt/common/media/LMprec_508.emd:system/media/LMprec_508.emd \
    vendor/eos/prebuilt/common/media/PFFprec_600.emd:system/media/PFFprec_600.emd

# Enable SIP+VoIP on all targets
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.software.sip.voip.xml:system/etc/permissions/android.software.sip.voip.xml

# Don't export PS1 in /system/etc/mkshrc.
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/mkshrc:system/etc/mkshrc

# PlayStation
PRODUCT_PACKAGES += \
    com.playstation.playstationcertified

PRODUCT_COPY_FILES +=  \
    vendor/eos/prebuilt/common/etc/permissions/com.playstation.playstationcertified.xml:system/etc/permissions/com.playstation.playstationcertified.xml

# Supersu support
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/init.d/99SuperSUDaemon:system/etc/init.d/99SuperSUDaemon \
    vendor/eos/prebuilt/common/xbin/daemonsu:system/xbin/daemonsu \
    vendor/eos/prebuilt/common/xbin/su:system/xbin/su
    
# Required eos packages
PRODUCT_PACKAGES += \
    BluetoothExt \
    Camera \
    EOSTools \
    EOSWeather \
    DSPManager\
    Email \
    LatinIME \
    Launcher2 \
    libcyanogen-dsp

# Optional eos packages
PRODUCT_PACKAGES += \
    audio_effects.conf \
    Basic \
    Music \
    VideoEditor \
    VoiceDialer \
    SoundRecorder

# Extra tools in eos
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
    mount.exfat \
    fsck.exfat \
    mkfs.exfat \
    ntfsfix \
    ntfs-3g \
    systembinsh \
    libcurl \
    curl

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

# emoji
PRODUCT_PACKAGES += \
    libemoji

# msim video tele
PRODUCT_PACKAGES += \
    libimscamera_jni \
    libvt_jni

# GDX gfx and perfomance suite native library
PRODUCT_PACKAGES += \
    libgdx

PRODUCT_PACKAGE_OVERLAYS += vendor/eos/overlay/dictionaries
PRODUCT_PACKAGE_OVERLAYS += vendor/eos/overlay/common

# Set valid modversion
PRODUCT_PROPERTY_OVERRIDES += ro.modversion=$(BUILD_NUMBER)

# T-Mobile Theme Engine
$(call inherit-product, vendor/eos/config/themes_common.mk)

# Open Source prebuilts
$(call inherit-product, vendor/eos/config/prebuilt_apps_common.mk)
