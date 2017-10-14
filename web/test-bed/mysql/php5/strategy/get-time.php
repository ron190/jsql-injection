<?php
# http://127.0.0.1/mysql/strategy/get-time.php?id=1

mysql_connect('127.0.0.1', 'login', 'password');

mysql_select_db('my_database');

mysql_query("SELECT col1, col2 FROM my_table where id=$_GET[id]");