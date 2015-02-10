PRODUCT_BRAND ?= EOS

PRODUCT_BUILD_PROP_OVERRIDES += BUILD_UTC_DATE=0

ifeq ($(PRODUCT_GMS_CLIENTID_BASE),)
PRODUCT_PROPERTY_OVERRIDES += \
    ro.com.google.clientidbase=android-google
else
PRODUCT_PROPERTY_OVERRIDES += \
    ro.com.google.clientidbase=$(PRODUCT_GMS_CLIENTID_BASE)
endif

PRODUCT_PROPERTY_OVERRIDES += \
    keyguard.no_require_sim=true \
    ro.url.legal=http://www.google.com/intl/%s/mobile/android/basic/phone-legal.html \
    ro.url.legal.android_privacy=http://www.google.com/intl/%s/mobile/android/basic/privacy.html \
    ro.com.android.wifi-watchlist=GoogleGuest \
    ro.setupwizard.enterprise_mode=1 \
    ro.com.android.dateformat=MM-dd-yyyy \
    ro.com.android.dataroaming=false

PRODUCT_PROPERTY_OVERRIDES += \
    ro.build.selinux=1

# Disable excessive dalvik debug messages
PRODUCT_PROPERTY_OVERRIDES += \
    dalvik.vm.debug.alloc=0

# Disable ADB authentication
ADDITIONAL_DEFAULT_PROPERTIES += ro.adb.secure=0

# Thank you, please drive thru!
PRODUCT_PROPERTY_OVERRIDES += persist.sys.dun.override=0


# Backup Tool
PRODUCT_COPY_FILES += \
	vendor/eos/prebuilt/common/bin/backuptool.sh:system/bin/backuptool.sh \
	vendor/eos/prebuilt/common/bin/backuptool.functions:system/bin/backuptool.functions \
	vendor/eos/prebuilt/common/bin/50-hosts.sh:system/addon.d/50-hosts.sh \
	vendor/eos/prebuilt/common/bin/blacklist:system/addon.d/blacklist

# Signature compatibility validation
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/bin/otasigcheck.sh:system/bin/otasigcheck.sh

# Terminal Emulator prebuilt library
PRODUCT_COPY_FILES +=  \
    vendor/eos/prebuilt/common/lib/libjackpal-androidterm4.so:system/app/TerminalEmulator/lib/arm/libjackpal-androidterm4.so

# init.d support
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/init.d/00banner:system/etc/init.d/00banner \
    vendor/eos/prebuilt/common/bin/sysinit:system/bin/sysinit

# userinit support
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/init.d/90userinit:system/etc/init.d/90userinit

# chromecast support
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/init.d/69chromecast:system/etc/init.d/69chromecast

# eos-specific init file
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/init.eos.rc:root/init.eos.rc

# mounts
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/bin/sysrw:system/bin/sysrw \
    vendor/eos/prebuilt/common/bin/sysro:system/bin/sysro \
    vendor/eos/prebuilt/common/bin/rootrw:system/bin/rootrw \
    vendor/eos/prebuilt/common/bin/rootro:system/bin/rootro

# Bring in camera effects
PRODUCT_COPY_FILES +=  \
    vendor/eos/prebuilt/common/media/LMprec_508.emd:system/media/LMprec_508.emd \
    vendor/eos/prebuilt/common/media/PFFprec_600.emd:system/media/PFFprec_600.emd

# Enable SIP+VoIP on all targets
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.software.sip.voip.xml:system/etc/permissions/android.software.sip.voip.xml

# Enable wireless Xbox 360 controller support
PRODUCT_COPY_FILES += \
    frameworks/base/data/keyboards/Vendor_045e_Product_028e.kl:system/usr/keylayout/Vendor_045e_Product_0719.kl

# Don't export PS1 in /system/etc/mkshrc.
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/mkshrc:system/etc/mkshrc

