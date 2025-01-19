<?php
ini_set('display_errors', 1);
$link = mysql_connect("localhost", "root", "password");
$result = mysql_query("SELECT * FROM information_schema.schemata where 1={$_GET['id']}");
while ($row = mysql_fetch_array($result, MYSQL_NUM)) {
    echo "<li>" . join(',', $row) . "</li>";
}