#!/bin/sh

# Download the base ISO image

if [ ! -f turnkey-tomcat-11.3-lucid-x86.iso ];
then
	wget http://downloads.sourceforge.net/project/turnkeylinux/iso/turnkey-tomcat-11.3-lucid-x86.iso
fi

# Clean up old build files

if [ -f patch.tar.gz ];
then
	rm patch.tar.gz
fi

# Execute
sudo tklpatch turnkey-tomcat-11.3-lucid-x86.iso patch

mv  turnkey-tomcat-11.3-lucid-x86-patched.iso activityinfo-appliance.iso