# Supersu support
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/bin/suinstaller.sh:system/bin/suinstaller.sh \
    vendor/eos/prebuilt/common/supersu.zip:system/supersu.zip

# Required eos packages
PRODUCT_PACKAGES += \
    BluetoothExt \
    Camera \
    Development \
    EOSWallpaperClient \
    EOSUpdater \
    AudioFX \
    Email \
    LatinIME \
    Launcher2 \
    LockClock \
    libpcre

# Optional eos packages
PRODUCT_PACKAGES += \
    audio_effects.conf \
    Basic \
    Eleven \
    VideoEditor \
    VoiceDialer \
    SoundRecorder \
    TerminalEmulator

# Theme Engine support
PRODUCT_PACKAGES += \
    ThemeChooser \
    ThemesProvider

# EOS Hardware Abstraction Framework
PRODUCT_PACKAGES += \
    org.cyanogenmod.hardware \
    org.cyanogenmod.hardware.xml

# Extra tools in EOS
PRODUCT_PACKAGES += \
    libsepol \
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
    mkfs.f2fs \
    fsck.f2fs \
    fibmap.f2fs \
    ntfsfix \
    ntfs-3g \
    systembinsh \
    libcurl \
    curl \
    gdbserver \
    micro_bench \
    oprofiled \
    sqlite3 \
    strace

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

# easy way to extend to add more packages
-include vendor/extra/product.mk

PRODUCT_PACKAGE_OVERLAYS += vendor/eos/overlay/dictionaries
PRODUCT_PACKAGE_OVERLAYS += vendor/eos/overlay/common

# Set valid modversion
PRODUCT_PROPERTY_OVERRIDES += ro.modversion=$(BUILD_NUMBER)

PRODUCT_VERSION_MAJOR = 5
PRODUCT_VERSION_MINOR = 0
PRODUCT_VERSION_MAINTENANCE = 0-RC0

# Set EOS_BUILDTYPE from the env RELEASE_TYPE, for jenkins compat

ifndef EOS_BUILDTYPE
    ifdef RELEASE_TYPE
        # Starting with "EOS_" is optional
        RELEASE_TYPE := $(shell echo $(RELEASE_TYPE) | sed -e 's|^EOS_||g')
        EOS_BUILDTYPE := $(RELEASE_TYPE)
    endif
endif

# Filter out random types, so it'll reset to UNOFFICIAL
ifeq ($(filter RELEASE NIGHTLY SNAPSHOT EXPERIMENTAL,$(EOS_BUILDTYPE)),)
    EOS_BUILDTYPE :=
endif

ifdef EOS_BUILDTYPE
    ifneq ($(EOS_BUILDTYPE), SNAPSHOT)
        ifdef EOS_EXTRAVERSION
            # Force build type to EXPERIMENTAL
            EOS_BUILDTYPE := EXPERIMENTAL
            # Remove leading dash from EOS_EXTRAVERSION
            EOS_EXTRAVERSION := $(shell echo $(EOS_EXTRAVERSION) | sed 's/-//')
            # Add leading dash to EOS_EXTRAVERSION
            EOS_EXTRAVERSION := -$(EOS_EXTRAVERSION)
        endif
    else
        ifndef EOS_EXTRAVERSION
            # Force build type to EXPERIMENTAL, SNAPSHOT mandates a tag
            EOS_BUILDTYPE := EXPERIMENTAL
        else
            # Remove leading dash from EOS_EXTRAVERSION
            EOS_EXTRAVERSION := $(shell echo $(EOS_EXTRAVERSION) | sed 's/-//')
            # Add leading dash to EOS_EXTRAVERSION
            EOS_EXTRAVERSION := -$(EOS_EXTRAVERSION)
        endif
    endif
else
    # If EOS_BUILDTYPE is not defined, set to UNOFFICIAL
    EOS_BUILDTYPE := UNOFFICIAL
    EOS_EXTRAVERSION :=
endif

