#!/bin/sh

service mysql start

mysql -uroot -ppassword --port=3308 -e "
  ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';
"

adduser postgres
mkdir /usr/local/pgsql/data -p
chown postgres /usr/local/pgsql/data
su postgres -c '
  ./usr/lib/postgresql/10/bin/initdb /usr/local/pgsql/data/
  ./usr/lib/postgresql/10/bin/pg_ctl -D /usr/local/pgsql/data start
  ./usr/lib/postgresql/10/bin/createdb test
'

echo "Listen 8079" >> /etc/apache2/ports.conf

apache2ctl -D FOREGROUND