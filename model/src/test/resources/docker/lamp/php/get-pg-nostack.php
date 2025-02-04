<?php
ini_set('display_errors', 1);
$db = new PDO('pgsql:', 'postgres', 'my-secret-pw');
foreach($db->query("SELECT '1' FROM (select 1)x where '1'={$_GET['id']}") as $row) {
    echo '<li>'. join(',', $row) .'</li>';
}