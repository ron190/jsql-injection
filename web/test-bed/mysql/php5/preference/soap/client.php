<?php
# Do not use
# Only a test client

$client = new SoapClient(
	"definition.wsdl",
	array(
	    "trace"      => 1,
	    "exceptions" => 0
	)
);

try {
    echo "\n";
    print($client->getParameters("version"));
    echo "\n<br>";
    print "<span style=width:1024px>\n";
    print "Request :\n".htmlspecialchars($client->__getLastRequest()) ."\n<br>";
    print "Response:\n".htmlspecialchars($client->__getLastResponse())."\n<br>";
    print "</span><br><br>";

    print($client->getParameters("version_comment"));
    echo "\n\n<br>";

    print "<span style=width:1024px>\n";
    print "Request :\n".htmlspecialchars($client->__getLastRequest()) ."\n<br>";
    print "Response:\n".htmlspecialchars($client->__getLastResponse())."\n<br>";
    print "</span>";
} catch (SoapFault $exception) {
    echo $exception;
}