# FLARE MISP Service - scheduler for CTI-toolkit


This service is provided to enable the specific use case of retrieving AIS data (in STIX 1.1.1 format) from AIS and loading the content in a MISP Server.  For more information about DHS and establishing access to the AIS service, please see https://us-cert.gov/ais.

The Flare MISP Service has been designed to provide a mechanism to routinely poll content from a TAXII server and make it available in a MISP server.  This service relies heavily upon the CTI Toolkit to do to the transformation of STIX content to MISP and to provide the TAXII server functionality.  It can be configured to run CTI-toolkit commands to pull from a TAXII server and convert the STIX files into MISP format or just download the STIX as xml at regular intervals. 



## Table of Contents
1. [About FLARE MISP Service](#about)
2. [Starting FLARE MISP Service](#starting)
3. [Endpoints](#endpoints)
4. [Building Tar Ball](#packaging)
5. [Installation](#installation)
   
# <a name="about"></a>About FLARE MISP Service

FlareMispService is basically a REST Web Service wrapper around the functionality of CTI-Toolkit

#### Web Service’s responsibilities:
* Run a Scheduler to wake up & use CTIToolkit to do work.
* Read the configuration file and pass values as args to CTIToolkit.
* Provide a basic REST API for interacting with CTIToolkit.
    * Reload configurable properties from file system
    * Check TAXI connectivity
    * Check MISP connectivity
    * Manually start a Poll for work to do (outputs XML only)
    * Manually start a Poll for work to do (outputs MISP Event to MISP Server)
    * Handle request from Scheduler to do work (MISP).
    * Restart Scheduler (assuming it crashed or failed auto startup)
    * Display list of Quartz Jobs (only 1 recurring job unless you program more)
    * Stop all Quartz Jobs 

# <a name="starting"></a>Starting FLARE MISP Service
#### To start the FlareMispService:
Run the shell script:

    /opt/mtc/runFLAREmispService.sh

Output should display on the console. 
Note that the version in the script may need to be updated.

#### Independently verify the service is running with the following command:
   	ps -ef | grep mtc-rest-service

#### To verify the service can connect to the FlareSuite’s TAXII Server:
    a.	http://localhost:8080/checkTaxiiStatus
    b.	Should seem something similar to: {"resourceType":"FLARE/TAXII","resource":"https://10.23.218.172:8443","statusCode":200}
    c.	The statusCode of value 200 indicates a successful connection to the TAXII server
    d.	If the value is not 200, verify the ip address and port in step 6e and 6f above.
    e.	Otherwise must verify that the FlareSuite TAXII server is running and accessible to the server where FlareMispService is running.

#### To verify the service can connect to the MISP Server:
    a.	http://localhost:8080/checkMispStatus
    b.	Should seem something similar to: {"resourceType":"Misp","resource":"http://10.23.218.173","statusCode":200}
    c.	The statusCode of value 200 indicates a successful connection to the TAXII server
    d.	If the value is not 200, verify the ip address in step 6g above. (no port specified because MISP uses a predefined port #)
    e.	Otherwise must verify that the MISP server is running and accessible to the server where FlareMispService is running.


#### Quartz output messages:
When it’s time for the Quartz job to poll the FlareSuite TAXI server for STIXX information, you can expect to see the following information on the command line and/or in /opt/mtc/logs/mtc-rest-service.log
(the dates and times will change accordingly)

    2018-02-28 09:36:46 - Processing events...
    2018-02-28 09:36:46 - TAXII health check passed.
    2018-02-28 09:36:47 - Misp health check passed.
    2018-02-28 09:36:48 - Processing events: Success
    2018-02-28 09:36:48 - {"id":14,"status":"Success","detailedStatus":"Completed sucessfully","processType":"xmlOutput","collection":"MISP","beginTimestamp":"2018-02-28T09:35:47+00:00","endTimeStamp":"2018-02-28T09:36:47+00:00"}

# <a name="endpoints"></a> Endpoints
### Verify FLARE MISP Service is running with the following endpoints:

####  Verify the Quartz Scheduler is Up and Running
URL:  http://localhost:8080/listQuartzJobs
﻿Quartz Jobs:[jobName] : initializeQuartzJob [groupName] : group1 - Thu Mar 22 12:30:25 EDT 2018<r>


####  Verify connectivity to MISP Server
URL: http://localhost:8080/checkMispStatus
﻿{"resourceType":"Misp","resource":"(MISP URL here)","statusCode":200}


####  Verify connectivity to FLARE TAXII Server
URL: http://localhost:8080/checkTaxiiStatus
﻿{"resourceType":"FLARE/TAXII","resource":"(MISP URL here):(MISP Port here)","statusCode":200}


####  Verify the configuration property file can be loaded/reloaded
This does not refresh the timestamps
URL: http://localhost:8080/refreshConfig
<blank page result is fine>  

### Verify Connectivity with the following endpoints:

####  Connect to FLARE TAXI, download any new STIX, then save as XML to temporary directory  “/opt/mtc/out”
URL: http://localhost:8080/misptransclient?processType=xmlOutput

Result:
﻿{"id":5,"status":"Success","detailedStatus":"Completed sucessfully","processType":"xmlOutput","collection":"MISP","beginTimestamp":"2018-03-22T11:10:56+00:00","endTimeStamp":"2018-03-22T12:33:05+00:00"}


####  Connect to FLARE TAXI, convert new STIX to MISP, then upload to MISP Server
URL: http://localhost:8080/misptransclient?processType=stixToMisp

Result:
﻿{"id":7,"status":"Success","detailedStatus":"Completed sucessfully","processType":"stixToMisp","collection":"MISP","beginTimestamp":"2018-03-22T12:33:23+00:00","endTimeStamp":"2018-03-22T12:34:18+00:00"}


#### Login to MISP Server to verify new MISP Events show up
URL: http://<MISP URL>/users/login
User:  admin@admin.test


# <a name="packaging"></a>To build the tarball:

1) Pull the most recent code 

   Set up Maven and Java 8 properly
   
2) go to the ~git/FLAREmispService/misp-trans-client-rest-service folder. 

   There should be a pom.xml file in this directory. Run command:
   
		mvn clean package
				
   in order to build the package
   
3) run the packageDeployment.sh script to pack the built mvn package into a tarball the tarball should be in ~git/FLAREmispService/misp-trans-client-rest-service/deploy/ named FLAREmispService.tar


# <a name="installation"></a>Installation:

Refer to the SAG for more details

#### 1) Create the directory /opt/mtc and set its permissions:

```sudo mkdir /opt/mtc```

```sudo chmod 755 /opt/mtc```

#### 2) Copy the FLAREmispService.tar to /opt/mtc:

```sudo cp FLAREmispService.tar /opt/mtc```

#### 3)	Untar the tarball.

```tar –xvf FLAREmispService.tar```

It _should_ contain the directories:

    /opt/mtc
    /opt/mtc/out
    /opt/mtc/logs
    /opt/mtc/config

The tarball should contain 2 properties files in /opt/confg:

    application.properties
    config.properties

The /logs and /out directories should be empty (initially).


#### 4)	Verify the ownership of the subdirectories under /opt/mtc are owned by ‘flare’  (or ‘flaredev’)
	If the directories are not owned by the proper account, change them.
        a)	cd /opt/mtc
        b)	sudo chown flare:flare /opt/mtc/*

#### 5)	Copy certificate and key for the TAXII Server (FlareSuite) to /opt/mtc/config

    a. The names and locations of the key and cert files are configurable with properties in  /opt/mtc/config/config.properties
    b. They default to the location /opt/mtc/config
    c. The key and cert files are specific to the FlareSuite TAXII Server

#### 6) Encrypt the private key password:

The encryption library (Encryption.jar) is located in the config folder.
To generate an encrypted password run the following command in terminal:

```java -cp Encryption.jar xor.bcmcgroup.Main <password> <encryptionKey>```

Then in the /opt/mtc/config/config.properties file set the values for:

    encKey=(Encryption key used)
    stixtransclient.misp.key=(Encrypted key value to connect to the MISP server)


#### 7)	Adjust remaining /opt/mtc/config/config.properties file values for the Deployed Environment
    Modify property: bin.filepath to stixtransclient.py location
    a.	Modify property: stixtransclient.client.key to be equal to path and name of the key file from step #5
    b. 	Modify property: stixtransclient.client.cert to be equal to path and name of the certificate file from step #5
    c.	Modify property: stixtransclient.poll.baseurl to have the IP and port for the Flaresuite TAXII server in deployed environment
    d.	Modify property: stixtransclient.poll.url 
        i.	Start with the same value as stixtransclient.poll.baseurl property in step (6c)
        ii.	append the following: /flare/taxii11/poll/
    e.	Modify property: stixtransclient.misp.url to have the IP for the MISP Server.
	
#### 8)	Tweak the /opt/mtc/config/application.properties file to appropriate values for the Deployed Environment.
    a.	Verify the logging property: logging.level.org.gd.ddcs.flare.misp = ERROR   (can be WARN, INFO, or DEBUG for additional information)
    b.	Verify the logging property: logging.level.org.springframework.web = ERROR  (can be WARN, INFO, or DEBUG for additional information)
   
#### 9) Start the service
See [Starting FLARE MISP Service](#starting)
