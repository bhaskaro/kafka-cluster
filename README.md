# Kafka Cluster (KRaft Mode) – Docker Compose

This repository contains a **3-node Apache Kafka cluster** running in **KRaft mode (no Zookeeper)** using Docker
Compose, along with example topics intended for local development and testing.

The setup follows **production-aligned Kafka practices** while remaining simple enough for local experimentation.

---

## ✨ Features

- Apache Kafka **KRaft mode** (no Zookeeper)
- **3 brokers**, each acting as **broker + controller**
- Proper **controller quorum**
- Explicit **listener configuration** (internal + external)
- **Manual topic management** (auto-create disabled)
- Compatible with **Spring Boot / Java Kafka clients**
- Designed to avoid common Kafka Docker pitfalls

---

## 🧱 Architecture Overview

| Component | Description                                |
|-----------|--------------------------------------------|
| kafka-1   | Broker + Controller (external access)      |
| kafka-2   | Broker + Controller (internal only)        |
| kafka-3   | Broker + Controller (internal only)        |
| KRaft     | Metadata quorum using Raft                 |
| Topics    | orders, payments, shipments, notifications |

**Listeners**

- `PLAINTEXT` → inter-broker + admin traffic
- `CONTROLLER` → KRaft quorum communication
- `EXTERNAL` → host-based clients (Spring Boot, CLI)

---

## 📦 Prerequisites

- Docker 24+
- Docker Compose v2
- Linux / macOS (tested on Ubuntu)
- Java 17+ (for client applications)

---

## 🚀 Getting Started

### 1️⃣ Clone the repository

```bash
git clone git@github.com:bhaskaro/kafka-cluster.git
cd kafka-cluster
````

---

### 2️⃣ Start the Kafka cluster

> ⚠️ This setup uses KRaft. A **clean start is required** if configs change.

```bash
docker compose down -v
docker compose up -d
```

Wait ~20–30 seconds for brokers and controller quorum to form.

---

### 3️⃣ Verify cluster health

```bash
docker exec -it kafka-1 \
  kafka-topics --bootstrap-server kafka-1:19092 --list
```

Expected output:

```
__consumer_offsets
orders
payments
shipments
notifications
```

If this command works, the cluster is healthy.

---

## 🗂️ Topic Management

Auto topic creation is **disabled by design**.

### Create topics manually

```bash
docker exec -it kafka-1 kafka-topics \
  --bootstrap-server kafka-1:19092 \
  --create --topic orders \
  --partitions 6 --replication-factor 3
```

Repeat for:

* `payments`
* `shipments`
* `notifications`

---

### Recreate topics (after volume wipe)

If you ran:

```bash
docker compose down -v
```

You **must recreate topics**, since metadata is deleted.

---

## 🧪 Quick Test (CLI)

### Produce a message

```bash
docker exec -it kafka-1 \
  kafka-console-producer \
  --bootstrap-server kafka-1:19092 \
  --topic orders
```

Type:

```
hello-kafka
```

### Consume the message

```bash
docker exec -it kafka-1 \
  kafka-console-consumer \
  --bootstrap-server kafka-1:19092 \
  --topic orders \
  --from-beginning
```

---

## 🧑‍💻 Client Configuration (Spring Boot example)

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      acks: all
      retries: 5
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    consumer:
      group-id: demo-consumer-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    listener:
      concurrency: 4
```

---

## ⚠️ Important Notes

* **KRaft mode requires strict listener configuration**
* Deleting Docker volumes **removes all topics**
* Only **one external port (9092)** is exposed
* All brokers advertise a reachable listener for leaders
* This setup is intended for **local development and learning**

---

## 🛠️ Common Commands

| Action             | Command                            |
|--------------------|------------------------------------|
| List topics        | `kafka-topics --list`              |
| Describe topics    | `kafka-topics --describe`          |
| Delete topic       | `kafka-topics --delete`            |
| Check consumer lag | `kafka-consumer-groups --describe` |
| View broker logs   | `docker logs kafka-1`              |

---

# 📊 Observability (Prometheus & Grafana)

This setup includes **Kafka monitoring using Prometheus and Grafana**, exposing broker metrics via JMX and visualizing them through dashboards.

---

## 🔍 Access URLs

