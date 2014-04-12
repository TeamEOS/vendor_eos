# Inherit common EOS  stuff
$(call inherit-product, vendor/eos/config/common_full.mk)

ifeq ($(TARGET_SCREEN_WIDTH) $(TARGET_SCREEN_HEIGHT),$(space))
    PRODUCT_COPY_FILES += \
        vendor/eos/prebuilt/common/bootanimation/720.zip:system/media/bootanimation.zip
endif
