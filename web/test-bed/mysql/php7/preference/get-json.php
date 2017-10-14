<?php
# http://127.0.0.1/mysql/preference/get-json.php?n=[{"a":["a",{"e":"p", "r": true}]},null,{"e":"r", "id":"-1", "r":[{"a":null, "h":"l"},{"ah":"al"},"aze", 1, true]}]&liba=0
# Open Preferences and choose option 'Inject each URL parameters...'
# Choose option 'Inject JSON parameters'

$json = json_decode($_GET['n']);

$link = mysqli_connect('localhost', 'root', '', 'my_database');

$result = $link->query("SELECT col1, col2 FROM my_table where id={$json[2]->id}");

while ($row = $result->fetch_array(MYSQLI_NUM))
    echo join(',', $row);