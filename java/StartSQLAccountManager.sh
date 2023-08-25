#!/bin/sh
java -Xbootclasspath/p:./lib/jsr167.jar -Djava.util.logging.config.file=config/console.cfg -cp lib/*: l2open.accountmanager.SQLAccountManager
