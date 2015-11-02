# Inherit common EOS stuff
$(call inherit-product, vendor/eos/config/common_full.mk)

# Required EOS packages
PRODUCT_PACKAGES += \
    LatinIME

# Include EOS LatinIME dictionaries
PRODUCT_PACKAGE_OVERLAYS += vendor/eos/overlay/dictionaries

# Default notification/alarm sounds
PRODUCT_PROPERTY_OVERRIDES += \
    ro.config.notification_sound=Argon.ogg \
    ro.config.alarm_alert=Helium.ogg

$(call inherit-product, vendor/eos/config/telephony.mk)
