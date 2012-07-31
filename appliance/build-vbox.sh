#!/bin/sh


VBoxManage createvm \
	--name ActivityInfo \
	--register


VBoxManage modifyvm ActivityInfo \
	--memory 640 \
	--ostype Debian \
	--nic1 bridged \
	--bridgeadapter1 wlan0


VBoxManage createhd --filename ActivityInfoHd -size 10240

VBoxManage storagectl ActivityInfo --name Cdrive --add ide --bootable on
VBoxManage storageattach ActivityInfo \
	--storagectl Cdrive \
	--port 0 \
	--device 0 \
	--medium ActivityInfoHd.vdi \
	--type hdd
VBoxManage storageattach ActivityInfo \
	--storagectl Cdrive \
	--port 1 \
	--device 0 \
	--medium activityinfo-appliance.iso \
	--type dvddrive




