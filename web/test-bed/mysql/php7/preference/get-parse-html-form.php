<?php
# http://127.0.0.1/mysql/preference/get-parse-html-form.php?fake-param=whatever
# Open Preferences and choose option 'Add <input> parameters...'
# Choose option 'Disable initial connection test'
# Choose option 'Inject each parameters...'
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
    header($_SERVER['SERVER_PROTOCOL'] . ' 500 Internal Server Error', true, 500);
    exit();
}

$link = mysqli_connect('localhost', 'root', '', 'my_database');

$result = $link->query("SELECT col1, col2 FROM my_table where id=$_GET[id]");

while ($row = $result->fetch_array(MYSQLI_NUM))
    echo join(',', $row);