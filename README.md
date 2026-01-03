# Kafka Cluster (KRaft Mode) – Docker Compose

This repository contains a **3-node Apache Kafka cluster** running in **KRaft mode (no Zookeeper)** using Docker Compose, along with example topics intended for local development and testing.

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

| Component | Description |
|---------|------------|
| kafka-1 | Broker + Controller (external access) |
| kafka-2 | Broker + Controller (internal only) |
| kafka-3 | Broker + Controller (internal only) |
| KRaft | Metadata quorum using Raft |
| Topics | orders, payments, shipments, notifications |

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
| ------------------ | ---------------------------------- |
| List topics        | `kafka-topics --list`              |
| Describe topics    | `kafka-topics --describe`          |
| Delete topic       | `kafka-topics --delete`            |
| Check consumer lag | `kafka-consumer-groups --describe` |
| View broker logs   | `docker logs kafka-1`              |

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

````

---

## ✅ Ready to Commit

```bash
git add README.md
git commit -m "Add Kafka KRaft cluster README"
git push
````

---
