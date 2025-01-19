#!/bin/sh
set -x

# my.cnf mandatory, custom .cnf not working
{
  echo [mysqld]
  echo port=3308
  echo secure_file_priv=""
} >> /etc/mysql/my.cnf

service mysql start
chmod 777 /usr/lib/mysql/plugin/

mysql -uroot -ppassword --port=3308 -e "
  SHOW VARIABLES like 'secure_file_priv';
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