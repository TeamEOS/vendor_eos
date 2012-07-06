#
# Copyright (C) 2011 The Android Open-Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# This file includes all definitions that apply to ALL eos builds

PRODUCT_PACKAGES += \
    TerminalEmulator \
	Superuser \
    su \
    thtt \
    EosOverclockHelper \
    busybox \
    ntfs-3g.probe \
    ntfsfix \
    ntfs-3g \
    EosStorageHelper \
    GooManager

PRODUCT_PACKAGES += \
    PhaseBeam \
    HoloSpiral

PRODUCT_PACKAGES += \
    ThemeChooser \
    ThemeManager

PRODUCT_COPY_FILES += \
    vendor/eos/proprietary/terminalemulator/libjackpal-androidterm3.so:system/lib/libjackpal-androidterm3.so \

PRODUCT_COPY_FILES += \
    frameworks/base/data/etc/com.tmobile.software.themes.xml:/system/etc/permissions/com.tmobile.software.themes.xml \

#Bring in camera media effects
$(call inherit-product-if-exists, frameworks/base/data/videos/VideoPackage2.mk)

#$(call inherit-product-if-exists, vendor/eos/proprietary/titaniumbackup/titaniumbackup.mk)
$(call inherit-product-if-exists, vendor/eos/overlay/overlay.mk)
DEVICE_PACKAGE_OVERLAYS += vendor/eos/package_overlays

PLATFORM_VERSION := 4.0.4

PRODUCT_PROPERTY_OVERRIDES += \
    ro.eos.majorversion=1

ifeq ($(EOS_RELEASE),)
    PRODUCT_BUILD_PROP_OVERRIDES += \
    BUILD_DISPLAY_ID="EOS IMM76D Nightly $(EOS_BUILD_NUMBER) (`(cd $(ANDROID_BUILD_TOP)/.repo/manifests ; git log -1 --pretty=%h versioned.xml)`)"\
    BUILD_ID=IMM76D
else
    PRODUCT_BUILD_PROP_OVERRIDES += \
    BUILD_DISPLAY_ID="EOS Stable release $(EOS_RELEASE)" \
    BUILD_ID=IMM76D
endif

#### Goo Manager support
## If EOS_RELEASE is not defined by the user, assume the build is a nightly release.
## If EOS_RELEASE is defined, use the environment variable EOS_RELEASE_GOOBUILD as the build number.
PRODUCT_PROPERTY_OVERRIDES += \
    ro.goo.developerid=teameos \
    ro.goo.board=$(subst full_,,$(TARGET_PRODUCT)) \

ifeq ($(EOS_RELEASE),)
	PRODUCT_PROPERTY_OVERRIDES += \
	ro.goo.rom=eosNightlies \
	ro.goo.version=$(shell date +%s)
else
	PRODUCT_PROPERTY_OVERRIDES += \
	ro.goo.rom=eos \
	ro.goo.version=$(EOS_RELEASE_GOOBUILD)
endif
