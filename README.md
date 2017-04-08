## Description
**jSQL Injection** is a lightweight application used to find database information from a distant server.

It is **free**, **open source** and **cross-platform** (Windows, Linux, Mac OS X).

![Kali Linux logo](https://github.com/ron190/jsql-injection/raw/master/web/images/kali_favicon.png "Kali Linux logo") jSQL Injection is also part of the official penetration testing distribution [Kali Linux](http://www.kali.org/) and is included in distributions like [Pentest Box](https://pentestbox.com/), [Parrot Security OS](https://www.parrotsec.org), [ArchStrike](https://archstrike.org/) or [BlackArch Linux](http://www.blackarch.org/).

[![Java 8](https://github.com/ron190/jsql-injection/raw/master/web/images/image.io/java.svg)](http://www.oracle.com/technetwork/java/javase/downloads/)
[![License GPLv2](https://github.com/ron190/jsql-injection/raw/master/web/images/image.io/license.svg)](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)
[![JUnit 4.11](https://github.com/ron190/jsql-injection/raw/master/web/images/image.io/junit.svg)](http://junit.org)
[![Maven 3.2](https://github.com/ron190/jsql-injection/raw/master/web/images/image.io/maven.svg)](https://maven.apache.org/)
[![SonarQube 6.3](https://github.com/ron190/jsql-injection/raw/master/web/images/image.io/sonar.svg)](http://www.sonarqube.org/)<br>
[![Twitter Follow](https://img.shields.io/twitter/follow/ron190jsql.svg?style=social&label=ron190)](https://twitter.com/ron190jsql)
[![Github](github.com/ron190/jsql-injection/raw/master/web/images/image.io/github.svg)](https://github.com)
[![Github All Releases](https://img.shields.io/github/downloads/ron190/jsql-injection/total.svg)](https://github.com/ron190/jsql-injection/releases/)
[![SourceForge](github.com/ron190/jsql-injection/raw/master/web/images/image.io/sourceforge.svg)](https://sourceforge.net)
[![SourceForge All Releases](https://img.shields.io/sourceforge/dw/jsqlinjection.svg)](https://sourceforge.net/projects/jsqlinjection/files/)

## Features
- Automatic injection of 18 kinds of databases: Access, CUBRID, DB2, Derby, Firebird, H2, HSQLDB, Informix, Ingres, MariaDB, MaxDB, MySQL, Oracle, PostgreSQL, SQLite, SQL Server, Sybase and Teradata
- Multiple injection strategies: Normal, Error, Blind and Time
- SQL Engine to study and optimize SQL expressions
- Creation and vizualisation of Web shell and SQL shell
- Search for administration pages
- Read files from the host
- Bruteforce hash of password
- Code and decode a string
- Community translation system: ar, cs, de, es, fr, in_ID, it, nl, pt, ru, tr, zh

## Installation
Install [Java 8](http://java.com), then download the latest [release](https://github.com/ron190/jsql-injection/releases/) of jSQL and double-click on the .jar to launch the software.<br>
You can also type `java -jar jsql-injection-v0.78.jar` in your terminal to start the program.

## v0.78 [[download here](https://github.com/ron190/jsql-injection/releases/download/v0.78/jsql-injection-v0.78.jar)]
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
`Injection strategies: Routed query / Multibyte / User Variable / Order and Group By, WAF detection, Bruteforce of HTTP Auth, Arabic translation, Command line interface.`

## Change log

**v0.78** `SQL Engine, MySQL Error strategy: BIGINT, Translations: es pt de it nl id, GUI improvements, Database flavor: Access`

**v0.76** `Czech translation, 17 Database flavors including SQLite`

**v0.75** `URI injection point, Mavenify, Upgrade to Java 7, Optimized UI`

**v0.73** `Authentication: Basic Digest Negotiate NTLM and Kerberos, Database flavor selection`

**v0.7** `Scan multiple URLs, Github Issue reporter, 16 Database flavors including Cubrid Derby H2 HSQLDB MariaDB and Teradata, Optimized UI`

**alpha-v0.6** `Speed x2: No hex encoding, 10 Database flavors including MySQL Oracle SQLServer PostgreSQL DB2 Firebird Informix Ingres MaxDb and Sybase, JUnit tests, Log4j, Translation`

**0.5** `SQL Shell, Uploader`

**0.4** `Admin page, Hash bruteforce like MD5 and MySQL, Text encoder/decoder like Base64, Hex and MD5`

**0.3** `File injection, Web Shell, Integrated terminal, Configuration backup, Update checker`

**0.2** `Algorithm Time, Multi-thread control: Start Pause Resume and Stop, Log URL calls`

**0.0-0.1** `Method GET POST Header and Cookie, Algorithm Normal Error and Blind, Best algorithm selection, Progression bars, Simple evasion, Proxy settings, MySQL only`

## Disclaimer
Attacking web-server is illegal without prior mutual consent. The end user is responsible and obeys all applicable laws.
Developers assume no liability and are not responsible for any misuse or damage caused by this program.
