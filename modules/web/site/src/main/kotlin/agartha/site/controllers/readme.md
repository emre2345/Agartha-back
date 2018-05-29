# WebServer Controllers

This directory contains controllers for WebServer API requests
These controllers are imported Spark server in Server.kt

Each controller has a Service indicating from where data should be read. F.x. PractitionerController takes an IPractitionerService as parameter where the MongoPractitionerService is for production and MockedPractitionerService is for test.

