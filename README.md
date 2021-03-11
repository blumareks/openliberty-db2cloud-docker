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

## Step 2a
The jcc library for the DB2 connectivity is obtained via maven, and copied inside Dockerfile commands.


## Step 3
When the `war` file is available, now you can use `docker` to build your image `(mind the trailing period: ".")`: 

```
docker image build -t <your docker id>/open-liberty-db2:0.8 .
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
docker run  -d -p50500:5050 -p5051:5051 \
-e DB2_DBNAME="BLUDB" \
-e DB2_PASSWORD="your-password" \
-e DB2_PORT=50000 \
-e DB2_HOST="some-name-xyz.bluemix.net" \
-e DB2_USER="your-user"  \
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

