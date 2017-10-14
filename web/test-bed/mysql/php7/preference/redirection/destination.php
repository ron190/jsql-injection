<?php 
# Called by http://127.0.0.1/mysql/preference/redirection/source.php?id=1
# Open Preferences and choose option 'Follow HTTP redirection'

$link = mysqli_connect('localhost', 'root', '', 'my_database');

$result = $link->query("SELECT col1, col2 FROM my_table where id=$_GET[id]");

while ($row = $result->fetch_array(MYSQLI_NUM))
    echo join(',', $row);