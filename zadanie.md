**Implementujte key-value databázu inšpirovanú Amazon Dynamo**

### 1. čast - 10 bodov - 30.3.2016 8:00

***Cieľom je demonštrovať základnú funkčnosť infraštruktúry, tz. mať v dockery na viacerých VM rozbehané jednotlivé časti aplikácie, ktoré dokážu spolu komunikovať, spraviť základný bootstrap, nastaviť proxy, pozbierať logy a zorbaziť jednoduchý dashboard napr. s počítadlom requestov.***

### Povinné časti:
* docker
* service discovery
* proxy
* distribuované logovanie
* monitoring
* dashboard
* dynamo - jednoduchá aplikácia, ktorá dokáže spraviť bootstrap a poslať nejaky request (napr. ping) na ďalšiu inštanciu - tz. musí sa vedieť prihlásiť do service discovery, získať adresy peerov a byť schopná s nimi komunikovať. Samotná funkcionalita dynama zatiaľ nie je potrebná

### Jednotlivé časti projektu:

##### Docker 
- povinné
- všetky súčasti systému musia byť distribuované a nasadené vo forme docker kontainerov bežiacich na viacerých (min. 2) samostatných VM

##### Service discovery
- povinné
- rieši boostrap/registráciu, pridávanie a odobratie uzlov do/z konfigurácie; zdroj informacií pre konfiguráciu proxy. Môže používať heartbeat na monitorovanie výpadkov uzlov.
- môžete použiť existujúci nástroj alebo vlastnú implementáciu

##### Proxy
- povinné
- zabezpečuje load-balancing komunikácie medzi používateľom a uzlami Dynamo. Konfigurácia proxy je automatická na základe informácií zo service discovery.
- môžete použiť existujúci nástroj alebo vlastnú implementáciu

##### Distribuované logovanie
- centrálne miesto na zber a ukladanie logov zo všetkých subsystémov. Logy sú jednotlivými procesmi ukladané vždy lokálne a odtiaľ su transportované (push/pull) do subsystému zabezpečujúceho centrálne ukladanie logov pomocou pipeline ako napr. Logstash alebo Fluentd
- povinné na logy z Dynamo uzlov, nepovinne aj na zber logov z proxy či service discovery
- môžete použiť existujúci nástroj alebo vlastnú implementáciu

##### Monitoring
- zabezpečuje zbieranie metrík (tak business ako aj performance, cpu, ram, iops, ...). Metriky môžu byť získavné aj z logov (napr. logovanie zápisu key-value a následné počítanie výskytov tohto logu)
- povinné na úrovni zskavania business metrík z logov, performance a systémové metriky sú nepovinné
- môžete použiť existujúci nástroj alebo vlastnú implementáciu - logy budete mať zjavne vlastné ale ak chcete robiť monitorovanie performance a systémových metrík ako CPU tak skôr použijete existujúci nástroj ako napr. Zabbix alebo PCP

##### Dashboard
- povinné
- vizualizácia logov a agregovaných monitorovaných údajov, nejaké pekné grafy, počítadlá, etc.
- môžete použiť existujúci nástroj alebo vlastnú implementáciu

##### Scheduling & container orchestration:
- nepovinné
- automatizuje umiesťnovanie, spúštanie a vypínanie jednotlivých inštancií aplikácií/kontainerov v clustri
- môžete použiť existjúci nástroj (výrazne sa odporúča) alebo vlastnú implementáciu

##### Dynamo:
- povinné
- proces poskytujúci samotné databázové API pre koncového používateľa a ukladajúci dáta
- vlastná implementácia

### Dynamo:

##### Chord & consistent hashing
- povinné
- p2p topológia využívajúca DHT na komunikáciu a adresovanie uzlov v sieti. Každému uzlu je pridelená časť z adresného priestoru hashov za ktoré je zodpovedný. Vzhľadom na počet uzlov nemusíte používať DHT ale každý uzol môže poznať adredy ostatných uzlov. Samotné informácie o adresách uzlov budete načítavať zo service discovery
        
##### Gossip
- nepovinné
- slúži na šírenie informácie o topológii siete a membershipe uzlov. Je možné ho použiť na konsenzus pri prehlásení uzla za mŕtvy (v opačnom prípade o tom rozhodne service discovery)

##### Vector clock:
- povinné
- verzionovanie dát zachovávajúce kauzálne usporiadanie

##### Merkle trees:
- nepovinné
- využíva sa na efektívne zistenie rozdielov stavu dvoch replík bez nutnosti preposielania a porovnávania kompletne všetkých dát

##### Hinted handoff:
- nepovinné
- umožňuje vysporiadať sa s dočasnými výpadkami uzlov tak, že takýto uzol nie je hneď prehlásený za mŕtvy (a všetky jeho dáta nie je teda potrebné hňeď replikovať) ale je dočasne nahradený ďalším uzlom, ktorý do jeho návratu prevezme zápisy za daný rozsah hashov

##### Fault tolerance:
- povinné
- schopnosť vysporiadať sa s výpadkami (kill) Dynamo uzlov

##### Bootstraping:
- povinné
- uzol (tz. docker kontainer pre Dynamo inštanciu) by sa mal do siete automaticky prihlásiť iba na základe znalosti adresy service discovery. Tz. uzol pri štartovaní dostane ako parameter napr. IP:PORT service discovery, stiahne si informácie o topológii siete, skontaktuje sa s potrebými uzlami, iniciuje replikáciu, etc.

##### Replication:
- povinné
- replikácia dát na potrebný počet uzlov zodpovedných za daný hash

##### Sloppy quorum:
- povinné
- možnosť špecifikovať pri akom počte odpovedí od replík je request považovaný za úspešne spracovaný (pre write aj pre read)

##### REST API:
- povinné
- API pre rozhranie používateľ <-> Dynamo
