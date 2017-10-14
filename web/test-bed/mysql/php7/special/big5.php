<?php
# http://127.0.0.1/mysql/big5.php?id=0

header('Content-Type: text/html; charset=GBK');

$link = mysqli_connect('localhost', 'root', '', 'my_database');

$link->set_charset('big5');

$result = $link->query("SELECT col1, col2 FROM my_table where id=((\"". addslashes($_GET['id']) ."\"))");

while ($row = $result->fetch_array(MYSQLI_NUM))
    echo join(',',$row);