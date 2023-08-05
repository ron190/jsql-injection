#!/bin/bash
echo "
[mysqld]
secure-file-priv = ""
" >> /etc/mysql/my.cnf

service mysql start

mysql -uroot -ppassword -e "
  ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';
  GRANT FILE ON *.* TO 'root'@'%';
"
mysql -u root -ppassword -e "
  SHOW DATABASES;
"

apache2ctl -D FOREGROUND