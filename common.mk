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
    AndroidTerm \
	Superuser \
    su \
    thtt \
    EosOverclockHelper \
    busybox \
    ntfs-3g.probe \
    ntfsfix \
    ntfs-3g \
    EosStorageHelper

PRODUCT_PACKAGES += \
    PhaseBeam \
    HoloSpiral

$(call inherit-product-if-exists, vendor/eos/proprietary/goomanager/goomanager.mk)
$(call inherit-product-if-exists, vendor/eos/proprietary/titaniumbackup/titaniumbackup.mk)
$(call inherit-product-if-exists, vendor/eos/overlay/overlay.mk)
DEVICE_PACKAGE_OVERLAYS += vendor/eos/package_overlays

PLATFORM_VERSION := 4.0.3

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
