# Inherit common eos stuff
$(call inherit-product, vendor/eos/config/common.mk)

# Required Eos packages
PRODUCT_PACKAGES += \
    LatinIME
$(call inherit-product, vendor/eos/config/telephony.mk)
