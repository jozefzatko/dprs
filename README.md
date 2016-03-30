## DPRS projekt
**Implementácia distribuovanej key-value databázy inšpirovanú Amazon Dynamo**

#### Autori:
- Jozef Zaťko
- Daniel Valocký
- Richard Pastorek

**Zadanie:** https://github.com/jozefzatko/dprs/blob/master/zadanie.md

#### Súčasti
* **Databázový uzol:** https://hub.docker.com/r/jozefzatko/dbnode/ https://github.com/jozefzatko/dprs/tree/master/DistributedDatabaseNode
* **NginX + Proxy:** https://hub.docker.com/r/dvalocky/nginx-consul_template https://github.com/jozefzatko/dprs/tree/master/nginx-consul_template

#### Použité technológie
* **Docker:** https://www.docker.com/
* **Docker Swarm:** https://docs.docker.com/swarm/networking/
* **Nginx:** http://nginx.org/
* **Registrator:** https://hub.docker.com/r/kidibox/registrator/
* **Consul:** https://www.consul.io/
* **Consul Template:** https://github.com/hashicorp/consul-template/
* **Spark:** http://sparkjava.com/
* **Logstash + ElasticSearch + Kibana:** https://www.elastic.co/