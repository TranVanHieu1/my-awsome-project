import { Kafka, Producer, Consumer, EachMessagePayload } from "kafkajs";

class KafkaConfig {
  private kafka: Kafka;
  private producer: Producer;
  private consumers: { [key: string]: Consumer };

  constructor() {
    this.kafka = new Kafka({
      clientId: "nodejs-kafka",
      brokers: ["42.119.249.86:9092"],
    });
    this.producer = this.kafka.producer();
    this.consumers = {};
  }

  async connectProducer(): Promise<void> {
    try {
      await this.producer.connect();
      console.log("Kafka producer connected");
    } catch (error) {
      console.error("Failed to connect Kafka producer:", error);
    }
  }

  async disconnectProducer(): Promise<void> {
    try {
      await this.producer.disconnect();
      console.log("Kafka producer disconnected");
    } catch (error) {
      console.error("Failed to disconnect Kafka producer:", error);
    }
  }

  async connectConsumer(groupId: string): Promise<void> {
    try {
      if (!this.consumers[groupId]) {
        this.consumers[groupId] = this.kafka.consumer({ groupId });
        await this.consumers[groupId].connect();
        console.log(`Kafka consumer connected for group: ${groupId}`);
      }
    } catch (error) {
      console.error(`Failed to connect Kafka consumer for group ${groupId}:`, error);
    }
  }

  async produce(topic: string, messages: { value: string }[]): Promise<void> {
    try {
      await this.producer.send({
        topic: topic,
        messages: messages,
      });
    } catch (error) {
      console.error("Error producing message to Kafka:", error);
    }
  }

  async consume(topic: string, groupId: string, callback: (message: string) => void): Promise<void> {
    try {
      await this.connectConsumer(groupId);
      await this.consumers[groupId].subscribe({ topic, fromBeginning: true });
      await this.consumers[groupId].run({
        eachMessage: async ({ topic, partition, message }: EachMessagePayload) => {
          if (message.value) {
            const value = message.value.toString();
            callback(value);
          }
        },
      });
    } catch (error) {
      console.error(`Error consuming message from Kafka for group ${groupId}:`, error);
    }
  }
}

export default KafkaConfig;
