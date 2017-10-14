<?php
# http://127.0.0.1/mysql/preference/get-ignore-http-error.php?id=1
# Open Preferences and choose option 'Disable initial connection test'

header("$_SERVER[SERVER_PROTOCOL] 500 Internal Server Error", true, 500);

mysql_connect('127.0.0.1', 'login', 'password');

mysql_select_db('my_database');

$result = mysql_query("SELECT col1, col2 FROM my_table where id=$_GET[id]");

while ($row = mysql_fetch_array($result, MYSQL_NUM))
    echo join(',', $row);