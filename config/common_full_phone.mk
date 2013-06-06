# Inherit common eos stuff
$(call inherit-product, vendor/eos/config/common_full.mk)

PRODUCT_PACKAGES += \
  Mms

# World APN list
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/apns-conf.xml:system/etc/apns-conf.xml

# World SPN overrides list
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/spn-conf.xml:system/etc/spn-conf.xml

# SIM Toolkit
PRODUCT_PACKAGES += \
    Stk
