# openliberty-db2cloud-docker
This is an example client Java library to connect to DB2 on IBM Cloud.

This example is based on the [Open Liberty guide for JPA](https://openliberty.io/guides/jpa-intro.html) and [Niklas Heildoff's article - Persistence for Java Microservices in Kubernetes Via JPA](https://dzone.com/articles/persistence-for-java-microservices-in-kubernetes-v)

## Step 1 
Clone this repository or download the zip file:

```
git clone https://github.com/blumareks/openliberty-db2cloud-docker.git
cd openliberty-db2cloud-docker
```

## Step 2
In order to proceed you need to use `maven` to build the `war` file with the demo application 
- there are 2 subdirectories: `backendServices` and `frontendUI`
- the backend services will be running as a Docker container
- the frontend UI will be running natively  

```
cd backendServices
mvn package
```

## Step 2a - JDBC library
The jcc library for the DB2 connectivity is obtained via maven, and copied inside Dockerfile commands.

## (optional) Step 2b - the DB2 SSL certificate
**This step is optional, since Open Liberty server has already the certificate to the DB2 included.**

This instructions are based on the method 1 from here: https://www.ibm.com/support/pages/complete-guide-set-ssl-using-ibm-data-server-driver-jdbc-and-sqlj

A DB2 admin should be able to provide a SSL certifcate - the certificate can be downloaded from the DB2 Web console from the Connection Information section.

Move the certificate to the target directory.

```
cd openliberty-db2cloud-docker/backendServices/target
cp ~/Downloads/DigiCertGlobalRootCA.crt . 
```

The rest of the setup will happen when building the image with Docker. 
You will need to add the following lines:

  #SSL
  COPY --chown=1001:0 target/DigiCertGlobalRootCA.crt /config/
  RUN keytool -import -trustcacerts -alias myalias -file /config/DigiCertGlobalRootCA.crt -keystore myTrustStore.jks -storepass changeit
  RUN keytool -list -v -keystore myTrustStore.jks

However attempt to add already existing certificate in the Open Liberty global trust store will result with the following warning:

```
RUN keytool -import -trustcacerts -alias myalias -file /config/DigiCertGlobalRootCA.crt -keystore myTrustStore.jks -storepass changeit
 ---> Running in 589cbf0607a5
Certificate already exists in system-wide CA keystore under alias <digicertglobalrootca [jdk]>
Do you still want to add it to your own keystore? [no]:  Certificate was not added to keystore
```

The `docker build` command will create a trust store and add the server certificate to the trust store. Trust store is a place where the client will store all the certificate it trusts. It will use keytool provided by JDK for this. Keytool will be usually present in path java_installation_path/jdk64/bin. Below command will create a truststore “myTrustStore.jks” if it doesn’t exist and add server certificate “mydbserver.arm” into it.

```
keytool -import -trustcacerts -alias myalias -file mydbserver.arm -keystore       myTrustStore.jks
```

You can verify the presence of the certificate in the TrustStore by executing the following command.
```              
keytool -list -v -keystore myTrustStore.jks
```

In the server.xml file the following elements needs to be updated when using SSL connection:
- Set JCC parameter sslConnection to true. sslConnection parameter can be set through Datasource or global properties file or through URL: `ds.setSslConnection(true); (Datasource)`
-  or `sslConnection=true (global properties file)`

If using the custom truststore file you  need to set parameter `sslTrustStoreLocation` to the path where trust stored is placed and set `sslTrustStorePassword` to the password of the truststore. sslTrustStoreLocation and sslTrustStorePassword parameters can be set through Datasource or global properties file or through URL.
              
```
 ds.setSslTrustStoreLocation("C:/Security/SSL/certificates/mynewdbclient.jks");
 ds.setSslTrustStorePassword("password"); (Datasource)
```
 or
```
 sslTrustStoreLocation=C:/Security/SSL/certificates/mynewdbclient.jks
 sslTrustStroePassword=password  (global properties file)
```

Optional: The version of the SSL & TLS protocols used will be decided by the JRE used in the application. But if we want to set the version to a different level than the default, we need to set “sslVersion” parameter as shown below.
- `ds.setSslVersion("TLSv1.1"); (Datasource)`
- or `sslVersion=TLSv1.1 (global properties file)`


## (optional) Step 2c - the DB2 SSL certificate and a client certificate

### generating a client certificate
Generate client certificate, create a KeyStore and add the client certificate to the KeyStore. KeyStore is a place for the client to store its own certificate so that it can provide when requested by servers. The following key command will take care of all 3 steps.

```
keytool -genkey -keyalg rsa -keystore clientkeystore.jks -storepass password -alias client_cert
```

The command will ask for details to include in the certificate such as first name, last name, organization unit, organization, city, state & country code. When prompted with ‘Enter key password for’, press Enter to use the same password as the KeyStore password.

**Note:** *It is not recommended to use same JKS as both TrustStore and KeyStore, since KeyStore will contain private key also in addition to client certificate. By using same JKS for both purpose we will be compromising the security of private key.*

### setting the securityMechanism, sslKeyStoreLocation, sslKeyStorePassword
Set parameter securityMechanism to 18 (DB2BaseDataSource.TLS_CLIENT_CERTIFICATE_SECURITY). This can be done through datasource or global properties file or through URL.

`ds.setSecurityMechanism((com.ibm.db2.jcc.DB2BaseDataSource.TLS_CLIENT_CERTIFICATE_SECURITY));` (Datasource)
or 
`securityMechanism=18` (global properties file)

Set parameter `sslKeystoreLocation` to the path where keystore is present. And set `sslKeyStorePassword` to the key store password.

- (Datasource):

```
ds.setSslKeyStoreLocation("C:/Security/SSL/certificates/clientkeystore.jks");
ds.setSslKeyStorePassword("password");
```
    
or
- (global properties file)

```
sslKeyStoreLocatoin=C:/Security/SSL/certificates/clientkeystore.jks
sslKeyStorePassword=password     
```

**Note:** *sslKeystoreLocation and sslKeyStorePassword are supported in JCC4 and not in JCC3. If you are using JCC3 then these properties should be set using system properties.
`System.setProperty("javax.net.ssl.keyStore","C:/Security/certificates/mykeystore");`
`System.setProperty("javax.net.ssl.keyStorePassword","123456");`  *


## Step 3
When the `war` file is available, now you can use `docker` to build your image `(mind the trailing period: ".")`: 

```
docker image build -t <your docker id>/open-liberty-db2:0.9 .
```

## Step 4
You can run the docker container, and access the properties page. Collect the data from DB2 cloud service or DB2 server - alternatively create DB2 Cloud based service here (`free!`): https://cloud.ibm.com/catalog/services/db2 
After creating the data you need to create/copy the service credentials. The credentials would look like this:

- Standard DB2:
These are the typical service credentials that might be obtained through the service detail page in IBM Cloud (edited to remove the actual certificates/users/password):
```JSON
{
  "apikey": "api key",
  "connection": {
    "cli": {
      "arguments": [
        [
          "-u",
          "my user",
          "-p",
          "my password",
          "--ssl",
          "--sslCAFile",
          "some id",
          "--authenticationDatabase",
          "admin",
          "--host",
          "generated-host.generated-subdomain.databases.appdomain.cloud:30847"
        ]
      ],
      "bin": "db2",
      "certificate": {
        "certificate_base64": "my cert data",
        "name": "my-cert"
      },
      "composed": [
        "db2 -u user -p password --ssl --sslCAFile my-cert --authenticationDatabase admin --host generated-host.generated-subdomain.databases.appdomain.cloud:30847"
      ],
      "environment": {},
      "type": "cli"
    },
    "db2": {
      "authentication": {
        "method": "direct",
        "password": "direct-password",
        "username": "direct-user"
      },
      "certificate": {
        "certificate_base64": "direct cert data",
        "name": "my-direct-cert"
      },
      "composed": [
        "db2://generated-host.generated-subdomain.databases.appdomain.cloud:30847/bludb?authSource=admin&replicaSet=replset"
      ],
      "database": "bludb",
      "host_ros": [
        "generated-host.generated-subdomain.databases.appdomain.cloud:32735"
      ],
      "hosts": [
        {
          "hostname": "generated-host.generated-subdomain.databases.appdomain.cloud",
          "port": 30847
        }
      ],
      "jdbc_url": [
        "jdbc:db2://generated-host.generated-subdomain.databases.appdomain.cloud:30847/bludb:user=<userid>;password=<your_password>;sslConnection=true;"
      ],
      "path": "/bludb",
      "query_options": {
        "authSource": "admin",
        "replicaSet": "replset"
      },
      "replica_set": "replset",
      "scheme": "db2",
      "type": "uri"
    }
  },
  "iam_apikey_description": "Auto-generated for key your-key",
  "iam_apikey_name": "Service credentials-fiserv",
  "iam_role_crn": "crn:v1:bluemix:public:iam::::serviceRole:Manager",
  "iam_serviceid_crn": "crn:v1:bluemix:public:iam-identity::a/iam-identity::serviceid:ServiceId",
  "instance_administration_api": {
    "deployment_id": "crn:v1:bluemix:public:dashdb-for-transactions:us-south:a/iam-identity:ident-pass::",
    "instance_id": "crn:v1:bluemix:public:dashdb-for-transactions:us-south:a/iam-identity:ident-pass::",
    "root": "https://api.us-south.db2.cloud.ibm.com/v4/ibm"
  }
}
```

- LITE DB2 version
```json
{
  "db": "BLUDB",
  "dsn": "DATABASE=BLUDB;HOSTNAME=some-name-xyz.bluemix.net;PORT=50000;PROTOCOL=TCPIP;UID=your-user;PWD=your-password;",
  "host": "some-name-xyz.bluemix.net",
  "hostname": "some-name-xyz.bluemix.net",
  "https_url": "https://some-name-xyz.bluemix.net",
  "jdbcurl": "jdbc:db2://some-name-xyz.bluemix.net:50000/BLUDB",
  "parameters": {
    "role_crn": "crn:v1:bluemix:public:iam::::serviceRole:Manager"
  },
  "password": "your-password",
  "port": 50000,
  "ssldsn": "DATABASE=BLUDB;HOSTNAME=some-name-xyz.bluemix.net;PORT=50001;PROTOCOL=TCPIP;UID=your-user;PWD=your-password;Security=SSL;",
  "ssljdbcurl": "jdbc:db2://some-name-xyz.bluemix.net:50001/BLUDB:sslConnection=true;",
  "uri": "db2://your-user:your-password@some-name-xyz.bluemix.net:50000/BLUDB",
  "username": "your-user"
}
```

Then you are ready to fillout the environmental variables and run your docker container:

- `SSL - port 50001`, and `DB2_SSL=true`

```
docker run  -d -p5050:5050 -p5051:5051 \
-e DB2_DBNAME="BLUDB" \
-e DB2_PASSWORD="your-password" \
-e DB2_PORT=50001 \
-e DB2_HOST="some-name-xyz.bluemix.net" \
-e DB2_USER="your-user"  \
-e DB2_SSL="true"
<your-docker-id>/open-liberty-db2:0.8
```

- `NO SSL - port 50000`, and `DB2_SSL=false`

```
docker run  -d -p5050:5050 -p5051:5051 \
-e DB2_DBNAME="BLUDB" \
-e DB2_PASSWORD="your-password" \
-e DB2_PORT=50000 \
-e DB2_HOST="some-name-xyz.bluemix.net" \
-e DB2_USER="your-user"  \
-e DB2_SSL="false"
<your-docker-id>/open-liberty-db2:0.8
```

In the seperate terminal run the frontend UI server:
```
cd frontendUI
mvn liberty:run
```

Check logs

```
docker logs id-of-the-container
```

## Step 5
Now you can curl the page for the results:

```
curl http://localhost:9090/eventmanager.jsf
curl http://localhost:5050/events

```

## Cleanup
To cleanup list running containers and stop them:

```
docker stop id-of-the-container
docker rm id-of-the-container
```

You might want to delete obsolete images:

```
docker image list
docker image rm id-of-the-image
```

Please follow me or DM on twitter: `@blumareks`

