# Inherit common Eos config
$(call inherit-product, vendor/eos/config/common.mk)

# Optional Eos packages
PRODUCT_PACKAGES += \
    Galaxy4 \
    HoloSpiralWallpaper \
    LiveWallpapers \
    LiveWallpapersPicker \
    MagicSmokeWallpapers \
    NoiseField \
    PhaseBeam \
    VisualizationWallpapers \
    PhotoTable \
    SoundRecorder \
    PhotoPhase \
    CMSettingsProvider

# Extra tools in Eos
PRODUCT_PACKAGES += \
    vim \
    zip \
    unrar \
    curl
