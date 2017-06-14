#!/bin/bash
# configure ios

# if [ $# -eq 0 ]
#   then
#     echo "No arguments supplied. Provide common name(your server IP/hostname)"
#     exit 1
# fi

#function to add parameters to the subject
#$1:field  $2:subjectString
# set -x
SSL_SUBJ=''
CA_SUBJ=''
RA_SUBJ=''
SERVER_ADDRESS=''
slash='/'
equal='='

buildSubject(){
    if [ $1 = "CN" ]; then
        echo "Please provide Common Name "
        read val
        while [[ -z $val ]]; do
            echo "Common name(your server IP/hostname) cannot be null. Please enter the Common name."
            read val;
        done
        if [ -n $val ]; then
            if [ $3 = "CA" ]; then
                CA_SUBJ="$CA_SUBJ$slash$1$equal$val"
                return
                elif [ $3 = "RA" ]; then
                RA_SUBJ="$RA_SUBJ$slash$1$equal$val"
                return
                else
                SSL_SUBJ="$SSL_SUBJ$slash$1$equal$val"
                SERVER_ADDRESS=$val
                return
            fi
        fi
    fi

    echo "Please provide "$2". Press Enter to skip."
    read val;
    if [ ! -z $val ]; then
        if [ $3 = "CA" ]; then
            CA_SUBJ="$CA_SUBJ$slash$1$equal$val"
            return
        elif [ $3 = "RA" ]; then
            RA_SUBJ="$RA_SUBJ$slash$1$equal$val"
            return
        else
            SSL_SUBJ="$SSL_SUBJ$slash$1$equal$val"
            return
        fi
    fi
}
echo ''
echo '=======Enter Values for CA Subject======='

buildSubject 'C' 'Country' 'CA'
buildSubject 'ST' 'State' 'CA'
buildSubject 'L' 'Location' 'CA'
buildSubject 'O' 'Organization' 'CA'
buildSubject 'OU' 'Organizational Unit' 'CA'
buildSubject 'emailAddress' 'Email Address' 'CA'
buildSubject 'CN' 'Common Name' 'CA'

echo 'Provided CA Subject : ' $CA_SUBJ
echo 'Use same values for RA Subject? (Y/N)'
response=''
read response
if [ $response = "y" -o $response = "Y" ]; then
    RA_SUBJ=$CA_SUBJ
else
    echo ''
    echo '=======Enter Values for RA Subject======='
    buildSubject 'C' 'Country' 'RA'
    buildSubject 'ST' 'State' 'RA'
    buildSubject 'L' 'Location' 'RA'
    buildSubject 'O' 'Organization' 'RA'
    buildSubject 'OU' 'Organizational Unit' 'RA'
    buildSubject 'emailAddress' 'Email Address' 'RA'
    buildSubject 'CN' 'Common Name' 'RA'
fi

echo 'Provided RA Subject : ' $RA_SUBJ

echo 'Use same values for SSL as CA? (Y/N)'
read response
if [ $response = "y" -o $response = "Y" ]; then
    SSL_SUBJ=$CA_SUBJ
else
    echo 'Use same values for SSL as RA? (Y/N)'
    read response
    if [ $response = "y" -o $response = "Y" ]; then
        SSL_SUBJ=$RA_SUBJ
    else
        echo '=======Enter Values for SSL Subject======='
        buildSubject 'C' 'Country' 'SSL'
        buildSubject 'ST' 'State' 'SSL'
        buildSubject 'L' 'Location' 'SSL'
        buildSubject 'O' 'Organization' 'SSL'
        buildSubject 'OU' 'Organizational Unit' 'SSL'
        buildSubject 'emailAddress' 'Email Address' 'SSL'
        buildSubject 'CN' 'Common Name' 'SSL'
    fi
fi

echo 'If you have a different EMM Keystore password please enter it here. Press Enter to use the default password.'
read -s password
if [ ! -z $password ]; then
    SSL_PASS=$password
else
    SSL_PASS="wso2carbon"
fi

if [ -z $SERVER_ADDRESS ]; then
    echo 'Please enter the server address'
    read ssl_hostname
    SERVER_ADDRESS=$ssl_hostname
fi

