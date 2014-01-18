# Notice: We include the prebuilt versions from the market
# even though source is available. This is to maintain signature
# integrity so users may update from market as needed

PRODUCT_PACKAGES += \
    TerminalEmulator \
    FileExplorer

# Terminal Emulator prebuilt library
PRODUCT_COPY_FILES +=  \
    vendor/cfx/prebuilt/common/lib/libjackpal-androidterm4.so:system/lib/libjackpal-androidterm4.so \
