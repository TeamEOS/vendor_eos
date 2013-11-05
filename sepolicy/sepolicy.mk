#
# This policy configuration will be used by all products that
# inherit from CFX
# 

BOARD_SEPOLICY_DIRS += \
    vendor/cfx/sepolicy

BOARD_SEPOLICY_UNION += \
    seapp_contexts \
    mac_permissions.xml
