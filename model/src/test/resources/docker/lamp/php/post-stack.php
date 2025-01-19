<?php
ini_set('display_errors', 1);
$link = mysqli_connect("localhost", "root", "password", "information_schema");
$array = explode(";", "SELECT * FROM schemata where 1={$_POST['id']}");
foreach ($array as $item) {
    foreach(mysqli_query($link, $item) as $row) {
        echo "<li>" . join(',', $row) . "</li>";
    }
}