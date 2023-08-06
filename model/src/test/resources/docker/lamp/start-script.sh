#!/bin/bash
echo '
[mysqld]
secure_file_priv=""
port=3308
' >> /etc/mysql/my.cnf

service mysql start

mysql -uroot -ppassword --port=3308 -e "
  ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';
"

echo "Listen 8079" >> /etc/apache2/ports.conf

apache2ctl -D FOREGROUND