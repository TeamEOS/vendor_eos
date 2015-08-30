# Inherit common EOS config
$(call inherit-product, vendor/eos/config/common.mk)

# Optional EOS packages
PRODUCT_PACKAGES += \
    HoloSpiralWallpaper \
    MagicSmokeWallpapers \
    NoiseField \
    Galaxy4 \
    LiveWallpapers \
    LiveWallpapersPicker \
    VisualizationWallpapers \
    PhaseBeam \
    CMSettingsProvider

# Stagefright FFMPEG plugin
PRODUCT_PACKAGES += \
    libstagefright_soft_ffmpegadec \
    libstagefright_soft_ffmpegvdec \
    libFFmpegExtractor \
    libnamparser \
    vim \
    zip \
    unrar \
    curl

