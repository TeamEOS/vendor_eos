# Inherit common cfX stuff
$(call inherit-product, vendor/cfx/config/common_full.mk)

# BT config
PRODUCT_COPY_FILES += \
    system/bluetooth/data/main.nonsmartphone.conf:system/etc/bluetooth/main.conf

ifeq ($(TARGET_SCREEN_WIDTH) $(TARGET_SCREEN_HEIGHT),$(space))
    PRODUCT_COPY_FILES += \
        vendor/cfx/prebuilt/common/bootanimation/720.zip:system/media/bootanimation.zip
endif
