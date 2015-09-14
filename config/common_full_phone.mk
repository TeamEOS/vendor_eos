# Inherit common eos stuff
$(call inherit-product, vendor/eos/config/common_full.mk)

# Required Eos packages
PRODUCT_PACKAGES += \
    LatinIME

# Include Eos LatinIME dictionaries
PRODUCT_PACKAGE_OVERLAYS += vendor/eos/overlay/dictionaries
$(call inherit-product, vendor/eos/config/telephony.mk)
