<?php 
# http://127.0.0.1/mysql/method/request-post.php
# Choose method Request: POST
# Request value => id=1

$link = mysqli_connect('localhost', 'root', '', 'my_database');

$result = $link->query("SELECT col1, col2 FROM my_table where id={$_POST['id']}");

while ($row = $result->fetch_array(MYSQLI_NUM))
    echo join(',', $row);