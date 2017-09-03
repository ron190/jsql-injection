## Description
**jSQL Injection** is a lightweight application used to find database information from a distant server.

It is **free**, **open source** and **cross-platform** (Windows, Linux, Mac OS X).

![Kali Linux logo](https://github.com/ron190/jsql-injection/raw/master/web/images/kali_favicon.png "Kali Linux logo") jSQL Injection is also part of the official penetration testing distribution [Kali Linux](http://www.kali.org/) and is included in other distributions like [Pentest Box](https://pentestbox.com/), [Parrot Security OS](https://www.parrotsec.org), [ArchStrike](https://archstrike.org/) or [BlackArch Linux](http://www.blackarch.org/).

[![Java 8](https://github.com/ron190/jsql-injection/raw/master/web/images/image.io/java.png)](http://www.oracle.com/technetwork/java/javase/downloads/)
[![License GPLv2](https://github.com/ron190/jsql-injection/raw/master/web/images/image.io/license.png)](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)
[![JUnit 4.11](https://github.com/ron190/jsql-injection/raw/master/web/images/image.io/junit.png)](http://junit.org)
[![Maven 3.2](https://github.com/ron190/jsql-injection/raw/master/web/images/image.io/maven.png)](https://maven.apache.org/)
[![SonarQube 6.3](https://github.com/ron190/jsql-injection/raw/master/web/images/image.io/sonar.png)](http://www.sonarqube.org/)<br>
[![Twitter Follow](https://img.shields.io/twitter/follow/ron190jsql.svg?style=social&label=ron190)](https://twitter.com/ron190jsql)

## Features
- Automatic injection of 23 kinds of databases: Access, CockroachDB, CUBRID, DB2, Derby, Firebird, H2, Hana, HSQLDB, Informix, Ingres, MaxDB, Mckoi, MySQL{MariaDb}, Neo4j, NuoDB, Oracle, PostgreSQL, SQLite, SQL Server, Sybase, Teradata and Vertica
- Multiple injection strategies: Normal, Error, Blind and Time
- SQL Engine to study and optimize SQL expressions
- Injection of multiple targets
- Search for administration pages
- Creation and vizualisation of Web shell and SQL shell
- Read and write files on host using injection
- Bruteforce of password's hash
- Code and decode a string

## Installation [[jsql-injection-v0.79.jar](https://github.com/ron190/jsql-injection/releases/download/v0.79/jsql-injection-v0.79.jar)]
Install [Java 8](http://java.com), then download the latest [release](https://github.com/ron190/jsql-injection/releases/) and double-click on the file `jsql-injection-v0.79.jar` to launch the software.<br>
You can also type `java -jar jsql-injection-v0.79.jar` in your terminal to start the program.<br>
If you are using Kali Linux then get the latest release using command `sudo apt-get -f install jsql`, or make a system full upgrade with `apt update` then `apt full-upgrade`.

## Screenshots and [video](https://youtu.be/ZZkQRE3OL8E)
[![Default](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/default-mini.png "Default")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/default.png)
[![Database](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/database-mini.png "Database")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/database.png)
[![SQL Engine](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.78/sqlengine-mini.png "SQL Engine")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.78/sqlengine.png)
[![Batch scan](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/scan-mini.png "Batch scan")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/scan.png)
[![Web shell](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/webshell-mini.png "Web shell")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/webshell.png)
[![SQL shell](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/sqlshell-mini.png "SQL shell")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/sqlshell.png)
[![Panels](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/panels-mini.png "Panel")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/panels.png)
[![Admin page](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/admin-mini.png "Admin page")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/admin.png)
[![File](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/file-mini.png "File")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/file.png)
[![Upload](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/upload-mini.png "Upload")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/upload.png)
[![Bruteforce](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/bruter-mini.png "Bruteforce")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/bruter.png)
[![Coder](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/coder-mini.png "Coder")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75/coder.png)

## Roadmap
`Database: Netezza, Test coverage with Jacoco, Integration test with Docker and JPA Hibernate Jooq, Reactive programming with RxJava, Maven Central: core swing cli, Full Path Disclosure, Injection strategies: DIOS RoutedQuery OOB UpdateInsertDelete, Bruteforce HTTP Auth using NTLM, Arabic translation, Command line interface, Dictionnary attack, WAF detection, Program self-updater.`

## In progress
`New SQL Structure: Dump In One Shot and Zipped, Database: MemSQL Couchbase, Dump to a file, Load from/to no. of row/char, User Agent configuration.`

## Since last release
`Database: CockroachDB Mckoi Neo4j NuoDB Hana and Vertica, Database Node renaming on F2 and Context Menu, Coldfusion injection, Translation updates: cz zh fr, New language: pl, Translation complete: ru.`

## Change log

**v0.79** `Error Strategies for MySQL and PostgreSQL compatible with Order/Group By, Wider range of Characters Insertion including multibyte %bf`

**v0.78** `SQL Engine, MySQL Error strategy: DOUBLE, Translations: es pt de it nl id, 18 Database flavors including Access`

**v0.76** `Translation: cz, 17 Database flavors including SQLite`

**v0.75** `URI injection point, Source code mavenification, Upgrade to Java 7`

**v0.73** `Authentication: Basic Digest Negotiate NTLM and Kerberos, Database flavor selection`

**v0.7** `Scan multiple URLs, Github Issue reporter, 16 Database flavors including Cubrid Derby H2 HSQLDB MariaDB and Teradata`

**alpha-v0.6** `Speed x2: No more hex encoding, 10 Database flavors including MySQL Oracle SQLServer PostgreSQL DB2 Firebird Informix Ingres MaxDb and Sybase, JUnit tests, Log4j, GUI translation`

**0.5** `SQL Shell, File Uploader`

**0.4** `Admin page finder, Bruteforce hashes like MD5 and MySQL, Encode and decode string with methods like Base64, Hex and MD5`

**0.3** `File injection, Web Shell with integrated CLI, Persistence of application parameters, Update checker`

**0.2** `Strategy Time, Multi-thread control: Start Pause Resume and Stop, Log URL calls`

**0.0-0.1** `Method GET POST Header and Cookie, Strategies Normal Error and Blind, Best strategy selection, Progression bars, Simple evasion, Proxy settings, MySQL only`

## Disclaimer
Attacking web-server is illegal without prior mutual consent. The end user is responsible and obeys all applicable laws.
Developers assume no liability and are not responsible for any misuse or damage caused by this program.
