<?php
require_once 'PJBS.php';

$drv = new PJBS(null, null);

$drv->connect('jdbc:neo4j:bolt://ip:port', 'login', 'password');

$res = $drv->exec("MATCH (n:Person) where 1=$_GET[lib] RETURN n.name, n.from, n.title, n.hobby");

while ($row = $drv->fetch_array($res))
    var_dump($row);