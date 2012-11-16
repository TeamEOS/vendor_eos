# Inherit common CM stuff
$(call inherit-product, vendor/cm/config/common_full.mk)

PRODUCT_PACKAGES += \
  Mms

# BT config
PRODUCT_COPY_FILES += \
    system/bluetooth/data/main.conf:system/etc/bluetooth/main.conf
