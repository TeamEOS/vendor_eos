# Inherit common eos stuff
$(call inherit-product, vendor/eos/config/common.mk)

PRODUCT_PACKAGES += \
  Mms

# World APN list
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/apns-conf.xml:system/etc/apns-conf.xml

# Selective SPN list for operator number who has the problem.
PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/etc/selective-spn-conf.xml:system/etc/selective-spn-conf.xml

# SIM Toolkit
PRODUCT_PACKAGES += \
    Stk
