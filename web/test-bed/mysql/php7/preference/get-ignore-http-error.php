<?php
# http://127.0.0.1/mysql/preference/get-ignore-http-error.php?id=1
# Open Preferences and choose option 'Disable initial connection test'

header($_SERVER['SERVER_PROTOCOL'] . ' 500 Internal Server Error', true, 500);

$link = mysqli_connect('localhost', 'root', '', 'my_database');

$result = $link->query("SELECT col1, col2 FROM my_table where id=$_GET[id]");

while ($row = $result->fetch_array(MYSQLI_NUM))
    echo join(',', $row);