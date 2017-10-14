<?php
# http://127.0.0.1/mysql/special/in-url/1*/url-rewriting.php?fake-param=whatever

$url = explode('/', $_SERVER['REQUEST_URI']);

$urldecode = urldecode($url[2]);

mysql_connect('127.0.0.1', 'login', 'password');

mysql_select_db('my_database');

$result = mysql_query("SELECT col1, col2 FROM my_table where id=$urldecode");

while ($row = mysql_fetch_array($result, MYSQL_NUM))
    echo join(',', $row);