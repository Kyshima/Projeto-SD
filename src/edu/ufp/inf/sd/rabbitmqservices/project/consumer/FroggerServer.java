package edu.ufp.inf.sd.rabbitmqservices.project.consumer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FroggerServer {
    public static String host;
    public static int port;


    public static void main(String[] argv) throws Exception {
        Info.main();
        try {
            RabbitUtils.printArgs(argv);

            //Read args passed via shell command
            host=argv[0];
            port=Integer.parseInt(argv[1]);
            String queueName=argv[2];

            /* Open a connection and a channel, and declare the queue from which to consume.
            Declare the queue here, as well, because we might start the client before the publisher. */
            Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
            Channel channel=RabbitUtils.createChannel2Server(connection);

            /* Declare a queue as Durable (queue won't be lost even if RabbitMQ restarts);
            NB: RabbitMQ doesn't allow to redefine an existing queue with different
            parameters, need to create a new one */
            boolean durable = true;
            //channel.queueDeclare(Send.QUEUE_NAME, false, false, false, null);
            channel.queueDeclare(queueName, durable, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            /* The server pushes messages asynchronously, hence we provide a DefaultConsumer callback
            that will buffer the messages until ready to use them. */
            //Set QoS: accept only one unacked message at a time; and force dispatch to next worker that is not busy.
            int prefetchCount = 1;
            channel.basicQos(prefetchCount);

            DeliverCallback deliverCallback=(consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");
                try {
                    doWork(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(" [x] Done processing task");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            //boolean autoAck = true; //When true disables "Manual message acknowledgments"
            //Set flag=false for worker to send proper ack (once it is done with a task).
            boolean autoAck = false;
            //Register handler deliverCallback()
            channel.basicConsume(queueName, autoAck, deliverCallback, consumerTag -> { });

        } catch (Exception e) {
            //Logger.getLogger(Recv.class.getName()).log(Level.INFO, e.toString());
            e.printStackTrace();
        }

    }

    /** Fake a second of work for every dot in the message body */
    private static void doWork(String task) throws InterruptedException {
        System.out.println(task);
        String[] a = task.split("!", 0);
        switch (a[0]) {
            case "criar": {
                String exchangeName = "Exchange " + Integer.parseInt(a[1]);
                try (Connection connection = RabbitUtils.newConnection2Server(host, port, "guest", "guest");
                     Channel channel = RabbitUtils.createChannel2Server(connection)) {

                    System.out.println("[X] Declare exchange: '" + exchangeName + "' of type " + BuiltinExchangeType.FANOUT);
                    /* Set the Exchange type to MAIL_TO_ADDR FANOUT (multicast to all queues)*/
                    channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

                    //Gets the message
                    Info.games[Integer.parseInt(a[1])][0] = 1;
                    String message = "Jogo criado!0";
                    TimeUnit.SECONDS.sleep(2);


                    String routingKey = "";
                    channel.basicPublish(exchangeName, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println(" [x] Sent '" + message + "'");

                } catch (IOException | TimeoutException ignored) {}
                break;
            }
            case "juntar": {
                String exchangeName = "Exchange " + Integer.parseInt(a[1]);
                try (Connection connection = RabbitUtils.newConnection2Server(host, port, "guest", "guest");
                     Channel channel = RabbitUtils.createChannel2Server(connection)) {

                    // Declare a queue where to send msg (idempotent, i.e., it will only be created if it doesn't exist);
                    //channel.queueDeclare(queueName, false, false, false, null);
                    //channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                    System.out.println("[X] Declare exchange: '" + exchangeName + "' of type " + BuiltinExchangeType.FANOUT);
                    /* Set the Exchange type to MAIL_TO_ADDR FANOUT (multicast to all queues)*/
                    channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

                    //Gets the message
                    System.out.println(Integer.parseInt(a[1]));
                    String message = "";
                    for (int i = 0; i < 4; i++) {
                        System.out.println(Info.games[Integer.parseInt(a[1])][i]);
                        if (Info.games[Integer.parseInt(a[1])][i] == 0) {
                            Info.games[Integer.parseInt(a[1])][i] = 1;
                            message = "Game joined!" + Info.games[Integer.parseInt(a[1])][i];
                            break;
                        } else if (i == 3) {
                            message = "Erro";
                        }

                    }
                    //String message = "Game joined!"+p;
                    TimeUnit.SECONDS.sleep(2);


                    String routingKey = "";
                    channel.basicPublish(exchangeName, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println(" [x] Sent '" + message + "'");

                } catch (IOException | TimeoutException ignored) {

                }
                break;
            }
            case "move": {
                String exchangeName = a[3];
                try (Connection connection = RabbitUtils.newConnection2Server(host, port, "guest", "guest");
                     Channel channel = RabbitUtils.createChannel2Server(connection)) {

                    // Declare a queue where to send msg (idempotent, i.e., it will only be created if it doesn't exist);
                    //channel.queueDeclare(queueName, false, false, false, null);
                    //channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                    System.out.println("[X] Declare exchange: '" + exchangeName + "' of type " + BuiltinExchangeType.FANOUT);
                    /* Set the Exchange type to MAIL_TO_ADDR FANOUT (multicast to all queues)*/
                    //channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

                    //Gets the message
                    String message = "";
                    switch (Integer.parseInt(a[2])) {
                        case 0:
                            message = "move!" + Integer.parseInt(a[1]) + "!" + 0;
                            break;
                        case 1:
                            message = "move!" + Integer.parseInt(a[1]) + "!" + 1;
                            break;
                        case 2:
                            message = "move!" + Integer.parseInt(a[1]) + "!" + 2;
                            break;
                        case 3:
                            message = "move!" + Integer.parseInt(a[1]) + "!" + 3;
                            break;
                    }

                    String routingKey = "";
                    channel.basicPublish(exchangeName, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println(" [x] Sent '" + message + "'");

                } catch (IOException | TimeoutException ignored) {}
                break;
            }
            case "die": {
                String exchangeName = a[2];
                try (Connection connection = RabbitUtils.newConnection2Server(host, port, "guest", "guest");
                     Channel channel = RabbitUtils.createChannel2Server(connection)) {

                    // Declare a queue where to send msg (idempotent, i.e., it will only be created if it doesn't exist);
                    //channel.queueDeclare(queueName, false, false, false, null);
                    //channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                    System.out.println("[X] Declare exchange: '" + exchangeName + "' of type " + BuiltinExchangeType.FANOUT);
                    /* Set the Exchange type to MAIL_TO_ADDR FANOUT (multicast to all queues)*/
                    //channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

                    //Gets the message
                    String message = "kill!" + Integer.parseInt(a[1]);

                    String routingKey = "";
                    channel.basicPublish(exchangeName, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println(" [x] Sent '" + message + "'");

                } catch (IOException | TimeoutException ignored) {
                }
                break;
            }
            case "reset": {
                String exchangeName = a[2];
                try (Connection connection = RabbitUtils.newConnection2Server(host, port, "guest", "guest");
                     Channel channel = RabbitUtils.createChannel2Server(connection)) {

                    // Declare a queue where to send msg (idempotent, i.e., it will only be created if it doesn't exist);
                    //channel.queueDeclare(queueName, false, false, false, null);
                    //channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                    System.out.println("[X] Declare exchange: '" + exchangeName + "' of type " + BuiltinExchangeType.FANOUT);
                    /* Set the Exchange type to MAIL_TO_ADDR FANOUT (multicast to all queues)*/
                    //channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

                    //Gets the message
                    String message = "reset!" + Integer.parseInt(a[1]);
                    //String message = "Game joined!"+p;
                    //TimeUnit.SECONDS.sleep(1);


            /* Publish a message to the logs_exchange instead of the nameless one
            Fanout exchanges will ignore routingKey (hence set routingKey="")
            Messages will be lost if no queue is bound to the exchange yet */
                    String routingKey = "";
                    channel.basicPublish(exchangeName, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println(" [x] Sent '" + message + "'");

                } catch (IOException | TimeoutException ignored) {
                }
                break;
            }
            case "start": {
                String exchangeName = a[1];
                try (Connection connection = RabbitUtils.newConnection2Server(host, port, "guest", "guest");
                     Channel channel = RabbitUtils.createChannel2Server(connection)) {

                    // Declare a queue where to send msg (idempotent, i.e., it will only be created if it doesn't exist);
                    //channel.queueDeclare(queueName, false, false, false, null);
                    //channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                    System.out.println("[X] Declare exchange: '" + exchangeName + "' of type " + BuiltinExchangeType.FANOUT);
                    /* Set the Exchange type to MAIL_TO_ADDR FANOUT (multicast to all queues)*/
                    //channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

                    //Gets the message
                    String message = "start!";
                    //String message = "Game joined!"+p;
                    //TimeUnit.SECONDS.sleep(1);


            /* Publish a message to the logs_exchange instead of the nameless one
            Fanout exchanges will ignore routingKey (hence set routingKey="")
            Messages will be lost if no queue is bound to the exchange yet */
                    String routingKey = "";
                    channel.basicPublish(exchangeName, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println(" [x] Sent '" + message + "'");

                } catch (IOException | TimeoutException ignored) {
                }
                break;
            }
            case "godMode": {
                String exchangeName = a[2];
                try (Connection connection = RabbitUtils.newConnection2Server(host, port, "guest", "guest");
                     Channel channel = RabbitUtils.createChannel2Server(connection)) {

                    // Declare a queue where to send msg (idempotent, i.e., it will only be created if it doesn't exist);
                    //channel.queueDeclare(queueName, false, false, false, null);
                    //channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                    System.out.println("[X] Declare exchange: '" + exchangeName + "' of type " + BuiltinExchangeType.FANOUT);
                    /* Set the Exchange type to MAIL_TO_ADDR FANOUT (multicast to all queues)*/
                    //channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

                    //Gets the message
                    String message = "godMode!" + Integer.parseInt(a[1]);
                    //String message = "Game joined!"+p;
                    //TimeUnit.SECONDS.sleep(1);


                    String routingKey = "";
                    channel.basicPublish(exchangeName, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println(" [x] Sent '" + message + "'");

                } catch (IOException | TimeoutException ignored) {
                }
                break;
            }
            case "end": {
                String exchangeName = a[1];
                try (Connection connection = RabbitUtils.newConnection2Server(host, port, "guest", "guest");
                     Channel channel = RabbitUtils.createChannel2Server(connection)) {

                    // Declare a queue where to send msg (idempotent, i.e., it will only be created if it doesn't exist);
                    //channel.queueDeclare(queueName, false, false, false, null);
                    //channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                    System.out.println("[X] Declare exchange: '" + exchangeName + "' of type " + BuiltinExchangeType.FANOUT);
                    /* Set the Exchange type to MAIL_TO_ADDR FANOUT (multicast to all queues)*/
                    //channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

                    //Gets the message
                    String message = "end!";
                    //String message = "Game joined!"+p;
                    //TimeUnit.SECONDS.sleep(1);


                    String routingKey = "";
                    channel.basicPublish(exchangeName, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println(" [x] Sent '" + message + "'");

                } catch (IOException | TimeoutException ignored) {
                }
                break;
            }
            case "next": {
                String exchangeName = a[1];
                try (Connection connection = RabbitUtils.newConnection2Server(host, port, "guest", "guest");
                     Channel channel = RabbitUtils.createChannel2Server(connection)) {

                    // Declare a queue where to send msg (idempotent, i.e., it will only be created if it doesn't exist);
                    //channel.queueDeclare(queueName, false, false, false, null);
                    //channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                    System.out.println("[X] Declare exchange: '" + exchangeName + "' of type " + BuiltinExchangeType.FANOUT);
                    /* Set the Exchange type to MAIL_TO_ADDR FANOUT (multicast to all queues)*/
                    //channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

                    //Gets the message
                    String message = "next!";
                    //String message = "Game joined!"+p;
                    TimeUnit.SECONDS.sleep(5);

                    String routingKey = "";
                    channel.basicPublish(exchangeName, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println(" [x] Sent '" + message + "'");

                } catch (IOException | TimeoutException ignored) {
                }
                break;
            }
        }
    }
}
