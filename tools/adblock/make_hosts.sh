#!/bin/bash
DATE=`date -u +%H:%M:%S\ %d.%m.%Y`
PWD=`pwd`

## First feed
wget -q -t 5 http://hosts-file.net/ad_servers.asp -O $PWD/temp_hosts1
sed -i -re '/^#/d ; s/#.*$//' $PWD/temp_hosts1

## Second feed
wget -q -t 5 "http://pgl.yoyo.org/adservers/serverlist.php?hostformat=hosts&showintro=0&mimetype=plaintext" -O temp_hosts2
sed -re '/^#/d ; s/#.*$//' $PWD/temp_hosts2 >> $PWD/temp_hosts1

## Third feed
wget -q -t 5 http://winhelp2002.mvps.org/hosts.txt -O temp_hosts3
sed -re '/^#/d ; s/#.*$//' $PWD/temp_hosts3 >> $PWD/temp_hosts1

## Fourth feed
wget -q -t 5 http://www.ismeh.com/HOSTS -O temp_hosts4
sed -re '/^#/d ; s/#.*$//' $PWD/temp_hosts4 >> $PWD/temp_hosts1

## Clean up
sed -i -e 's///g' -e 's/[ \t][ \t]*/ /g' $PWD/temp_hosts1
sed -i -re '/127\.0\.0\.1.*localhost/d' $PWD/temp_hosts1
sed -i -re '/\:\:1.*localhost/d' $PWD/temp_hosts1
sed -i '/^$/d' $PWD/temp_hosts1

(cat << EOF) > $PWD/hosts
##
## File created by Mustaavalkosta's hosts script
##
## $DATE
##
127.0.0.1 localhost
EOF

awk '!seen[$0]++' $PWD/temp_hosts1 |sort >> $PWD/hosts
rm temp_*
