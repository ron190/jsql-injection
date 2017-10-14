<?php
# http://127.0.0.1/mysql/preference/get-json.php?n=[{"a":["a",{"e":"p", "r": true}]},null,{"e":"r", "id":"-1", "r":[{"a":null, "h":"l"},{"ah":"al"},"aze", 1, true]}]&liba=0
# Open Preferences and choose option 'Inject JSON parameters'

$json = json_decode($_GET['n']);

mysql_connect('127.0.0.1', 'login', 'password');

mysql_select_db('my_database');

$result = mysql_query("SELECT col1, col2 FROM my_table where id={$json[2]->id}");

while ($row = mysql_fetch_array($result, MYSQL_NUM))
    echo join(',', $row);