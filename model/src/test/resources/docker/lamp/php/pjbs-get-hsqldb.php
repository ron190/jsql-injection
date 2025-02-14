hsqldb
<?php
ini_set('display_errors', 1);

require_once 'PJBS.php';

$drv = new PJBS(null,null);
$drv->connect('jdbc:hsqldb:hsql://127.0.0.1:9003/mainDb', 'sa', '');

$array = explode(";", "select USER_NAME,USER_NAME from information_schema.system_users where 1=$_GET[lib]");
foreach ($array as $item) {

    $res = $drv->exec($item);

    // print the first result
    var_dump($drv->fetch_array($res));

    while($row = $drv->fetch_array($res)) {
        var_dump($row);
    }
}