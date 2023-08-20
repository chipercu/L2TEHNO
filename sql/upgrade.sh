USER=root
PASS=
DBNAME=l2open
DBHOST=localhost

mysqldump --ignore-table=${DBNAME}.game_log --ignore-table=${DBNAME}.loginserv_log --default-character-set=utf8 --ignore-table=${DBNAME}.petitions --add-drop-table -h $DBHOST -u $USER --password=$PASS $DBNAME > ${DBNAME}_upgrade_`date +%Y-%m-%d_%H:%M:%S`.sql

for tab in upgrade/*.sql
do
		echo Loading $tab ...
		mysql -h $DBHOST -u $USER --password=$PASS --default-character-set=utf8 -D $DBNAME < $tab
done
