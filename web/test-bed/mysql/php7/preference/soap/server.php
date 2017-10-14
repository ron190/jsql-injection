<?php
# http://127.0.0.1/mysql/preference/soap/server.php
# Activate extension=php_soap.dll in php.ini
# Choose method Request: POST
/* 
# Request value => <?xml version="1.0" encoding="UTF-8" standalone="no"?><SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:ns1="urn:mySqlParms" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><SOAP-ENV:Body><ns1:getParameters><_param xsi:type="xsd:string">0</_param></ns1:getParameters></SOAP-ENV:Body></SOAP-ENV:Envelope>
*/
# Open Preferences and choose option 'Inject SOAP parameters...'

function getParameters($parm) {
    $returnedValue = "";
    
    $link = mysqli_connect('localhost', 'root', '', 'my_database');
    
    $link->set_charset('utf8');

    $result = $link->query("SELECT col1, col2 FROM my_table where id=$parm");

    while ($row = $result->fetch_array(MYSQLI_NUM))
        $returnedValue .= join(',', $row);
    
    return $returnedValue;
}

ini_set("soap.wsdl_cache_enabled", "0"); // désactivation du cache WSDL

$server = new SoapServer("definition.wsdl");
$server->addFunction("getParameters");
$server->handle();