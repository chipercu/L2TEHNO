if [ -f mysql_pass.sh ]; then
        . mysql_pass.sh
else
        . mysql_pass.sh.default
fi

mysqldump --ignore-table=$DBNAME.game_log --ignore-table=$DBNAME.loginserv_log --default-character-set=utf8 --ignore-table=$DBNAME.petitions --add-drop-table -h $DBHOST -u $USER --password=$PASS $DBNAME > l2jdb_full_backup.sql

mysql -h $DBHOST -u $USER --password=$PASS --default-character-set=utf8 -D $DBNAME < login/accounts.sql
mysql -h $DBHOST -u $USER --password=$PASS -D $DBNAME < login/banned_ips.sql
mysql -h $DBHOST -u $USER --password=$PASS -D $DBNAME < login/loginserv_log.sql
mysql -h $DBHOST -u $USER --password=$PASS -D $DBNAME < login/lock.sql
mysql -h $DBHOST -u $USER --password=$PASS -D $DBNAME < login/referrals.sql
