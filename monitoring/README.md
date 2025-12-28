Monitoring Stack (ELK)
======================

This folder provides a lightweight Elastic stack for local monitoring and log analysis: Elasticsearch + Kibana + Logstash.

What is included
----------------
- Elasticsearch 8.19.0 (single node, no auth/TLS, dev only)
- Kibana 8.19.0 (points to the local Elasticsearch)
- Logstash 8.19.0 with one pipeline listening on TCP 5000 (JSON lines)
- Persistent Elasticsearch data volume (`es_data`)

Prerequisites
-------------
- Docker Desktop (or compatible Docker engine) running
- PowerShell/Terminal access

How to run
----------
1) From the `monitoring` folder, start the stack:
```bash
cd monitoring
docker-compose up -d
```
2) Check containers:
```bash
docker-compose ps
```
3) Access the UIs:
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601

Sending logs to Logstash
------------------------
- Ingest port: TCP 5000
- Expected format: JSON lines with fields like `message`, `level`, `logger_name`, etc.
- Example (PowerShell):
```powershell
echo '{"message":"auth failed","level":"ERROR","logger_name":"com.project.supplychain.Auth"}' | ncat localhost 5000
```

What the pipeline does
----------------------
- Drops logs whose `logger_name` does not start with `com.project.supplychain`
- Masks secrets in `message` (passwords, bearer tokens, generic tokens)
- Tags security/login issues and domain errors based on message/level patterns
- Outputs to Elasticsearch index: `logback-YYYY.MM.dd`
- Also echoes events to stdout for debugging

Stopping and cleanup
--------------------
- Stop stack: `docker-compose down`
- Stop and remove data volume (fresh cluster next run): `docker-compose down -v`

Operational notes
-----------------
- Current setup disables xpack security/SSL for simplicity; enable for any shared or production-like environment.
- JVM heap is set to 512m for Elasticsearch; increase for larger datasets.
- Logstash monitoring API exposed on port 9600 (local use only).
