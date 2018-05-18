# vertretungsplan-uploader

[![Build Status](https://travis-ci.org/vertretungsplanme/vertretungsplan-uploader.svg?branch=master)](https://travis-ci.org/vertretungsplanme/vertretungsplan-uploader)
[![Build status](https://ci.appveyor.com/api/projects/status/uq4paoow43ktcwyc?svg=true)](https://ci.appveyor.com/project/johan12345/vertretungsplan-uploader)
[![codecov](https://codecov.io/gh/vertretungsplanme/vertretungsplan-uploader/branch/master/graph/badge.svg)](https://codecov.io/gh/vertretungsplanme/vertretungsplan-uploader)

<a href="https://vertretungsplan.app"><img style="vertical-align:middle" src="https://vertretungsplan.app/img/vertretungsplan_icon_color.svg" width=80/></a>

What is this?
-------------

This is a cross-platform desktop application that allows schools to upload their substitution schedules (Vertretungsplan
 / Supplierplan) to a web server. Currently, only the FTP protocol is implemented, but others (e.g. SFTP) can easily be
 added. It is intended to be used together with our [mobile app](https://vertretungsplan.app/) that can fetch the
 schedules from the school's server. In the future, we will add an option to notify our server when the school has
 updated their schedule, so that changes are reflected in the app instantly.

The application is written in [Kotlin](https://kotlinlang.org/) using [tornadofx](https://github.com/edvin/tornadofx)
and [JFoenix](https://github.com/jfoenixadmin/JFoenix).

Project status
--------------

vertretungsplan-uploader is still in development and has not been tested in production


License
-------
The code in this repository is published under the terms of the GPLv3 License.
See the LICENSE file for the complete license text.
