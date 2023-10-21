<?php
ini_set('display_errors', 1);
$link = mysqli_connect("localhost","root","password","information_schema");
foreach(mysqli_query($link,"SELECT * FROM schemata where 1={$_GET['id']}") as $row) {
    echo "<li>" . join(',', $row) . "</li>";
}