> Replace `<HOST_IP>` with your machine IP if accessing remotely

| Component         | URL                           |
| ----------------- | ----------------------------- |
| Kafka UI          | http://<HOST_IP>:8080/ui/     |
| Prometheus        | http://<HOST_IP>:9090/query   |
| Grafana Dashboard | http://<HOST_IP>:3000         |
| Kafka Metrics     | http://<HOST_IP>:7071/metrics |

---

## 🧱 Monitoring Architecture

```text
Kafka Brokers → JMX Metrics → JMX Exporter → Prometheus → Grafana
```

* Kafka exposes metrics via **JMX**
* JMX Exporter converts them to **Prometheus format**
* Prometheus scrapes metrics
* Grafana visualizes dashboards

---

## 📈 Available Metrics

### Broker Metrics

* Messages in/sec
* Bytes in/sec
* Bytes out/sec
* Request rate

### Cluster Metrics

* Leader count
* Partition count
* Under-replicated partitions

### Topic Metrics

* Throughput per topic
* Traffic distribution

---

## ⚠️ Important Notes

* Metrics are exposed via **JMX Exporter (port 7071)**
* Prometheus scrapes Kafka brokers at configured intervals
* Grafana dashboards depend on correct **JMX mapping rules**
* Some dashboards require **additional exporters (e.g., consumer lag)**

---

## 📊 Grafana Setup

### Default Login

```text
Username: admin
Password: admin
```

---

### Add Prometheus Data Source

1. Go to **Settings → Data Sources**
2. Select **Prometheus**
3. Set URL:

```text
http://prometheus:9090
```

---

### Import Kafka Dashboard

Use a Kafka dashboard (example):

```text
Dashboard ID: 721
```

---

## 💾 Persisting Grafana Dashboards

By default, Grafana data is lost on container restart.

### ✅ Enable persistence (recommended)

Add volume to `docker-compose.yml`:

```yaml
grafana:
  image: grafana/grafana
  ports:
    - "3000:3000"
  volumes:
    - grafana-data:/var/lib/grafana
  environment:
    - GF_SECURITY_ADMIN_PASSWORD=admin

volumes:
  grafana-data:
```

---

### 🟢 Alternative (host-mounted volume)

```yaml
volumes:
  - ./grafana-data:/var/lib/grafana
```

👉 This allows:

* easy backup
* local inspection

---

## ⚠️ Common Issues

| Issue                | Cause                      | Fix                              |
| -------------------- | -------------------------- | -------------------------------- |
| No data in Grafana   | Missing JMX mappings       | Update `kafka-jmx.yml`           |
| Metrics missing      | Exporter config incomplete | Add kafka.server / cluster rules |
| Consumer lag empty   | No exporter                | Add Kafka Exporter               |
| Dashboards disappear | No volume                  | Add persistent storage           |

---

## 🔧 Debugging Steps

1. Check metrics endpoint:

```text
http://<HOST_IP>:7071/metrics
```

2. Verify Prometheus:

```promql
up
```

3. Search metrics:

```promql
{kafka_server_brokertopicmetrics_meanrate}
```

---

## 🚀 Production Insight

A complete Kafka observability stack typically includes:

```text
JMX Exporter + Prometheus + Grafana + Kafka Exporter
```

* JMX Exporter → broker metrics
* Kafka Exporter → consumer lag + topic metrics
* Prometheus → storage
* Grafana → visualization

---

## 🎯 Key Learning

* Kafka metrics require **proper JMX mapping**
* Grafana dashboards depend on **metric naming consistency**
* Observability is critical for:

    * throughput monitoring
    * lag detection
    * system health

---

## 📌 Troubleshooting

* If brokers exit immediately → check listener names
* If topics disappear → volumes were removed
* If clients cannot connect → verify `EXTERNAL` listener
* Always restart with `docker compose down -v` after listener changes

---

## 📚 References

* Apache Kafka Documentation
* Kafka KRaft Mode (ZooKeeper-less Kafka)
* Confluent cp-kafka Docker Image

---

## 👤 Author

**Vijaya Bhaskar Oggu**
Enterprise Architect | Cloud & Distributed Systems
Kafka • Java • Spring Boot • OCI • Microservices

---

## 📝 License

This project is provided for educational and development purposes.

---
