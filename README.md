## Description
**jSQL Injection** is a lightweight application used to find database information from a server.

It's **free**, **open source** and **cross-platform** for Windows, Linux and Mac and it works with Java from version 11 to 23.

jSQL Injection is also part of the official penetration testing distribution [Kali Linux](https://www.kali.org) and is included in various other distributions like [SnoopGod](https://snoopgod.com), [Pentest Box](https://pentestbox.com), [Parrot Security OS](https://www.parrotsec.org), [ArchStrike](https://archstrike.org) and [BlackArch Linux](http://www.blackarch.org).

[![Java](https://img.shields.io/badge/java-11%20to%2023-orange?logo=java "Version range compatibility")](http://www.oracle.com/technetwork/java/javase/downloads/)
[![JUnit](https://img.shields.io/badge/junit-5-50940f "Tests")](http://junit.org)
[![Maven](https://img.shields.io/badge/maven-3.6-a2265a "Build")](https://maven.apache.org/)
[![License](https://img.shields.io/github/license/ron190/jsql-injection "License")](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)<br>
[![GitHub](https://img.shields.io/badge/build-blue?logo=github "Github Actions status")](https://github.com/ron190/jsql-injection/actions)
[![Codecov](https://img.shields.io/codecov/c/github/ron190/jsql-injection?label=coverage&logo=codecov "Codecov test coverage")](https://codecov.io/gh/ron190/jsql-injection)
[![Codacy](https://img.shields.io/codacy/grade/e7ccb247f9b74d489a1fa9f9483c978f?label=quality&logo=codacy "Codacy code quality")](https://app.codacy.com/gh/ron190/jsql-injection/dashboard)
[![Snyk](https://img.shields.io/badge/build-monitored-8A2BE2?logo=snyk&label=security "Snyk code vulnerability")](#)<br>
[![Sonar](https://img.shields.io/sonar/violations/ron190:jsql-injection?format=long&label=issues&logo=sonarqube&server=https%3A%2F%2Fsonarcloud.io "Sonar code issues")](https://sonarcloud.io/dashboard?id=ron190%3Ajsql-injection)

## Wiki
Read about jSQL [features](https://github.com/ron190/jsql-injection/wiki/Features) and more in the [wiki](https://github.com/ron190/jsql-injection/wiki).

For programmers, access the generated [Maven reports](https://ron190.github.io/jsql-injection/site/) and [Sonar analysis](https://sonarcloud.io/dashboard?id=ron190%3Ajsql-injection) to analyze internal metrics, and open the [programming section](https://github.com/ron190/jsql-injection/wiki/Programming-jSQL) in the wiki for more details.

## Install
First, install :coffee: [Java](http://java.com) 11 or up to version 23, then download the latest jSQL [release](https://github.com/ron190/jsql-injection/releases/) and double-click on the file `jsql-injection-v0.103.jar` to run the software.

You can also type `java -jar jsql-injection-v0.103.jar` in your terminal to start the program.

If you are using Kali Linux then get the latest version using command `sudo apt-get -f install jsql`, or make a system full upgrade with `apt update` then `apt full-upgrade`.

> [!NOTE]
> Download the latest version from GitHub: [jsql-injection-v0.103.jar](https://github.com/ron190/jsql-injection/releases/download/v0.103/jsql-injection-v0.103.jar)
___
## Screenshots
[<img src=https://github.com/ron190/jsql-injection/raw/master/web/images/app/theme-light.png width=404/>](https://github.com/ron190/jsql-injection/raw/master/web/images/app/theme-light.png)
[<img src=https://github.com/ron190/jsql-injection/raw/master/web/images/app/theme-dark.png width=404/>](https://github.com/ron190/jsql-injection/raw/master/web/images/app/theme-dark.png)
[<img src=https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/database-dark.png width=200/>](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/database-dark.png)
[<img src=https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/database.png width=200/>](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/database.png)
[<img src=https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/tamper.png width=200/>](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/tamper.png)
[<img src=https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/sqlengine.png width=200/>](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/sqlengine.png)
[<img src=https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/batch.png width=200/>](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/batch.png)
[<img src=https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/admin.png width=200/>](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/admin.png)
[<img src=https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/shell.png width=200/>](https://github.com/ron190/jsql-injection/raw/master/web/images/v0.102/shell.png)
___
## Disclaimer
> [!NOTE]
> Attacking web-server is illegal without prior mutual consent. The end user is responsible and obeys all applicable laws.
> Developers assume no liability and are not responsible for any misuse or damage caused by this program.