ifeq ($(EOS_BUILDTYPE), UNOFFICIAL)
    ifneq ($(TARGET_UNOFFICIAL_BUILD_ID),)
        EOS_EXTRAVERSION := -$(TARGET_UNOFFICIAL_BUILD_ID)
    endif
endif

ifeq ($(EOS_BUILDTYPE), RELEASE)
    ifndef TARGET_VENDOR_RELEASE_BUILD_ID
        EOS_VERSION := $(PRODUCT_VERSION_MAJOR).$(PRODUCT_VERSION_MINOR).$(PRODUCT_VERSION_MAINTENANCE)$(PRODUCT_VERSION_DEVICE_SPECIFIC)-$(EOS_BUILD)
    else
        ifeq ($(TARGET_BUILD_VARIANT),user)
            EOS_VERSION := $(PRODUCT_VERSION_MAJOR).$(PRODUCT_VERSION_MINOR)-$(TARGET_VENDOR_RELEASE_BUILD_ID)-$(EOS_BUILD)
        else
            EOS_VERSION := $(PRODUCT_VERSION_MAJOR).$(PRODUCT_VERSION_MINOR).$(PRODUCT_VERSION_MAINTENANCE)$(PRODUCT_VERSION_DEVICE_SPECIFIC)-$(EOS_BUILD)
        endif
    endif
else
    ifeq ($(PRODUCT_VERSION_MINOR),0)
        EOS_VERSION := $(PRODUCT_VERSION_MAJOR)-$(shell date -u +%Y%m%d)-$(EOS_BUILDTYPE)$(EOS_EXTRAVERSION)-$(EOS_BUILD)
    else
        EOS_VERSION := $(PRODUCT_VERSION_MAJOR).$(PRODUCT_VERSION_MINOR)-$(shell date -u +%Y%m%d)-$(EOS_BUILDTYPE)$(EOS_EXTRAVERSION)-$(EOS_BUILD)
    endif
endif

PRODUCT_PROPERTY_OVERRIDES += \
  ro.eos.version=$(EOS_VERSION) \
  ro.eos.releasetype=$(EOS_BUILDTYPE) \
  ro.modversion=$(EOS_VERSION)

-include vendor/eos-priv/keys/keys.mk

EOS_DISPLAY_VERSION := $(EOS_VERSION)

ifneq ($(PRODUCT_DEFAULT_DEV_CERTIFICATE),)
ifneq ($(PRODUCT_DEFAULT_DEV_CERTIFICATE),build/target/product/security/testkey)
  ifneq ($(EOS_BUILDTYPE), UNOFFICIAL)
    ifndef TARGET_VENDOR_RELEASE_BUILD_ID
      ifneq ($(EOS_EXTRAVERSION),)
        # Remove leading dash from EOS_EXTRAVERSION
        EOS_EXTRAVERSION := $(shell echo $(EOS_EXTRAVERSION) | sed 's/-//')
        TARGET_VENDOR_RELEASE_BUILD_ID := $(EOS_EXTRAVERSION)
      else
        TARGET_VENDOR_RELEASE_BUILD_ID := $(shell date -u +%Y%m%d)
      endif
    else
      TARGET_VENDOR_RELEASE_BUILD_ID := $(TARGET_VENDOR_RELEASE_BUILD_ID)
    endif
    EOS_DISPLAY_VERSION=$(PRODUCT_VERSION_MAJOR).$(PRODUCT_VERSION_MINOR)-$(TARGET_VENDOR_RELEASE_BUILD_ID)
  endif
endif
endif

# by default, do not update the recovery with system updates
PRODUCT_PROPERTY_OVERRIDES += persist.sys.recovery_update=false

PRODUCT_PROPERTY_OVERRIDES += \
  ro.eos.display.version=$(EOS_DISPLAY_VERSION)

-include $(WORKSPACE)/build_env/image-auto-bits.mk

-include vendor/eospriv/product.mk

$(call inherit-product-if-exists, vendor/extra/product.mk)
