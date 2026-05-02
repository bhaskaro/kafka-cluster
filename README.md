# Kafka Cluster (KRaft Mode) - Docker Compose

This repository provides a local **4-node Kafka KRaft cluster** (no ZooKeeper) with:

- Kafka brokers/controllers (`cp-kafka:7.8.3`)
- Kafka UI
- Prometheus
- Grafana

It is designed for local development, testing, and observability.

## What Runs

| Service | Image | Host Ports |
|---|---|---|
| `kafka1` | `confluentinc/cp-kafka:7.8.3` | `9092`, `7071` |
| `kafka2` | `confluentinc/cp-kafka:7.8.3` | `9093`, `7072` |
| `kafka3` | `confluentinc/cp-kafka:7.8.3` | `9094`, `7073` |
| `kafka4` | `confluentinc/cp-kafka:7.8.3` | `9095`, `7074` |
| `kafka-ui` | `provectuslabs/kafka-ui:v0.7.2` | `8080` |
| `prometheus` | `prom/prometheus` | `9090` |
| `grafana` | `grafana/grafana` | `3000` |

Kafka roles:
- `kafka1`, `kafka2`, `kafka3`: `broker,controller`
- `kafka4`: `broker` only

## Prerequisites

- Docker
- Docker Compose v2
- Java JDK 21+ (JDK required, not JRE) for running the Spring Boot app

## One-Time Setup

### 1) Generate a KRaft cluster ID

Generate once, then use the same value for all broker `CLUSTER_ID` entries:

```bash
docker run --rm confluentinc/cp-kafka:7.8.3 kafka-storage random-uuid
```

### 2) Set advertised host IP

`docker-compose.yml` uses:

```yaml
KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka1:29092,EXTERNAL://${HOST_IP:-127.0.0.1}:9092
```

Set `HOST_IP` in your shell before startup if clients connect from outside this machine:

```bash
export HOST_IP=<your_reachable_host_ip>
```

For local-only usage on same machine, default `127.0.0.1` is fine for host clients.

Listener model used by this stack:
- `INTERNAL://kafkaX:29092` for broker-to-broker and in-network clients
- `EXTERNAL://${HOST_IP}:9092..9095` for host-machine clients
- `CONTROLLER://kafka1..3:9093` for KRaft controller quorum traffic

## Start the Stack

```bash
docker compose up -d
```

## Health Behavior

Each Kafka broker has a healthcheck:

```bash
KAFKA_OPTS='' kafka-broker-api-versions --bootstrap-server localhost:9092 > /dev/null 2>&1
```

`KAFKA_OPTS` is cleared in the probe to avoid JMX javaagent port conflicts.

Dependent services startup order:

- `kafka-ui` waits for all brokers to be healthy
- `prometheus` waits for all brokers to be healthy
- `grafana` starts after `prometheus`

Check status:

```bash
docker compose ps
```

## Verify Kafka

List topics from inside a broker container:

```bash
docker exec -it kafka1 kafka-topics --bootstrap-server kafka1:29092 --list
```

From host (if Kafka CLI is installed locally):

```bash
kafka-topics --bootstrap-server localhost:9092 --list
```

## Access UIs

- Kafka UI: `http://localhost:8080`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (default password: `admin`)

JMX metric endpoints:

- `http://localhost:7071/metrics`
- `http://localhost:7072/metrics`
- `http://localhost:7073/metrics`
- `http://localhost:7074/metrics`

## Spring Boot App Control

Use the utility script in this repo:

```bash
./appctl.sh start
./appctl.sh status
./appctl.sh logs
./appctl.sh stop
```

Default Java path in script:
- `/scratch/voggu/softwares/jdk-25.0.1`

Override if needed:

```bash
JAVA_HOME=/path/to/your/jdk ./appctl.sh start
```

## Persistence and Reset

Kafka data is persisted in named Docker volumes:

- `kafka1-data`
- `kafka2-data`
- `kafka3-data`
- `kafka4-data`

Normal stop/start keeps data:

```bash
docker compose down
docker compose up -d
```

Full reset deletes broker metadata/topics:

```bash
docker compose down -v
docker compose up -d
```

## Notes

- This is a KRaft-only setup; ZooKeeper is not used.
- Controller quorum voters are `kafka1..kafka3` (3-node quorum).
- `kafka4` is broker-only and not part of controller quorum.
- `version: '3.8'` in compose is currently harmless but deprecated by newer Compose CLIs.
- For shared environments, change the default Grafana admin password.
