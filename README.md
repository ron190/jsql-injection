## Description
**jSQL Injection** is a lightweight application used to find database information from a distant server.

jSQL is **free**, **open source** and **cross-platform** (Windows, Linux, Mac OS X).

![Kali Linux logo](https://github.com/ron190/jsql-injection/raw/master/web/images/kali_favicon.png "Kali Linux logo") jSQL is part of **[Kali Linux](http://www.kali.org/)**, the official penetration testing distribution.

jSQL is also included in distributions like [Pentest Box](https://pentestbox.com/), [Parrot Security OS](https://www.parrotsec.org), [ArchStrike](https://archstrike.org/) and [BlackArch Linux](http://www.blackarch.org/).

[![License: GPLv2](https://img.shields.io/badge/License-GPLv2-blue.svg?maxAge=2592000)](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)
[![Java](https://img.shields.io/badge/Java-7-orange.svg?maxAge=2592000)](http://www.oracle.com/technetwork/java/javase/jdk7-relnotes-418459.html#changes)
[![SonarQube](https://img.shields.io/badge/SonarQube-5.6-000000.svg?maxAge=2592000)](http://www.sonarqube.org/)
[![JUnit](https://img.shields.io/badge/JUnit-4.11-4E9A06.svg?maxAge=2592000)](http://junit.org)
[![Maven](https://img.shields.io/badge/Maven-3.2-AB215A.svg?maxAge=2592000)](https://maven.apache.org/)

## Installation
Install [Java](http://java.com), then download the latest [release](https://github.com/ron190/jsql-injection/releases/download/v0.74/jsql-injection-v0.74.jar) and double-click on the .jar to launch the software.<br>
You can also type `java -jar jsql-injection-v0.74.jar` in your terminal.

## Screenshot of v0.74 [[download here](https://github.com/ron190/jsql-injection/releases)]
[![jSQL v0.75](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75-mini.png "jSQL v0.75")](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.75.png)
## Change log
**Roadmap** `New databases (SQLite, Access, Neo4j), Command-line interface, Translation`

**Coming v0.75** `URI injection point, WAF tamper, HTTP auth brute force, mavenify, upgrade to Java 7`

**v0.73** `Authentication Basic Digest Negotiate NTLM and Kerberos, database type selection`

**v0.7** `Batch scan, Github issue reporter, support for 16 db engines, optimized GUI`

**alpha-v0.6** `Speed x 2 (no more hex encoding), 10 db vendors supported: MySQL Oracle SQLServer PostgreSQL DB2 Firebird Informix Ingres MaxDb Sybase. JUnit tests, log4j, i18n integration and more.`

## Screenshots of v0.5 [[download here](https://code.google.com/p/jsql-injection/downloads/list)]
[![Database reader](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-database-mini.png "Database reader")](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-database.png)
[![Admin page finder](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-admin-mini.png "Admin page finder")](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-admin.png)
[![File reader](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-file-mini.png "File reader")](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-file.png)
[![Webshell execution](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-webshell-mini.png "Webshell execution")](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-webshell.png)
[![SQLshell execution](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-sqlshell-mini.png "SQLshell execution")](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-sqlshell.png)
[![Upload file](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-upload-mini.png "Upload file")](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-upload.png)
[![Bruteforce hash](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-bruteforce-mini.png "Bruteforce hash")](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-bruteforce.png)
[![Code/Encode string](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-coder-mini.png "Code/Encode string")](https://github.com/ron190/jsql-injection/raw/master/web/images/201309272136-screenshot-coder.png)
## Change log
**0.5** `SQL shell, Uploader.`

**0.4** `Admin page search, Brute force (md5 mysql...), Decoder (decode encode base64 hex md5...).`

**0.3** `Distant file reader, Webshell drop, Terminal for webshell commands, Configuration backup, Update checker.`

**0.2** `Time based algorithm, Multi-thread control (start pause resume stop), Shows URL calls.`

**0.0-0.1** `Methods GET / POST / header / cookie, Algorithms Normal / Error / Blind, Automatic best algorithm selection, Progression bars, Simple evasion, Proxy setting, Supports MySQL.`

## Disclaimer
Attacking web-server is illegal without prior mutual consent. The end user is responsible and obeys all applicable laws.
Developers assume no liability and are not responsible for any misuse or damage caused by this program.