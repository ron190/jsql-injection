<?php
# http://127.0.0.1/mysql/preference/get-parse-html-form.php?fake-param=whatever
# Open Preferences and choose option 'Add <input> parameters...'
?>

<form action="/a" method="get">
<input value="a" />
<input name="a" value="a" />
<input name="id" value="1" />
</form>

<form action="/b" method="post">
<input name="b" value="b" />
</form>

<?php
if (!isset($_GET['a']) && !isset($_POST['b'])) {
    header("$_SERVER[SERVER_PROTOCOL] 500 Internal Server Error", true, 500);
    exit();
}

mysql_connect('127.0.0.1', 'login', 'password');

mysql_select_db('my_database');

$result = mysql_query("SELECT col1, col2 FROM my_table where id=$_GET[id]");

while ($row = mysql_fetch_array($result, MYSQL_NUM))
    echo join(',', $row);