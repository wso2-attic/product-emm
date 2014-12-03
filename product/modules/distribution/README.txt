WSO2 Enterprise Mobility Manager (EMM)
----------------------
Welcome to the WSO2 Enterprise Mobility Manager (EMM) ${product.version} release
=======

Key Features
------------
1.  Self-service device enrollment and management with end-user MDM console
2.  Policy-driven device management for security, data, and device features (Camera, Password Policy)
3.  Deploy policies over-the-air 
4.  Compliance monitoring for reporting, alerting, and device deprovisioning
5.  Role based permissions for device management
6.  Provisioning and deprovisioning applications to enrolled devices
7.  Blacklisting of applications for Android
8.  Supports App management
9.  App approval process through a lifecycle
10. Discover mobile apps through an Enterprise App Store
11. Self-provisioning of mobile apps to devices


Installation & Running
----------------------
1. extract the downloaded zip file
2. Run the wso2server.sh or wso2server.bat file in the bin directory
3. Once the server starts, point your Web browser to
   https://localhost:9443/

For more details, see the Installation Guide

System Requirements
-------------------

1. Minimum memory - 2GB
2. Portal app requires full Javascript enablement of the Web browser

For more details see
https://docs.wso2.org/display/EMM110/Prerequisites

For known issues see
https://wso2.org/jira/issues/?filter=11791

WSO2 Enterprise Mobility Manager (EMM) Binary Distribution Directory Structure
-----------------------------------------------------

  EMM_HOME
        .
        ├── bin              //executables
        ├── dbscripts        //DBScripts
        ├── INSTALL.txt
        ├── lib
        ├── LICENSE.txt
        ├── modules          //Jaggery Modules
        ├── README.txt
        ├── release-notes.html
        ├── repository       // repository
        ├── tmp
        ├── webapp-mode


    - bin
      Contains various scripts .sh & .bat scripts.

    - dbscripts
      Contains the database creation & seed data population SQL scripts for
      various supported databases.

    - lib
      Contains the basic set of libraries required to startup WSO2 Enterprise Mobility Manager
      in standalone mode

    - repository
      The repository where Carbon artifacts & Axis2 services and
      modules deployed in WSO2 Carbon are stored.
      In addition to this other custom deployers such as
      dataservices and axis1services are also stored.

        - carbonapps
          Carbon Application hot deployment directory.

      - components
          Contains all OSGi related libraries and configurations.

        - conf
          Contains server configuration files. Ex: axis2.xml, carbon.xml

        - data
          Contains internal LDAP related data.

        - database
          Contains the WSO2 Registry & User Manager database.

        - deployment
          Contains server side and client side Axis2 repositories.
        All deployment artifacts should go into this directory.

        - logs
          Contains all log files created during execution.

        - resources
          Contains additional resources that may be required.

  - tenants
    Directory will contain relevant tenant artifacts
    in the case of a multitenant deployment.

    - tmp
      Used for storing temporary files, and is pointed to by the
      java.io.tmpdir System property.

    - webapp-mode
      The user has the option of running WSO2 Carbon in webapp mode (hosted as a web-app in an application server).
      This directory contains files required to run Carbon in webapp mode.

    - LICENSE.txt
      Apache License 2.0 under which WSO2 Carbon is distributed.

    - README.txt
      This document.

    - INSTALL.txt
      This document contains information on installing WSO2 Enterprise Mobility Manager.

    - release-notes.html
      Release information for WSO2 Enterprise Mobility Manager ${product.version}

Secure sensitive information in carbon configuration files
----------------------------------------------------------

There are sensitive information such as passwords in the carbon configuration.
You can secure them by using secure vault. Please go through following steps to
secure them with default mode.

1. Configure secure vault with default configurations by running ciphertool
  script from bin directory.

> ciphertool.sh -Dconfigure   (in UNIX)

This script would do following configurations that you need to do by manually

(i) Replaces sensitive elements in configuration files,  that have been defined in
     cipher-tool.properties, with alias token values.
(ii) Encrypts plain text password which is defined in cipher-text.properties file.
(iii) Updates secret-conf.properties file with default keystore and callback class.

cipher-tool.properties, cipher-text.properties and secret-conf.properties files
      can be found at repository/conf/security directory.

2. Start server by running wso2server script from bin directory

> wso2server.sh   (in UNIX)

By default mode, it would ask you to enter the master password
(By default, master password is the password of carbon keystore and private key)

3. Change any password by running ciphertool script from bin directory.

> ciphertool -Dchange  (in UNIX)

For more details see
http://docs.wso2.org/wiki/display/Carbon410/WSO2+Carbon+Secure+Vault

Training
--------

WSO2 Inc. offers a variety of professional Training Programs, including
training on general Web services as well as WSO2 Enterprise Store, Apache Axis2,
Data Services and a number of other products.

For additional support information please refer to
http://wso2.com/training/


Support
-------

We are committed to ensuring that your enterprise middleware deployment is completely supported
from evaluation to production. Our unique approach ensures that all support leverages our open
development methodology and is provided by the very same engineers who build the technology.

For additional support information please refer to http://wso2.com/support/

---------------------------------------------------------------------------
(c) Copyright 2013 WSO2 Inc.
