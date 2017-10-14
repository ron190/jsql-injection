<?php
# http://127.0.0.1/mysql/strategy/get-all.php?id=1*

mysql_connect('127.0.0.1', 'login', 'password');

mysql_select_db('my_database');

$result = mysql_query("SELECT col1, col2 FROM my_table where id=$_GET[id]") # time
or die(mysql_error()); # error

echo rand();
?> A <?php

if (mysql_num_rows($result) != 0)
	# blind
    echo '.';

?> B <?php

while ($row = mysql_fetch_array($result, MYSQL_NUM))
    # normal
    echo join(',', $row);

?> C <?php
echo rand();