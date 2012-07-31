#!/bin/sh

# Download the base ISO image

if [ ! -f turnkey-tomcat-11.3-lucid-x86.iso ];
then
	wget http://downloads.sourceforge.net/project/turnkeylinux/iso/turnkey-tomcat-11.3-lucid-x86.iso
fi

# Clean up old build files
rm patch.tar.gz

# Bundle the patch to patch.tar.gz
tklpatch-bundle patch

# Execute 



