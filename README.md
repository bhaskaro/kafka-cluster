# Kafka Cluster (KRaft Mode) - Docker Compose

This repository runs a **4-node Apache Kafka cluster** in **KRaft mode** (no ZooKeeper) using Docker Compose.
It also includes Kafka UI, Prometheus, and Grafana for local testing and observability.

## Features

- Kafka **KRaft mode** using `confluentinc/cp-kafka:7.8.3`
- **4 brokers**, each configured as `broker,controller`
- Shared `CLUSTER_ID` across all brokers
- Kafka UI for topic and cluster inspection
- Prometheus + Grafana monitoring
- JMX exporter enabled per broker

## Services and Ports

| Service | Purpose | Host Port |
|---|---|---|
| kafka1 | Broker + Controller + JMX | `9092`, `7071` |
| kafka2 | Broker + Controller + JMX | `9093`, `7072` |
| kafka3 | Broker + Controller + JMX | `9094`, `7073` |
| kafka4 | Broker + Controller + JMX | `9095`, `7074` |
| kafka-ui | Web UI | `8080` |
| prometheus | Metrics scrape/query | `9090` |
| grafana | Dashboards | `3000` |

## Prerequisites

- Docker
- Docker Compose v2

## CLUSTER_ID (KRaft)

KRaft requires a cluster UUID. Generate it once and use the same value for all brokers in `docker-compose.yml`:

```bash
docker run --rm confluentinc/cp-kafka:7.8.3 kafka-storage random-uuid
```

Set that value in each broker's `CLUSTER_ID` environment variable.

## Start Cluster

```bash
docker compose up -d
```

For a clean restart (removes volumes and broker metadata):

```bash
docker compose down -v
docker compose up -d
```

## Verify Cluster

List topics from broker container:

```bash
docker exec -it kafka1 kafka-topics --bootstrap-server kafka1:9092 --list
```

From host machine, use any mapped broker port, for example:

```bash
kafka-topics --bootstrap-server localhost:9092 --list
```

## Kafka UI

Open:

- `http://localhost:8080`

Configured bootstrap servers:

- `kafka1:9092,kafka2:9092,kafka3:9092,kafka4:9092`

## Monitoring

- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (admin password: `admin`)

JMX metrics endpoints per broker:

- `http://localhost:7071/metrics`
- `http://localhost:7072/metrics`
- `http://localhost:7073/metrics`
- `http://localhost:7074/metrics`

## Notes

- This setup is KRaft-only; ZooKeeper is not used.
- If you change KRaft storage-related settings, do a clean restart with `docker compose down -v`.
- `KAFKA_ADVERTISED_LISTENERS` in compose should use your reachable host IP (or DNS name) for external clients.
