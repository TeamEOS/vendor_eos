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

ADDITIONAL_DEFAULT_PROPERTIES += \
    ro.adb.secure=0

PRODUCT_PACKAGES += \
    TerminalEmulator \
    busybox \
    thtt \
    ntfs-3g.probe \
    ntfsfix \
    ntfs-3g \
    fsck.exfat \
    mount.exfat \
    mkfs.exfat \
    PhaseBeam \
    HoloSpiral \
    fstrim \
    libnl \
    iw \
    tcpdump \
    powertop \
    dropbear \
    scp \
    sftp \
    libbt-vendor \
    ssh-keygen \
    libemoji \
    audio_effects.conf \
    BluetoothExt

PRODUCT_COPY_FILES += \
    vendor/eos/proprietary/terminalemulator/libjackpal-androidterm4.so:system/lib/libjackpal-androidterm4.so

#Bring in camera media effects
$(call inherit-product-if-exists, frameworks/base/data/videos/VideoPackage2.mk)

$(call inherit-product-if-exists, vendor/eos/filesystem_overlay/overlay.mk)
DEVICE_PACKAGE_OVERLAYS += vendor/eos/resource_overlay

PRODUCT_PROPERTY_OVERRIDES += \
    ro.eos.majorversion=4

ifeq ($(EOS_RELEASE),)
    PRODUCT_BUILD_PROP_OVERRIDES += \
    BUILD_DISPLAY_ID="EOS KVT49L Nightly $(EOS_BUILD_NUMBER)"
else
    PRODUCT_BUILD_PROP_OVERRIDES += \
    BUILD_DISPLAY_ID="EOS Stable release $(EOS_RELEASE)"
endif

$(call inherit-product, vendor/eos/bootanimations/bootanimation.mk)
