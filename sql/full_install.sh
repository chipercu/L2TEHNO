if [ -f mysql_pass.sh ]; then
        . mysql_pass.sh
else
        . mysql_pass.sh.default
fi

mysqldump --ignore-table=${DBNAME}.game_log --ignore-table=${DBNAME}.loginserv_log --default-character-set=utf8 --ignore-table=${DBNAME}.petitions --add-drop-table -h $DBHOST -u $USER --password=$PASS $DBNAME > l2jdb_full_backup.sql

for tab in install/*.sql
do
                echo Loading $tab ...
                mysql -h $DBHOST -u $USER --password=$PASS --default-character-set=utf8 -D $DBNAME < $tab
done
./upgrade.sh
