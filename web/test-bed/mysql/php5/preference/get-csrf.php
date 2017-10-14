<?php
# http://127.0.0.1/mysql/preference/get-csrf.php?id=1
# Open Preferences and choose option 'Process CSRF token'

session_start();

if (!isset($_SESSION['csrf_token'])) {
    $_SESSION['csrf_token'] = base64_encode(substr(str_shuffle(MD5(microtime())), 0, 10));
}

session_write_close();
?>

<input name="csrf_token" value="<?php echo $_SESSION['csrf_token'] ?>" />

<?php
echo "Session csrf_token $_SESSION[csrf_token]<br/>";

if (isset($_SERVER['HTTP_CSRF_TOKEN']))
    echo "Header HTTP_CSRF_TOKEN: $_SERVER[HTTP_CSRF_TOKEN]<br/>";

if (isset($_GET['csrf_token']))
    echo "Get csrf_token: $_GET[csrf_token]<br/>";

// Check a POST is valid.
if (isset($_POST['csrf_token']) && $_POST['csrf_token'] === $_SESSION['csrf_token']) {
    // POST data is valid.
}  else {
    exit();
}

mysql_connect('127.0.0.1', 'login', 'password');

mysql_select_db('my_database');

$result = mysql_query("SELECT col1, col2 FROM my_table where id=$_GET[id]");

while ($row = mysql_fetch_array($result, MYSQL_NUM))
    echo join(',', $row);