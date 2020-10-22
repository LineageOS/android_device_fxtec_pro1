# Audio
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    af.fast_track_multiplier=2 \
    audio.deep_buffer.media=true \
    audio.offload.video=true \
    audio.offload.min.duration.secs=30 \
    ro.af.client_heap_size_kbyte=7168 \
    ro.config.media_vol_steps=25 \
    ro.config.vc_call_vol_steps=11

# DRM
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    drm.service.enabled=true

# FRP
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.frp.pst=/dev/block/bootdevice/by-name/frp

# Gboard
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.com.google.ime.kb_pad_port_l=10 \
    ro.com.google.ime.kb_pad_port_r=10 \
    ro.com.google.ime.key_border=true \
    ro.com.google.ime.bs_theme=true

# Graphics
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    debug.hwui.use_buffer_age=false \
    debug.sf.enable_hwc_vds=1 \
    debug.sf.latch_unsignaled=1 \
    persist.debug.wfd.enable=1 \
    persist.hwc.enable_vds=1

# Location
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.location.osnlp.package=com.google.android.gms \
    ro.location.osnlp.region.package=com.google.android.gms

# NFC
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.hardware.nfc_nci=nqx.default

# RIL
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    keyguard.no_require_sim=true \
    ril.subscription.types=NV,RUIM \
    ro.com.android.dataroaming=true \
    ro.telephony.call_ring.multiple=false \
    telephony.lteOnCdmaDevice=1

# Voice assistant
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.opa.eligible_device=true

# VoLTE / VoWifi
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    persist.dbg.volte_avail_ovr=1 \
    persist.dbg.vt_avail_ovr=1 \
    persist.dbg.wfc_avail_ovr=1
