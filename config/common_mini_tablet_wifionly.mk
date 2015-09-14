# Inherit common Eos stuff
$(call inherit-product, vendor/eos/config/common.mk)

# Required Eos packages
PRODUCT_PACKAGES += \
    LatinIME
