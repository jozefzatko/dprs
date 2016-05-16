#!/bin/bash

JSON=`curl -XGET $CONSUL_URL/v1/catalog/service/elk-9200 |  jq '.[0].ServiceAddress'`

RES="${JSON//\"/}"
echo $RES
ADR="http://$RES:9200/_template/filebeat?pretty"
echo $ADR
curl -XPUT $ADR -d@/usr/bin/filebeat.template.json

/workdir/target/dbnode-jar-with-dependencies.jar &
/usr/bin/filebeat -c /usr/bin/filebeat.yml -v -e 