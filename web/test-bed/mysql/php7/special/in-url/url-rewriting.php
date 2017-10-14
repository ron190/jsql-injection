<?php
# http://127.0.0.1/mysql/special/in-url/1*/url-rewriting.php?fake-param=whatever

$url = explode("/", $_SERVER['REQUEST_URI']);

$urldecode = urldecode($url[2]);

$link = mysqli_connect('localhost', 'root', '', 'my_database');

$result = $link->query("SELECT col1, col2 FROM my_table where id=$urldecode");

while ($row = $result->fetch_array(MYSQLI_NUM))
    echo join(',', $row);