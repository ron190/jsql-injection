#!/bin/sh

service mysql start

mysql -uroot -ppassword --port=3308 -e "
  ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';
"

echo "Listen 8079" >> /etc/apache2/ports.conf

apache2ctl -D FOREGROUND