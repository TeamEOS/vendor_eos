
PRODUCT_COPY_FILES += \
    vendor/eos/filesystem_overlay/bin/sysinit:system/bin/sysinit \
    vendor/eos/filesystem_overlay/bin/sysrw:system/bin/sysrw \
    vendor/eos/filesystem_overlay/bin/sysro:system/bin/sysro \
    vendor/eos/filesystem_overlay/bin/rootrw:system/bin/rootrw \
    vendor/eos/filesystem_overlay/bin/rootro:system/bin/rootro \
    vendor/eos/filesystem_overlay/etc/init.d/01zram-swap:system/etc/init.d/01zram-swap \
    vendor/eos/filesystem_overlay/etc/init.d/02overclock:system/etc/init.d/02overclock \
    vendor/eos/filesystem_overlay/etc/init.d/03eosSettings:system/etc/init.d/03eosSettings \
    vendor/eos/filesystem_overlay/bin/backuptool.functions:system/bin/backuptool.functions \
    vendor/eos/filesystem_overlay/bin/backuptool.sh:system/bin/backuptool.sh \
    vendor/eos/filesystem_overlay/addon.d/99-eos-hosts.sh:system/addon.d/99-eos-hosts.sh \
    vendor/eos/filesystem_overlay/media/LMprec_508.emd:system/media/LMprec_508.emd \
    vendor/eos/filesystem_overlay/media/PFFprec_600.emd:system/media/PFFprec_600.emd \
# Proper zoneinfo files
# don't know why the proper ZoneinfoDB classes aren't updated yet
PRODUCT_COPY_FILES += \
    vendor/eos/filesystem_overlay/usr/share/zoneinfo/zoneinfo.dat:system/usr/share/zoneinfo/zoneinfo.dat \
    vendor/eos/filesystem_overlay/usr/share/zoneinfo/zoneinfo.idx:system/usr/share/zoneinfo/zoneinfo.idx \
    vendor/eos/filesystem_overlay/usr/share/zoneinfo/zoneinfo.version:system/usr/share/zoneinfo/zoneinfo.version

