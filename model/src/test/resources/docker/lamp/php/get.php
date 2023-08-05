<?php
ini_set('display_errors', 1);
$db = new PDO("mysql:host=127.0.0.1;dbname=information_schema", "root", 'password');
foreach($db->query("SELECT * FROM schemata where 1={$_GET['id']}") as $row) {
    echo "<li>" . join(',', $row) . "</li>";
}