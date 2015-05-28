# World APN list
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/apns-conf.xml:system/etc/apns-conf.xml

# Telephony packages
PRODUCT_PACKAGES += \
    Mms \
    Stk \
    CellBroadcastReceiver \
    VoiceDialer

# Mms depends on SoundRecorder for recorded audio messages
PRODUCT_PACKAGES += \
    SoundRecorder

# Default ringtone
PRODUCT_PROPERTY_OVERRIDES += \
    ro.config.ringtone=Orion.ogg
