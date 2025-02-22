<?php
ini_set('display_errors', 1);
$db = new SQLite3('test.sqlite');
$db->loadExtension('fileio.so');
$array = explode(";", "select * from tbl1 where 1=$_GET[id]");
foreach ($array as $item) {
    $results = $db->query($item);
    while ($row = $results->fetchArray()) {
        echo "$row[one]<br>";
    }
}