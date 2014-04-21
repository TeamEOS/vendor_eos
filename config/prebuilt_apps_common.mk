# Notice: We include the prebuilt versions from the market
# even though source is available. This is to maintain signature
# integrity so users may update from market as needed

PRODUCT_COPY_FILES += \
    vendor/eos/prebuilt/common/apk/Superuser.apk:system/app/Superuser.apk \
    vendor/eos/prebuilt/common/apk/FileExplorer.apk:system/app/FileExplorer.apk \
    vendor/eos/prebuilt/common/apk/TerminalEmulator.apk:system/app/TerminalEmulator.apk

# Terminal Emulator prebuilt library
PRODUCT_COPY_FILES +=  \
    vendor/eos/prebuilt/common/lib/libjackpal-androidterm4.so:system/lib/libjackpal-androidterm4.so \
