#!/bin/bash

while :;
do
	mv log/stdout.log "log/`date +%Y-%m-%d_%H-%M-%S`_mmo.log"
	nice -n -2 java -server -Xbootclasspath/p:./lib/jsr167.jar -Xms64m -Xmx64m -cp lib/*: l2open.gameserver.instancemanager.MMOTopManager > log/mmo.log 2>&1
	[ $? -ne 2 ] && break
	sleep 10;
done
