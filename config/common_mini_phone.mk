# Inherit common CM stuff
$(call inherit-product, vendor/cm/config/common.mk)

# Include CM audio files
include vendor/cm/config/cm_audio.mk

PRODUCT_PACKAGES += \
  Mms
