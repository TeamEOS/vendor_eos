#!/sbin/sh
mkdir -p /tmp/supersu_1/
unzip -o -q /system/supersu.zip -d /tmp/supersu_1/
chmod 755 /tmp/supersu_1/META-INF/com/google/android/update-binary
cp /system/supersu.zip /tmp/supersu.zip
rm -rf /system/supersu.zip
sh /tmp/supersu_1/META-INF/com/google/android/update-binary 2 20 /tmp/supersu.zip
