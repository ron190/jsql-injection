#!/bin/sh
set -x

# configure mysql
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

# configure postgres
adduser postgres
mkdir /usr/local/pgsql/data -p
chown postgres /usr/local/pgsql/data
su postgres -c "
./usr/lib/postgresql/10/bin/initdb /usr/local/pgsql/data/
./usr/lib/postgresql/10/bin/pg_ctl -D /usr/local/pgsql/data start
./usr/lib/postgresql/10/bin/createdb test
psql -U postgres <<SQL
  ALTER USER postgres PASSWORD 'my-secret-pw';
SQL
"

# configure sqlite
sed -i 's/^;extension=sqlite3/extension=sqlite3/' /etc/php/7.2/cli/php.ini /etc/php/7.2/apache2/php.ini

# configure pjbs
DERBY_CP="/var/www/html/java/derbyclient-10.16.1.1.jar"
DERBY_CP="$DERBY_CP:/var/www/html/java/derbyshared-10.16.1.1.jar"
DERBY_CP="$DERBY_CP:/var/www/html/java/derbytools-10.16.1.1.jar"
DERBY_CP="$DERBY_CP:/var/www/html/java/derby-10.16.1.1.jar"
DERBY_CP="$DERBY_CP:/var/www/html/java/derbynet-10.16.1.1.jar"
DERBY_CP="$DERBY_CP:/var/www/html/java/lucene-core-1.9.1.jar"
DERBY_CP="$DERBY_CP:/var/www/html/java/hsqldb-2.7.4.jar"
DERBY_CP="$DERBY_CP:/var/www/html/java/h2-2.3.232.jar"
java -cp "$DERBY_CP" /var/www/html/java/Pjbs.java &

# configure apache
echo "Listen 8079" >> /etc/apache2/ports.conf
apache2ctl -D FOREGROUND
