WSO2 Enterprise Mobility Manager (EMM)
----------------------
Welcome to the WSO2 Enterprise Mobility Manager (EMM) 2.1.0-ALPHA release
=======

Key Features
------------
1.  Self-service device enrollment and management with end-user EMM console


Installation & Running
----------------------
1. Extract the downloaded zip file
2. Run the wso2server.sh or wso2server.bat file in the bin directory
3. Once the server starts, point your Web browser to
   https://localhost:9443/
6. Please find the maven-archetype in <PRODUCT_HOME>repository/tools to generate the mdm-android-agent project for any specific changes from the default agent provided.
    Make sure to follow the README file.
7. Go to EMM admin console.
    Add a license with following properties.
    Ex -: Provider -: Any text
             Name -: android
             Language -: en-us
             Valid From -: date in {dd-mm-yyyy} format.
  	     Valid To -: date in {dd-mm-yyyy} format.
             License -: License Text

8. Configure email account.
   Go to Following path and open CARBON_HOME/repository/conf/axis2 and open axis2.xml.
   Un comment following entry and give correct credentials for email account.

 <transportSender name="mailto"
                     class="org.apache.axis2.transport.mail.MailTransportSender">
        <parameter name="mail.smtp.from">wso2demomail@gmail.com</parameter>
        <parameter name="mail.smtp.user">wso2demomail</parameter>
        <parameter name="mail.smtp.password">mailpassword</parameter>
        <parameter name="mail.smtp.host">smtp.gmail.com</parameter>

        <parameter name="mail.smtp.port">587</parameter>
        <parameter name="mail.smtp.starttls.enable">true</parameter>
        <parameter name="mail.smtp.auth">true</parameter>
    </transportSender>

9. In case if you need to use iOS device management, please install following features using carbon admin features. This
   features are not packed by default with the server. You will have to ask by contacting us by
   http://wso2.com/products/enterprise-mobility-manager/csr/
       EMM - iOS APNS Server.
       EMM - iOS Device Management JAX-RS Web Application.
       EMM - iOS Device Management Server.

10. Change Urls and settings in ios-config.xml found in CARBON_HOME/repository/conf.

11. Follow the instructions in following document to generate keys.
    https://docs.wso2.com/display/EMM200/iOS+Server+Configurations


For more details, see the Installation Guide

System Requirements
-------------------

1. Minimum memory - 2GB
2. Portal app requires full Javascript enablement of the Web browser

For known issues see
https://wso2.org/jira/issues/?filter=12593

WSO2 Enterprise Mobility Manager (EMM) Binary Distribution Directory Structure
-----------------------------------------------------

  EMM_HOME
        .
        ├── bin              //executables
        ├── dbscripts        //DBScripts
        ├── lib
        ├── LICENSE.txt
        ├── modules          //Jaggery Modules
        ├── README.txt
        ├── release-notes.html
        ├── repository       // repository
        ├── tmp
        ├── resources


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
      data services and axis1services are also stored.

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

        - tools
          Contains the Script to generate WSO2 MDM Android agent.

        - tenants
          Directory will contain relevant tenant artifacts
          in the case of a multitenant deployment.

    - tmp
      Used for storing temporary files, and is pointed to by the
      java.io.tmpdir System property.

    - LICENSE.txt
      Apache License 2.0 under which WSO2 Carbon is distributed.

    - README.txt
      This document.

    - release-notes.html
      Release information for WSO2 Enterprise Mobility Manager 2.0.0-SNAPSHOT

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
(c) Copyright 2015 WSO2 Inc.
