# Inherit common EOS stuff
$(call inherit-product, vendor/eos/config/common.mk)

# Include EOS audio files
include vendor/eos/config/eos_audio.mk

# Required EOS packages
PRODUCT_PACKAGES += \
    LatinIME

# Default notification/alarm sounds
PRODUCT_PROPERTY_OVERRIDES += \
    ro.config.notification_sound=Argon.ogg \
    ro.config.alarm_alert=Helium.ogg

