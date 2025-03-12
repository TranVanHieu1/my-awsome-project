import KafkaConfig from '../config/KafkaConfig';
import { PayosService } from "../services/PayosService";

const kafkaConfig = new KafkaConfig();
const payosService = new PayosService();

async function processPaymentMessage(message: string): Promise<void> {
  try {
    console.log("Topic: create-payment");
    const parsedMessage = JSON.parse(message);
    console.log("Consumer received id:", parsedMessage.id);
    const totalPrice = parseFloat(parsedMessage.totalPrice);
    await payosService.createPaymentLink(parsedMessage, totalPrice);
  } catch (error: any) {
    console.error("Error processing create-payment message:", error.message);
  }
}

async function cancelPaymentMessage(message: string): Promise<void> {
  try {
    console.log("Topic: cancel-payment");
    const parsedMessage = JSON.parse(message);
    console.log("Consumer received id:", parsedMessage.id);
    await payosService.cancelPayment(parsedMessage.id, parsedMessage.cancellationReason);
  } catch (error: any) {
    console.error("Error processing cancel-payment message:", error.message);
  }
}

async function successPaymentMessage(message: string): Promise<void> {
  try {
    console.log("Topic: success-payment");
    const parsedMessage = JSON.parse(message);
    console.log("Consumer received id:", parsedMessage.id);
    await payosService.successPaymentMessage(parsedMessage.id);
  } catch (error: any) {
    console.error("Error processing success-payment message:", error.message);
  }
}

async function cashoutPaymentMessage(message: string): Promise<void> {
  try {
    console.log("Topic: cashout-message-topic");
    const parsedMessage = JSON.parse(message);
    console.log(message)
    await payosService.createPaymentLinkByInstructor(parsedMessage);
  } catch (error: any) {
    console.error("Error processing cashout-message-topic message:", error.message);
  }
}

export const startConsumer = async () => {
  await kafkaConfig.connectConsumer("create-payment");
  await kafkaConfig.connectConsumer("cancel-payment");
  await kafkaConfig.connectConsumer("success-payment");
  await kafkaConfig.connectConsumer("cashout-message-topic");

  await kafkaConfig.consume("create-payment", "create-payment", processPaymentMessage);
  await kafkaConfig.consume("cancel-payment", "cancel-payment", cancelPaymentMessage);
  await kafkaConfig.consume("success-payment", "success-payment", successPaymentMessage);
  await kafkaConfig.consume("cashout-message-topic", "cashout-message-topic", cashoutPaymentMessage);
};
