#!/usr/bin/env -S PYTHONPATH=../../../tools/extract-utils python3
#
# SPDX-FileCopyrightText: 2024 The LineageOS Project
# SPDX-License-Identifier: Apache-2.0
#

import extract_utils.tools
extract_utils.tools.DEFAULT_PATCHELF_VERSION = '0_9'

from extract_utils.fixups_blob import (
    blob_fixup,
    blob_fixups_user_type,
)
from extract_utils.main import (
    ExtractUtils,
    ExtractUtilsModule,
)

namespace_imports = [
    'device/fxtec/pro1',
    'hardware/qcom-caf/msm8998',
    'hardware/qcom-caf/wlan',
    'vendor/qcom/opensource/data-ipa-cfg-mgr-legacy-um',
    'vendor/qcom/opensource/dataservices',
]

blob_fixups: blob_fixups_user_type = {
    ('system/lib64/com.qualcomm.qti.ant@1.0.so', 'system/lib64/vendor.qti.hardware.fm@1.0.so'): blob_fixup()
        .replace_needed('libhidlbase.so', 'libhidlbase-v32.so'),
    ('system_ext/etc/permissions/com.qti.dpmframework.xml',
     'system_ext/etc/permissions/qti_libpermissions.xml'): blob_fixup()
        .regex_replace('name="android.hidl.manager-V1.0-java', 'name="android.hidl.manager@1.0-java'),
    'system_ext/lib64/lib-imscamera.so': blob_fixup()
        .add_needed('libgui_shim.so'),
    'system_ext/lib64/lib-imsvideocodec.so': blob_fixup()
        .add_needed('libgui_shim.so')
        .add_needed('libui_shim.so')
        .replace_needed('libqdMetaData.so', 'libqdMetaData.system.so'),
    ('vendor/bin/hw/android.hardware.bluetooth@1.0-service-qti', 'vendor/bin/hw/btlfpserver', 'vendor/bin/hw/vendor.display.color@1.0-service', 'vendor/bin/hw/vendor.qti.esepowermanager@1.0-service', 'vendor/bin/hw/vendor.qti.hardware.qdutils_disp@1.0-service-qti', 'vendor/bin/hw/vendor.qti.hardware.qteeconnector@1.0-service', 'vendor/bin/hw/vendor.qti.hardware.soter@1.0-service', 'vendor/bin/hw/vendor.qti.hardware.tui_comm@1.0-service-qti', 'vendor/lib64/libsecureui.so'): blob_fixup()
        .replace_needed('libhidlbase.so', 'libhidlbase-v32.so'),
    'vendor/bin/hw/android.hardware.drm@1.1-service.widevine': blob_fixup()
        .replace_needed('libhidltransport.so', 'libhidlbase.so')
        .remove_needed('libhwbinder.so'),
    'vendor/etc/izat.conf': blob_fixup()
        .patch_file('gps/0001-gps-izat-Disable-slim_daemon.patch'),
    ('vendor/lib/libxapi_bokeh.so', 'vendor/lib/libxapi_mfe.so'): blob_fixup()
        .replace_needed('libstdc++.so', 'libstdc++_vendor.so'),
    'vendor/lib/hw/camera.msm8998.so': blob_fixup()
        .binary_regex_replace(b'\x73\x65\x72\x76\x69\x63\x65\x2E\x62\x6F\x6F\x74\x61\x6E\x69\x6D\x2E\x65\x78\x69\x74', b'\x73\x65\x72\x76\x69\x63\x65\x2E\x62\x6F\x6F\x74\x61\x6E\x69\x6D\x2E\x7a\x7a\x7a\x7a'),
    'vendor/lib64/hw/fingerprint.msm8998.so': blob_fixup()
        .replace_needed('libhidltransport.so', 'libhidlbase_shim.so'),
    'vendor/lib64/libwvhidl.so': blob_fixup()
        .add_needed('libcrypto_shim.so'),
}  # fmt: skip

module = ExtractUtilsModule(
    'pro1',
    'fxtec',
    blob_fixups=blob_fixups,
    namespace_imports=namespace_imports,
    add_firmware_proprietary_file=True,
    check_elf=False,
)

if __name__ == '__main__':
    utils = ExtractUtils.device(module)
    utils.run()
