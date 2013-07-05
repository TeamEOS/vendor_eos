PRODUCT_BRAND ?= codefireX

PRODUCT_BUILD_PROP_OVERRIDES += BUILD_UTC_DATE=0

SUPERUSER_EMBEDDED := true
SUPERUSER_PACKAGE_PREFIX := com.android.settings.cyanogenmod.superuser

PRODUCT_PROPERTY_OVERRIDES += \
    keyguard.no_require_sim=true \
    ro.url.legal=http://www.google.com/intl/%s/mobile/android/basic/phone-legal.html \
    ro.url.legal.android_privacy=http://www.google.com/intl/%s/mobile/android/basic/privacy.html \
    ro.com.google.clientidbase=android-google \
    ro.com.android.wifi-watchlist=GoogleGuest \
    ro.setupwizard.enterprise_mode=1 \
    ro.com.android.dateformat=MM-dd-yyyy \
    ro.com.android.dataroaming=false

# cfX File Contexts
ifeq ($(HAVE_SELINUX),true)
PRODUCT_COPY_FILES += \
    vendor/cfx/prebuilt/common/root/file_contexts:root/file_contexts

endif

# Disable ADB authentication
ADDITIONAL_DEFAULT_PROPERTIES += ro.adb.secure=0

# Proprietary LatinIME Gesture Support
PRODUCT_COPY_FILES += \
    vendor/cfx/prebuilt/common/lib/libjni_latinime.so:obj/lib/libjni_latinime.so \
    vendor/cfx/prebuilt/common/lib/libjni_latinime.so:system/lib/libjni_latinime.so \
    vendor/cfx/prebuilt/common/lib/libjni_latinime.so:system/lib/libjni_latinimegoogle.so

# Backup Tool
PRODUCT_COPY_FILES += \
	vendor/cfx/prebuilt/common/bin/backuptool.sh:system/bin/backuptool.sh \
	vendor/cfx/prebuilt/common/bin/backuptool.functions:system/bin/backuptool.functions \
	vendor/cfx/prebuilt/common/bin/50-hosts.sh:system/addon.d/50-hosts.sh \
	vendor/cfx/prebuilt/common/bin/blacklist:system/addon.d/blacklist

# init.d support
PRODUCT_COPY_FILES += \
    vendor/cfx/prebuilt/common/etc/init.d/00banner:system/etc/init.d/00banner \
    vendor/cfx/prebuilt/common/bin/sysinit:system/bin/sysinit

# userinit support
PRODUCT_COPY_FILES += \
    vendor/cfx/prebuilt/common/etc/init.d/90userinit:system/etc/init.d/90userinit

# cfX-specific init file
PRODUCT_COPY_FILES += \
    vendor/cfx/prebuilt/common/etc/init.cfx.rc:root/init.cfx.rc

# Don't copy memory tweaks on low ram devices (<786M)
ifneq ($(TARGET_IS_LOW_RAM),true)
PRODUCT_COPY_FILES += \
    vendor/cfx/prebuilt/common/etc/init.memory.rc:root/init.memory.rc
endif

# Compcache/Zram support
PRODUCT_COPY_FILES += \
    vendor/cfx/prebuilt/common/bin/compcache:system/bin/compcache \
    vendor/cfx/prebuilt/common/bin/handle_compcache:system/bin/handle_compcache

# mounts
PRODUCT_COPY_FILES += \
    vendor/cfx/prebuilt/common/bin/sysrw:system/bin/sysrw \
    vendor/cfx/prebuilt/common/bin/sysro:system/bin/sysro \
    vendor/cfx/prebuilt/common/bin/rootrw:system/bin/rootrw \
    vendor/cfx/prebuilt/common/bin/rootro:system/bin/rootro

# swap support
PRODUCT_COPY_FILES += \
    vendor/cfx/prebuilt/common/bin/handle_swap:system/bin/handle_swap

# Nam configuration script
PRODUCT_COPY_FILES += \
    vendor/cfx/prebuilt/common/bin/modelid_cfg.sh:system/bin/modelid_cfg.sh

# Bring in camera effects
PRODUCT_COPY_FILES +=  \
    vendor/cfx/prebuilt/common/media/LMprec_508.emd:system/media/LMprec_508.emd \
    vendor/cfx/prebuilt/common/media/PFFprec_600.emd:system/media/PFFprec_600.emd

# Enable SIP+VoIP on all targets
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.software.sip.voip.xml:system/etc/permissions/android.software.sip.voip.xml

# Don't export PS1 in /system/etc/mkshrc.
PRODUCT_COPY_FILES += \
    vendor/cfx/prebuilt/common/etc/mkshrc:system/etc/mkshrc

# Required cfX packages
PRODUCT_PACKAGES += \
    Camera \
    CFXSettings \
    Development \
    Email \
    LatinIME \
    Launcher2 \
    LockClock \
    PermissionsManager \
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

PRODUCT_PACKAGE_OVERLAYS += vendor/cfx/overlay/dictionaries
PRODUCT_PACKAGE_OVERLAYS += vendor/cfx/overlay/common
