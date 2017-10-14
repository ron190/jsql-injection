<?php
# http://127.0.0.1/mysql/strategy/get-all.php?id=1

$link = mysqli_connect('localhost', 'root', '', 'my_database');

$result = $link->query("SELECT col1, col2 FROM my_table where id=$_GET[id]") # time
or die(mysql_error()); # error

echo rand();
?> A <?php

# blind
if ($result->num_rows($result) != 0)
    echo ".";

?> B <?php

while ($row = $result->fetch_array(MYSQLI_NUM))
    # normal
    echo join(',', $row);

?> C <?php
echo rand();