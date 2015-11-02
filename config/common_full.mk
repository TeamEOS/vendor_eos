# Inherit common EOS stuff
$(call inherit-product, vendor/eos/config/common.mk)

# Include EOS audio files
include vendor/eos/config/eos_audio.mk

# Optional EOS packages
PRODUCT_PACKAGES += \
    Galaxy4 \
    HoloSpiralWallpaper \
    LiveWallpapers \
    LiveWallpapersPicker \
    MagicSmokeWallpapers \
    NoiseField \
    PhaseBeam \
    PhotoTable \
    SoundRecorder \
    PhotoPhase

# Extra tools in EOS
PRODUCT_PACKAGES += \
    vim \
    zip \
    unrar \
    curl
