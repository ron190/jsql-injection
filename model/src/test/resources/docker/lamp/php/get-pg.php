<?php
ini_set('display_errors', 1);
$db = new PDO("pgsql:host=localhost;port=5432;dbname=test", 'postgres', 'my-secret-pw');
$array = explode(";", "SELECT '1' FROM (select 1)x where '1'={$_GET['id']}");
foreach ($array as $item) {
    foreach($db->query($item) as $row) {
        echo "<li>" . join(',', $row) . "</li>";
    }
}