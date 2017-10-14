<?php
# http://127.0.0.1/mysql/big5.php?id=0
# Create a table with compatible charset:
/*
CREATE TABLE IF NOT EXISTS `my_table` (
  `col1` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=big5;
*/

header('Content-Type: text/html; charset=GBK');
  
$link = mysql_connect('127.0.0.1', 'login', 'password');

mysql_set_charset('big5', $link);

mysql_select_db('my_database');

$result = mysql_query("SELECT col1, col2 FROM my_table where id=((\"". addslashes($_GET['id']) ."\"))");

while ($row = mysql_fetch_array($result, MYSQL_NUM))
    echo join(',', $row);