# Inherit common cfX stuff
$(call inherit-product, vendor/cfx/config/common_full.mk)

ifeq ($(TARGET_SCREEN_WIDTH) $(TARGET_SCREEN_HEIGHT),$(space))
    PRODUCT_COPY_FILES += \
        vendor/cfx/prebuilt/common/bootanimation/720.zip:system/media/bootanimation.zip
endif
