#!/bin/bash
echo "
[mysqld]
secure-file-priv = ''
port = 3308
" >> /etc/mysql/my.cnf

service mysql start

mysql -uroot -ppassword -e "
  ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';
  GRANT FILE ON *.* TO 'root'@'%';
"
mysql -u root -ppassword -e "
  SHOW VARIABLES LIKE 'secure_file_priv';
  SHOW DATABASES;
"

apache2ctl -D FOREGROUND