replaceText='s/SERVER_ADDRESS/'$SERVER_ADDRESS'/g'
echo "clear output folder"
rm -f output/*

if [ ! -d ../repository/resources/security/backup_jks ]
then
    echo "folder creation"
    mkdir ../repository/resources/security/backup_jks
    cp ../repository/resources/security/client-truststore.jks ../repository/resources/security/backup_jks/
    cp ../repository/resources/security/wso2carbon.jks ../repository/resources/security/backup_jks/
    cp ../repository/resources/security/wso2certs.jks ../repository/resources/security/backup_jks/
	cp ../repository/conf/cdm-config.xml ../repository/resources/security/backup_jks/cdm-config.xml
fi


echo "backedup JKS in [EMM_HOME]/repository/resources/security/backup_jks"

echo "copying jks"
cp ../repository/resources/security/backup_jks/client-truststore.jks ./output/
cp ../repository/resources/security/backup_jks/wso2carbon.jks ./output/
cp ../repository/resources/security/backup_jks/wso2certs.jks ./output/

echo "Generating CA"
openssl genrsa -out ./output/ca_private.key 4096
openssl req -new -key ./output/ca_private.key -out ./output/ca.csr -subj $CA_SUBJ
openssl x509 -req -days 365 -in ./output/ca.csr -signkey ./output/ca_private.key -out ./output/ca.crt -extensions v3_ca -extfile ./needed_files/openssl.cnf
openssl rsa -in ./output/ca_private.key -text > ./output/ca_private.pem
openssl x509 -in ./output/ca.crt -out ./output/ca_cert.pem

echo "Generating RA"
openssl genrsa -out ./output/ra_private.key 4096
openssl req -new -key ./output/ra_private.key -out ./output/ra.csr -subj $RA_SUBJ
openssl x509 -req -days 365 -in ./output/ra.csr -CA ./output/ca.crt -CAkey ./output/ca_private.key -set_serial 02 -out ./output/ra.crt -extensions v3_req -extfile ./needed_files/openssl.cnf
openssl rsa -in ./output/ra_private.key -text > ./output/ra_private.pem
openssl x509 -in ./output/ra.crt -out ./output/ra_cert.pem

echo "Generating SSL"
openssl genrsa -out ./output/ia.key 4096
openssl req -new -key ./output/ia.key -out ./output/ia.csr  -subj $SSL_SUBJ
openssl x509 -req -days 730 -in ./output/ia.csr -CA ./output/ca_cert.pem -CAkey ./output/ca_private.pem -set_serial 044324343 -out ./output/ia.crt

echo "Export to PKCS12"
openssl pkcs12 -export -out ./output/KEYSTORE.p12 -inkey ./output/ia.key -in ./output/ia.crt -CAfile ./output/ca_cert.pem -name "wso2carbon" -password pass:$SSL_PASS
openssl pkcs12 -export -out ./output/ca.p12 -inkey ./output/ca_private.pem -in ./output/ca_cert.pem -name "cacert" -password pass:$SSL_PASS
openssl pkcs12 -export -out ./output/ra.p12 -inkey ./output/ra_private.pem -in ./output/ra_cert.pem -chain -CAfile ./output/ca_cert.pem -name "racert" -password pass:$SSL_PASS

echo "Export PKCS12 to JKS"
keytool -importkeystore -srckeystore ./output/KEYSTORE.p12 -srcstoretype PKCS12 -destkeystore ./output/wso2carbon.jks -deststorepass wso2carbon -srcstorepass wso2carbon -noprompt
keytool -importkeystore -srckeystore ./output/KEYSTORE.p12 -srcstoretype PKCS12 -destkeystore ./output/client-truststore.jks -deststorepass wso2carbon -srcstorepass wso2carbon -noprompt
keytool -importkeystore -srckeystore ./output/ca.p12 -srcstoretype PKCS12 -destkeystore ./output/wso2certs.jks -deststorepass wso2carbon -srcstorepass wso2carbon -noprompt
keytool -importkeystore -srckeystore ./output/ra.p12 -srcstoretype PKCS12 -destkeystore ./output/wso2certs.jks -deststorepass wso2carbon -srcstorepass wso2carbon -noprompt

echo "Created all certificates, configuration files and agent. Copying to pack..."
cp output/client-truststore.jks ../repository/resources/security/
cp output/wso2carbon.jks ../repository/resources/security/
cp output/wso2certs.jks ../repository/resources/security/
cp needed_files/ios-config.xml output/
sed -i -e $replaceText output/ios-config.xml
cp output/ios-config.xml ../repository/conf/
cp needed_files/certificate-config.xml ../repository/conf/
cp needed_files/ios-agent.ipa ../repository/deployment/server/jaggeryapps/emm-web-agent/app/pages/mdm.page.enrollments.ios.download-agent/public/asset

echo "Generating Certificate for JWT"
keytool -export -keystore ../repository/resources/security/wso2carbon.jks -alias wso2carbon -file ./output/wso2carbon.cer -srcstorepass wso2carbon -deststorepass wso2carbon

echo "Adding push PushNotificationProviders to cdm-config.xml"
cp ../repository/resources/security/backup_jks/cdm-config.xml output/cdm-config.xml
sed 's/<PushNotificationProviders>/<PushNotificationProviders><Provider>org.wso2.carbon.device.mgt.mobile.impl.ios.apns.APNSBasedPushNotificationProvider<\/Provider>/g' output/cdm-config.xml
cp output/cdm-config.xml ../repository/conf/

echo "Completed!!"
