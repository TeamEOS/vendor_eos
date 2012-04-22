#!/usr/bin/env bash

mkdir -p titaniumbackup
#curl -L http://matrixrewriter.com/android/files/TitaniumBackup_latest.apk -o titaniumbackup/TitaniumBackup.apk &&
(cat << EOF) > titaniumbackup/titaniumbackup.mk
# Copyright (C) 2011 Team Eos
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

PRODUCT_PACKAGES += TitaniumBackup.apk
PRODUCT_COPY_FILES += \
    vendor/eos/proprietary/titaniumbackup/lib/armeabi/libtitanium.so:system/lib/libtitanium.so
EOF

(cat << EOF) > titaniumbackup/Android.mk
# Copyright (C) 2011 Team Eos
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
LOCAL_PATH:= \$(call my-dir)
include \$(CLEAR_VARS)
LOCAL_MODULE := TitaniumBackup.apk
LOCAL_MODULE_CLASS := APPS
LOCAL_CERTIFICATE := PRESIGNED
LOCAL_SRC_FILES := TitaniumBackup.apk
LOCAL_MODULE_TAGS := optional
include \$(BUILD_PREBUILT)
EOF

cd titaniumbackup;
unzip TitaniumBackup.apk lib/armeabi/libtitanium.so
