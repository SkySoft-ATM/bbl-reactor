To run this example, you will need to run a Kafka instance locally.

The simplest way to do that is with docker:

```
docker run -p 2181:2181 -p 9092:9092 --env ADVERTISED_LISTENERS=PLAINTEXT://127.0.0.1:9092 teivah/kafka:2.0.0
```

