SONHMS 

### Build Instructions

This project is organized as a mvn project and has "org.onap.dcaegen2" as parent project. The build generate a jar and package into docker container. 

```
git clone https://gerrit.onap.org/r/dcaegen2/services/son-handler
mvn clean install
```


### Environment variables in Docker Container


Variables coming from deployment system:

- APP_NAME - son-handler application name that will be registered with consul
- CONSUL_PROTOCOL - Consul protocol by default set to **http**, if it is need to change it then that can be set to different value 
- CONSUL_HOST - used with conjunction with CBSPOLLTIMER, should be a host address (without port! e.g my-ip-or-host) where Consul service lies
- CBS_PROTOCOL - Config Binding Service protocol by default set to **http**, if it is need to change it then that can be set to different value
- CONFIG_BINDING_SERVICE - used with conjunction with CBSPOLLTIMER, should be a name of CBS as it is registered in Consul
- HOSTNAME - used with conjunction with CBSPOLLTIMER, should be a name of sonhms application as it is registered in CBS catalog
### Release images
For R4 - image/version  pushed to nexus3 
```
nexus3.onap.org:10001/snapshots/onap/org.onap.dcaegen2.services.son-handler   1.0.0
```

### Deployment
son handler can be manually deployed in dcae environment using cloudify blueprint.

login to bootstrap container in dcae deployment

Copy the blueprints to the bootstrap container.

The blueprint can be found under dpo/blueprints in the son-handler project.

To install : 
	cfy install -b sonhms -d sonhms -i <inputs filepath> <blueprint filepath> 

To uninstall:
	cfy uninstall sonhms
	cfy blueprints delete sonhms


