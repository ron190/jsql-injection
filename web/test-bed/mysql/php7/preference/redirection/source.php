<?php 
# http://127.0.0.1/mysql/preference/redirection/source.php?id=1
# Open Preferences and choose option 'Follow HTTP redirection'

header('Location: destination.php?'.$_SERVER['QUERY_STRING']);
exit();