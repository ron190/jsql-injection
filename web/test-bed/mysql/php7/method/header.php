<?php
# http://127.0.0.1/mysql/method/header.php?id=1
# Choose method Header
# Header value => id: 1

$link = mysqli_connect('localhost', 'root', '', 'my_database');

$result = $link->query("SELECT col1, col2 FROM my_table where id={$_SERVER['HTTP_ID']}");

while ($row = $result->fetch_array(MYSQLI_NUM))
    echo join(',', $row);