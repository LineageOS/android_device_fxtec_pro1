#!/bin/bash
#
# SPDX-FileCopyrightText: 2016 The CyanogenMod Project
# SPDX-FileCopyrightText: 2017-2024 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

set -e

export DEVICE=pro1
export VENDOR=fxtec

# Load extract_utils and do some sanity checks
MY_DIR="${BASH_SOURCE%/*}"
if [[ ! -d "${MY_DIR}" ]]; then MY_DIR="${PWD}"; fi

ANDROID_ROOT="${MY_DIR}/../../.."

HELPER="${ANDROID_ROOT}/tools/extract-utils/extract_utils.sh"
if [ ! -f "${HELPER}" ]; then
    echo "Unable to find helper script at ${HELPER}"
    exit 1
fi
source "${HELPER}"

function blob_fixup() {
    case "${1}" in
        system/lib64/com.qualcomm.qti.ant@1.0.so|system/lib64/vendor.qti.hardware.fm@1.0.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${2}"
            ;;
        system_ext/etc/permissions/com.qti.dpmframework.xml|system_ext/etc/permissions/qti_libpermissions.xml)
            [ "$2" = "" ] && return 0
            sed -i "s/name=\"android.hidl.manager-V1.0-java/name=\"android.hidl.manager@1.0-java/g" "${2}"
            ;;
        system_ext/lib64/lib-imscamera.so)
            [ "$2" = "" ] && return 0
            grep -q "libgui_shim.so" "${2}" || "${PATCHELF}" --add-needed "libgui_shim.so" "${2}"
            ;;
        system_ext/lib64/lib-imsvideocodec.so)
            [ "$2" = "" ] && return 0
            grep -q "libgui_shim.so" "${2}" || "${PATCHELF}" --add-needed "libgui_shim.so" "${2}"
            grep -q "libui_shim.so" "${2}" || "${PATCHELF}" --add-needed "libui_shim.so" "${2}"
            "${PATCHELF}" --replace-needed "libqdMetaData.so" "libqdMetaData.system.so" "${2}"
            ;;
        vendor/bin/hw/android.hardware.bluetooth@1.0-service-qti|vendor/bin/hw/btlfpserver|vendor/bin/hw/vendor.display.color@1.0-service|vendor/bin/hw/vendor.qti.esepowermanager@1.0-service|vendor/bin/hw/vendor.qti.hardware.qdutils_disp@1.0-service-qti|vendor/bin/hw/vendor.qti.hardware.qteeconnector@1.0-service|vendor/bin/hw/vendor.qti.hardware.soter@1.0-service|vendor/bin/hw/vendor.qti.hardware.tui_comm@1.0-service-qti)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${2}"
            ;;
        vendor/etc/izat.conf)
            [ "$2" = "" ] && return 0
            sed -i "216s/PROCESS_STATE=ENABLED/PROCESS_STATE=DISABLED/g" izat.conf
            ;;
        vendor/lib/hw/camera.msm8998.so)
            [ "$2" = "" ] && return 0
            sed -i "s/service.bootanim.exit/service.bootanim.zzzz/g" "${2}"
            ;;
        vendor/lib/libxapi_bokeh.so|vendor/lib/libxapi_mfe.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${2}"
            ;;
        vendor/lib64/hw/fingerprint.msm8998.so)
            [ "$2" = "" ] && return 0
            sed -i "s/libhidltransport.so/libhidlbase_shim.so/" "${2}"
            ;;
        vendor/lib64/libsecureui.so)
            [ "$2" = "" ] && return 0
            "${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${2}"
            ;;
        *)
            return 1
            ;;
    esac

    return 0
}

function blob_fixup_dry() {
    blob_fixup "$1" ""
}

# Default to sanitizing the vendor folder before extraction
CLEAN_VENDOR=true

ONLY_FIRMWARE=
KANG=
SECTION=

while [ "${#}" -gt 0 ]; do
    case "${1}" in
        --only-firmware )
                ONLY_FIRMWARE=true
                ;;
        -n | --no-cleanup )
                CLEAN_VENDOR=false
                ;;
        -k | --kang )
                KANG="--kang"
                ;;
        -s | --section )
                SECTION="${2}"
                shift
                CLEAN_VENDOR=false
                ;;
        * )
                SRC="${1}"
                ;;
    esac
    shift
done

if [ -z "${SRC}" ]; then
    SRC="adb"
fi

# Initialize the helper
setup_vendor "${DEVICE}" "${VENDOR}" "${ANDROID_ROOT}" true "${CLEAN_VENDOR}"

if [ -z "${ONLY_FIRMWARE}" ]; then
    extract "${MY_DIR}/proprietary-files.txt" "${SRC}" "${KANG}" --section "${SECTION}"
fi

if [ -z "${SECTION}" ]; then
    extract_firmware "${MY_DIR}/proprietary-firmware.txt" "${SRC}"
fi

"${MY_DIR}/setup-makefiles.sh"
