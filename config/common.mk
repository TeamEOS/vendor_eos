PRODUCT_BRAND ?= eos

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

ifneq ($(TARGET_BUILD_VARIANT),user)
# Thank you, please drive thru!
PRODUCT_PROPERTY_OVERRIDES += persist.sys.dun.override=0
endif

ifneq ($(TARGET_BUILD_VARIANT),eng)
# Enable ADB authentication
ADDITIONAL_DEFAULT_PROPERTIES += ro.adb.secure=1
endif

# Copy over the changelog to the device
PRODUCT_COPY_FILES += \
    vendor/eos/CHANGELOG.mkdn:system/etc/CHANGELOG-EOS.txt

# Backup Tool
ifneq ($(WITH_GMS),true)
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/bin/backuptool.sh:install/bin/backuptool.sh \
    vendor/eos/prebuilt/common/bin/backuptool.functions:install/bin/backuptool.functions \
    vendor/eos/prebuilt/common/bin/50-eos.sh:system/addon.d/50-eos.sh \
    vendor/eos/prebuilt/common/bin/blacklist:system/addon.d/blacklist
endif

# Signature compatibility validation
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/bin/otasigcheck.sh:install/bin/otasigcheck.sh

# init.d support
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/init.d/00banner:system/etc/init.d/00banner \
    vendor/eos/prebuilt/common/bin/sysinit:system/bin/sysinit

ifneq ($(TARGET_BUILD_VARIANT),user)
# userinit support
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/init.d/90userinit:system/etc/init.d/90userinit
endif

# EOS-specific init file
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/init.eos.rc:root/init.eos.rc

# Copy over added mimetype supported in libcore.net.MimeUtils
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/lib/content-types.properties:system/lib/content-types.properties

# Enable SIP+VoIP on all targets
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.software.sip.voip.xml:system/etc/permissions/android.software.sip.voip.xml

# Enable wireless Xbox 360 controller support
PRODUCT_COPY_FILES += \
    frameworks/base/data/keyboards/Vendor_045e_Product_028e.kl:system/usr/keylayout/Vendor_045e_Product_0719.kl

# This is EOS! (but we'll use some EOS perms)!
PRODUCT_COPY_FILES += \
    vendor/eos/config/permissions/com.cyanogenmod.android.xml:system/etc/permissions/com.cyanogenmod.android.xml

# Allow tethering without provisioning app
PRODUCT_PROPERTY_OVERRIDES += \
    net.tethering.noprovisioning=true

# Optional Eos packages
PRODUCT_PACKAGES += \
    Jive \
    org.teameos.wallpapers \
    Chromium \
    EOSUpdater

# Eos Utils Library
PRODUCT_PACKAGES += \
    org.teameos.utils

PRODUCT_BOOT_JARS += \
    org.teameos.utils

# Theme engine
include vendor/eos/config/themes_common.mk

# Required EOS packages
PRODUCT_PACKAGES += \
    Development \
    BluetoothExt \
    Profiles

# Optional EOS packages
PRODUCT_PACKAGES += \
    libemoji \
    Terminal

# Custom EOS packages
PRODUCT_PACKAGES += \
    Launcher3 \
    Trebuchet \
    AudioFX \
    Eleven \
    LockClock \
    CMSettingsProvider \
    ExactCalculator

# EOS Platform Library
PRODUCT_PACKAGES += \
    org.cyanogenmod.platform-res \
    org.cyanogenmod.platform \
    org.cyanogenmod.platform.xml

# EOS Hardware Abstraction Framework
PRODUCT_PACKAGES += \
    org.cyanogenmod.hardware \
    org.cyanogenmod.hardware.xml

# Extra tools in EOS
PRODUCT_PACKAGES += \
    libsepol \
    e2fsck \
    mke2fs \
    tune2fs \
    bash \
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

# Stagefright FFMPEG plugin
PRODUCT_PACKAGES += \
    libffmpeg_extractor \
    libffmpeg_omx \
    media_codecs_ffmpeg.xml

PRODUCT_PROPERTY_OVERRIDES += \
    media.sf.omx-plugin=libffmpeg_omx.so \
    media.sf.extractor-plugin=libffmpeg_extractor.so

# TCM (TCP Connection Management)
PRODUCT_PACKAGES += \
    tcmiface

PRODUCT_BOOT_JARS += \
    tcmiface

# These packages are excluded from user builds
ifneq ($(TARGET_BUILD_VARIANT),user)
PRODUCT_PACKAGES += \
    procmem \
    procrank \
    su
endif

PRODUCT_PROPERTY_OVERRIDES += \
    persist.sys.root_access=0

PRODUCT_PACKAGE_OVERLAYS += vendor/eos/overlay/common

PRODUCT_VERSION_MAJOR = 6
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
    EOS_VERSION := $(PRODUCT_VERSION_MAJOR).$(PRODUCT_VERSION_MINOR)-$(shell date -u +%Y%m%d)-$(EOS_BUILDTYPE)$(EOS_EXTRAVERSION)-$(EOS_BUILD)
endif

PRODUCT_PROPERTY_OVERRIDES += \
  ro.eos.version=$(EOS_VERSION) \
  ro.eos.releasetype=$(EOS_BUILDTYPE) \
  ro.modversion=$(EOS_VERSION)

-include vendor/cm-priv/keys/keys.mk

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

ifndef CM_PLATFORM_SDK_VERSION
  # This is the canonical definition of the SDK version, which defines
  # the set of APIs and functionality available in the platform.  It
  # is a single integer that increases monotonically as updates to
  # the SDK are released.  It should only be incremented when the APIs for
  # the new release are frozen (so that developers don't write apps against
  # intermediate builds).
  CM_PLATFORM_SDK_VERSION := 4
endif

ifndef CM_PLATFORM_REV
  # For internal SDK revisions that are hotfixed/patched
  # Reset after each EOS_PLATFORM_SDK_VERSION release
  # If you are doing a release and this is NOT 0, you are almost certainly doing it wrong
  CM_PLATFORM_REV := 0
endif

PRODUCT_PROPERTY_OVERRIDES += \
  ro.eos.display.version=$(EOS_DISPLAY_VERSION)

# CyanogenMod Platform SDK Version
PRODUCT_PROPERTY_OVERRIDES += \
  ro.cm.build.version.plat.sdk=$(CM_PLATFORM_SDK_VERSION)

# CyanogenMod Platform Internal
PRODUCT_PROPERTY_OVERRIDES += \
  ro.cm.build.version.plat.rev=$(CM_PLATFORM_REV)

-include $(WORKSPACE)/build_env/image-auto-bits.mk

-include vendor/cyngn/product.mk

$(call prepend-product-if-exists, vendor/extra/product.mk)
