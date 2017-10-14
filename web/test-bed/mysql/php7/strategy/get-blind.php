<?php
# http://127.0.0.1/mysql/strategy/get-blind.php?id=1

$link = mysqli_connect('localhost', 'root', '', 'my_database');

$result = $link->query("SELECT col1, col2 FROM my_table where id=$_GET[id]");

echo rand();
?> A <?php

# blind
if ($result->num_rows($result) != 0)
    echo '.';

?> B <?php 
echo rand();