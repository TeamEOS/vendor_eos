#
# This policy configuration will be used by all products that
# inherit from CFX
# 

BOARD_SEPOLICY_DIRS += \
    vendor/cfx/sepolicy

BOARD_SEPOLICY_UNION += \
    file.te \
    file_contexts \
    fs_use \
    genfs_contexts \
    installd.te \
    seapp_contexts \
    vold.te \
    mac_permissions.xml
