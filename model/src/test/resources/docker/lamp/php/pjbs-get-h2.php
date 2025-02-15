h2
<?php
ini_set('display_errors', 1);

require_once 'PJBS.php';

$drv = new PJBS(null,null);
$drv->connect('jdbc:h2:mem:public;IGNORECASE=TRUE;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1;', 'sa', '');

$array = explode(";", "SELECT schema_name FROM INFORMATION_SCHEMA.schemata where 1=$_GET[lib]");
foreach ($array as $item) {

    $res = $drv->exec($item);

    // print the first result
    var_dump($drv->fetch_array($res));

    while($row = $drv->fetch_array($res)) {
        var_dump($row);
    }
}