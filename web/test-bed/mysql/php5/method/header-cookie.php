<?php 
# http://127.0.0.1/mysql/method/cookie.php
# Choose method Header
# Header value => Cookie: id=1

mysql_connect('127.0.0.1', 'login', 'password');

mysql_select_db('my_database');

$result = mysql_query("SELECT col1, col2 FROM my_table where id={$_COOKIE['id']}");

while ($row = mysql_fetch_array($result, MYSQL_NUM))
    echo join(',', $row);