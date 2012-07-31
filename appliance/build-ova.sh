#!/bin/sh

VBoxManage modifyhd ActivityInfoHd.vdi --compact

if [ -f ActivityInfo.ovf ];
then
	rm ActivityInfo.ovf
fi

if [ -f ActivityInfo-disk1.vmdk ];
then
	rm ActivityInfo-disk1.vmdk
fi

VBoxManage export ActivityInfo \
	--vsys 0 \
	--output ActivityInfo.ovf \
	--product ActivityInfo \
	--producturl http://www.activityinfo.org \
	--vendor bedatadriven \
	--vendorurl http://www.bedatadriven.com \
	--eulafile gpl.txt


# Remove the reference to wlan0 from the config file
sed -i '/wlan0/d' ActivityInfo.ovf

tar -cf ActivityInfo-2.6.25.ova ActivityInfo.ovf ActivityInfo-disk1.vmdk

