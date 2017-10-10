voltdb 
<?php
    /*

    ant
    jdbc:voltdb://localhost:21212
    
    export PATH=$PATH:[voltdbbinpath]
    voltdb create
    ./run.sh server
    ./run ./run.sh jars
    ./run.sh init.
    ./run.sh client

    
    */

    require_once 'PJBS.php';

    // create an instance with something like
    $drv = new PJBS(null,null);

    // connect to a JDBC data source with
    $drv->connect('jdbc:vertica://127.0.0.1:5433/', 'dbadmin', 'password');

    // execute a query
    // echo "select USER_NAME from information_schema.system_users where 0=$_GET[lib]";
    $res = $drv->exec("SELECT col1, col2 FROM my_table where id=$_GET[lib]");

    // print the first result
    var_dump($drv->fetch_array($res));

    while($row = $drv->fetch_array($res))
    {
        var_dump($row);
    }