#!/bin/bash
#
# Copyright (C) 2017-2020 The LineageOS Project
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

set -e

export DEVICE=pro1
export VENDOR=fxtec

export DEVICE_BRINGUP_YEAR=2020

# Load extract_utils and do some sanity checks
MY_DIR="${BASH_SOURCE%/*}"
if [[ ! -d "${MY_DIR}" ]]; then MY_DIR="${PWD}"; fi

LINEAGE_ROOT="${MY_DIR}/../../.."

HELPER="${LINEAGE_ROOT}/vendor/lineage/build/tools/extract_utils.sh"
if [ ! -f "${HELPER}" ]; then
    echo "Unable to find helper script at ${HELPER}"
    exit 1
fi
source "${HELPER}"

# Default to sanitizing the vendor folder before extraction
CLEAN_VENDOR=true
SECTION=
KANG=

while [ "$1" != "" ]; do
    case "$1" in
        -n | --no-cleanup )     CLEAN_VENDOR=false
                                ;;
        -k | --kang)            KANG="--kang"
                                ;;
        -s | --section )        shift
                                SECTION="$1"
                                CLEAN_VENDOR=false
                                ;;
        * )                     SRC="$1"
                                ;;
    esac
    shift
done

if [ -z "${SRC}" ]; then
    SRC=adb
fi

# Initialize the helper
setup_vendor "${DEVICE}" "${VENDOR}" "${LINEAGE_ROOT}" true "${CLEAN_VENDOR}"

extract "${MY_DIR}/proprietary-files.txt" "${SRC}" ${KANG} --section "${SECTION}"

BLOB_ROOT="$LINEAGE_ROOT"/vendor/"$VENDOR"/"$DEVICE"/proprietary

#
# Correct android.hidl.manager@1.0-java jar name
#
sed -i "s|name=\"android.hidl.manager-V1.0-java|name=\"android.hidl.manager@1.0-java|g" \
    "$BLOB_ROOT"/vendor/etc/permissions/qti_libpermissions.xml

#
# Fix product framework path
#
function fix_product_framework_path () {
    sed -i \
        's/\/system\/framework\//\/system\/product\/framework\//g' \
        "$BLOB_ROOT"/"$1"
}

fix_product_framework_path product/etc/permissions/cneapiclient.xml
fix_product_framework_path product/etc/permissions/com.qualcomm.qti.imscmservice-V2.0-java.xml
fix_product_framework_path product/etc/permissions/com.qualcomm.qti.imscmservice-V2.1-java.xml
fix_product_framework_path product/etc/permissions/com.qualcomm.qti.imscmservice.xml
fix_product_framework_path product/etc/permissions/com.quicinc.cne.xml
fix_product_framework_path product/etc/permissions/embms.xml
fix_product_framework_path product/etc/permissions/qcrilhook.xml
fix_product_framework_path product/etc/permissions/telephonyservice.xml

"${MY_DIR}/setup-makefiles.sh"
