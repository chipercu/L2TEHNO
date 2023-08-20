if [ -f mysql_pass.sh ]; then
	. mysql_pass.sh
else
	. mysql_pass.sh.default
fi

mysqldump --ignore-table=${DBNAME}.game_log --default-character-set=utf8 --ignore-table=${DBNAME}.loginserv_log --ignore-table=${DBNAME}.petitions --add-drop-table -h $DBHOST -u $USER --password=$PASS $DBNAME > ${DBNAME}_BECKUP_`date +%Y-%m-%d_%H:%M:%S`.sql
