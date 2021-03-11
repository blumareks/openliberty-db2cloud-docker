# openliberty-db2cloud-docker
This is an example client Java library to connect to DB2 on IBM Cloud

## Step 1 
Clone this repository or download the zip file:

```
git clone https://github.com/blumareks/openliberty-db2cloud-docker.git
cd openliberty-db2cloud-docker
```

## Step 2
In order to proceed you need to use `maven` to build the `war` file with the demo application. 

```
mvn package
```

## Step 2a
You might need to obtain the `db2jcc4.jar` and `db2jcc_license_cu.jar` files - you can download them from here, and place them in the `target` directory

Use the following location to get JDBC 4.0 Driver (db2jcc4.jar) : https://www.ibm.com/support/pages/node/382667 


## Step 3
When the `war` file is available, now you can use `docker` to build your image `(mind the trailing period: ".")`: 

```
docker image build -t <your docker id>/open-liberty-db2:0.6 .
```

## Step 4
You can run the docker container, and access the properties page. Collect the data from DB2 cloud service or DB2 server - alternatively create DB2 Cloud based service here (`free!`): https://cloud.ibm.com/catalog/services/db2 
After creating the data you need to create/copy the service credentials. The credentials would look like this:

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

Then you are ready to fillout and run your docker container:

```
docker run  -d -p80:9080 \
-e DB2_DBNAME="BLUDB" \
-e DB2_PASSWORD="your-password" \
-e DB2_PORT=50000 \
-e DB2_HOST="some-name-xyz.bluemix.net" \
-e DB2_USER="your-user"  \
<your-docker-id>/open-liberty-db2:0.6
```

Just for testing if everything works you might want to use blumareks' docker image:

```
docker run  -d -p80:9080 \
-e DB2_DBNAME="BLUDB" \
-e DB2_PASSWORD="your-password" \
-e DB2_PORT=50000 \
-e DB2_HOST="some-name-xyz.bluemix.net" \
-e DB2_USER="your-user"  \
blumareks/open-liberty-db2:0.6
```

Check logs

```
docker logs id-of-the-container
```

## Step 5
Now you can curl the page for the results:

```
curl http://localhost/system/properties-new

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

