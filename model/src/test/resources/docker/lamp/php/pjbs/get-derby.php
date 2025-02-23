derby
<?php
ini_set('display_errors', 1);

require_once 'PJBS.php';

$drv = new PJBS(null,null);
$drv->connect('jdbc:derby://127.0.0.1:1527/memory:testdb;create=true', 'admin', 'admin');

$array = explode(";", "select schemaname FROM sys.sysschemas where 1=$_GET[lib]");
foreach ($array as $item) {

    $res = $drv->exec($item);

    // print the first result
    var_dump($drv->fetch_array($res));

    while($row = $drv->fetch_array($res)) {
        var_dump($row);
    }
}