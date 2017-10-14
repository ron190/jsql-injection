<?php
# http://127.0.0.1/mysql/strategy/get-time.php?id=1

$link = mysqli_connect('localhost', 'root', '', 'my_database');

$link->query("SELECT col1, col2 FROM my_table where id=$_GET[id]");