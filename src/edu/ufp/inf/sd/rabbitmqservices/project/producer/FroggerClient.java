package edu.ufp.inf.sd.rabbitmqservices.project.producer;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;
import froggermq.Main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FroggerClient {

    // Name of the queue
    //public final static String TASK_QUEUE_NAME = "task_queue";

    /**
     * Allow arbitrary messages to be sent from the command line.
     */
    public static String[] arguments;

    public static Main m;
    public static String exchangeName;
    public static void main(String[] args) throws Exception {
        RabbitUtils.printArgs(args);
        arguments = args;
        connect2Server(arguments);
        client2Exchange(arguments);
    }

    private static void doWork(String task) throws RemoteException {
        System.out.println(task);
        String[] a = task.split("!");
        switch (a[0]) {
            case "Jogo criado":
            case "Game joined":
                m = new Main(Integer.parseInt(a[1]));
                m.run();
                break;
            case "move":
                m.moveFroggers(Integer.parseInt(a[1]), Integer.parseInt(a[2]));
                break;
            case "kill":
                m.die(Integer.parseInt(a[1]));
                break;
            case "reset":
                m.reset(Integer.parseInt(a[1]));
                break;
            case "start":
                m.start();
                break;
            case "godMode":
                m.godMode(Integer.parseInt(a[1]));
                break;
            case "end":
                m.finished();
                break;
            case "next":
                m.nextLevel();
                break;
            case "Erro":
                System.out.println("Jogo cheio");
                System.exit(-1);
        }
    }

    public static void movement_frogger(int frogid, int dir) throws IOException, TimeoutException {
        String host=arguments[0];
        int port=Integer.parseInt(arguments[1]);
        String queueName=arguments[2];
        //try-with-resources
        try (Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
             Channel channel=RabbitUtils.createChannel2Server(connection)) {

            boolean durable=true;
            channel.queueDeclare(queueName, durable, false, false, null);

            //String message = "Hello...";
            //Receive message to send via argv[3]
            String message= "move!"+frogid+"!"+dir+"!"+exchangeName;
            System.out.println(message);

            /* To avoid loosing queues when rabbitmq crashes, mark messages as persistent by setting
             MessageProperties (which implements BasicProperties) to value PERSISTENT_TEXT_PLAIN. */
            //channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes("UTF-8"));
            channel.basicPublish("", queueName,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

        client2Exchange(arguments);

    }

    public static void kill_frogger(int frogid) throws IOException, TimeoutException {
        String host=arguments[0];
        int port=Integer.parseInt(arguments[1]);
        String queueName=arguments[2];
        //try-with-resources
        try (Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
             Channel channel=RabbitUtils.createChannel2Server(connection)) {
            /* We must declare a queue to send to (this is idempotent, i.e. it will only be created if it doesn't exist;
               then we can publish a message to the queue;
               The message content is a byte array (can encode whatever we need).
               The previous queue was not Durable... persistent */
            //channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);

            /* Declare a queue as Durable (queue won't be lost even if RabbitMQ restarts);
               RabbitMQ does not allow redefine an existing queue with different parameters (hence create a new one) */
            boolean durable=true;
            channel.queueDeclare(queueName, durable, false, false, null);

            //String message = "Hello...";
            //Receive message to send via argv[3]
            String message= "die!"+frogid+"!"+exchangeName;
            System.out.println(message);
            //TimeUnit.SECONDS.sleep(2);

            /* To avoid loosing queues when rabbitmq crashes, mark messages as persistent by setting
             MessageProperties (which implements BasicProperties) to value PERSISTENT_TEXT_PLAIN. */
            //channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes("UTF-8"));
            channel.basicPublish("", queueName,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

        client2Exchange(arguments);

    }

    public static void reset_frogger(int frogid) throws IOException, TimeoutException {
        String host=arguments[0];
        int port=Integer.parseInt(arguments[1]);
        String queueName=arguments[2];
        //try-with-resources
        try (Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
             Channel channel=RabbitUtils.createChannel2Server(connection)) {
            /* We must declare a queue to send to (this is idempotent, i.e. it will only be created if it doesn't exist;
               then we can publish a message to the queue;
               The message content is a byte array (can encode whatever we need).
               The previous queue was not Durable... persistent */
            //channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);

            /* Declare a queue as Durable (queue won't be lost even if RabbitMQ restarts);
               RabbitMQ does not allow redefine an existing queue with different parameters (hence create a new one) */
            boolean durable=true;
            channel.queueDeclare(queueName, durable, false, false, null);

            //String message = "Hello...";
            //Receive message to send via argv[3]
            String message= "reset!"+frogid+"!"+exchangeName;
            System.out.println(message);
            //TimeUnit.SECONDS.sleep(2);

            /* To avoid loosing queues when rabbitmq crashes, mark messages as persistent by setting
             MessageProperties (which implements BasicProperties) to value PERSISTENT_TEXT_PLAIN. */
            //channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes("UTF-8"));
            channel.basicPublish("", queueName,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

        client2Exchange(arguments);

    }

    public static void start_frogger() throws IOException, TimeoutException {
        String host=arguments[0];
        int port=Integer.parseInt(arguments[1]);
        String queueName=arguments[2];
        //try-with-resources
        try (Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
             Channel channel=RabbitUtils.createChannel2Server(connection)) {
            /* We must declare a queue to send to (this is idempotent, i.e. it will only be created if it doesn't exist;
               then we can publish a message to the queue;
               The message content is a byte array (can encode whatever we need).
               The previous queue was not Durable... persistent */
            //channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);

            /* Declare a queue as Durable (queue won't be lost even if RabbitMQ restarts);
               RabbitMQ does not allow redefine an existing queue with different parameters (hence create a new one) */
            boolean durable=true;
            channel.queueDeclare(queueName, durable, false, false, null);

            //String message = "Hello...";
            //Receive message to send via argv[3]
            String message= "start!"+exchangeName;
            System.out.println(message);
            //TimeUnit.SECONDS.sleep(2);

            /* To avoid loosing queues when rabbitmq crashes, mark messages as persistent by setting
             MessageProperties (which implements BasicProperties) to value PERSISTENT_TEXT_PLAIN. */
            //channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes("UTF-8"));
            channel.basicPublish("", queueName,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

        client2Exchange(arguments);

    }

    public static void godMode(int frogid) throws IOException, TimeoutException {
        String host=arguments[0];
        int port=Integer.parseInt(arguments[1]);
        String queueName=arguments[2];
        //try-with-resources
        try (Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
             Channel channel=RabbitUtils.createChannel2Server(connection)) {
            /* We must declare a queue to send to (this is idempotent, i.e. it will only be created if it doesn't exist;
               then we can publish a message to the queue;
               The message content is a byte array (can encode whatever we need).
               The previous queue was not Durable... persistent */
            //channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);

            /* Declare a queue as Durable (queue won't be lost even if RabbitMQ restarts);
               RabbitMQ does not allow redefine an existing queue with different parameters (hence create a new one) */
            boolean durable=true;
            channel.queueDeclare(queueName, durable, false, false, null);

            //String message = "Hello...";
            //Receive message to send via argv[3]
            String message= "godMode!"+frogid+"!"+exchangeName;
            System.out.println(message);
            //TimeUnit.SECONDS.sleep(2);

            /* To avoid loosing queues when rabbitmq crashes, mark messages as persistent by setting
             MessageProperties (which implements BasicProperties) to value PERSISTENT_TEXT_PLAIN. */
            //channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes("UTF-8"));
            channel.basicPublish("", queueName,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

        client2Exchange(arguments);

    }
    public static void endLevel() throws IOException, TimeoutException {
        String host=arguments[0];
        int port=Integer.parseInt(arguments[1]);
        String queueName=arguments[2];
        //try-with-resources
        try (Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
             Channel channel=RabbitUtils.createChannel2Server(connection)) {
            /* We must declare a queue to send to (this is idempotent, i.e. it will only be created if it doesn't exist;
               then we can publish a message to the queue;
               The message content is a byte array (can encode whatever we need).
               The previous queue was not Durable... persistent */
            //channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);

            /* Declare a queue as Durable (queue won't be lost even if RabbitMQ restarts);
               RabbitMQ does not allow redefine an existing queue with different parameters (hence create a new one) */
            boolean durable=true;
            channel.queueDeclare(queueName, durable, false, false, null);

            //String message = "Hello...";
            //Receive message to send via argv[3]
            String message= "end!"+exchangeName;
            System.out.println(message);
            //TimeUnit.SECONDS.sleep(2);

            /* To avoid loosing queues when rabbitmq crashes, mark messages as persistent by setting
             MessageProperties (which implements BasicProperties) to value PERSISTENT_TEXT_PLAIN. */
            //channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes("UTF-8"));
            channel.basicPublish("", queueName,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

        client2Exchange(arguments);

    }
    public static void nextLevel() throws IOException, TimeoutException {
        String host=arguments[0];
        int port=Integer.parseInt(arguments[1]);
        String queueName=arguments[2];
        //try-with-resources
        try (Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
             Channel channel=RabbitUtils.createChannel2Server(connection)) {
            /* We must declare a queue to send to (this is idempotent, i.e. it will only be created if it doesn't exist;
               then we can publish a message to the queue;
               The message content is a byte array (can encode whatever we need).
               The previous queue was not Durable... persistent */
            //channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);

            /* Declare a queue as Durable (queue won't be lost even if RabbitMQ restarts);
               RabbitMQ does not allow redefine an existing queue with different parameters (hence create a new one) */
            boolean durable=true;
            channel.queueDeclare(queueName, durable, false, false, null);

            //String message = "Hello...";
            //Receive message to send via argv[3]
            String message= "next!"+exchangeName;
            System.out.println(message);
            //TimeUnit.SECONDS.sleep(2);

            /* To avoid loosing queues when rabbitmq crashes, mark messages as persistent by setting
             MessageProperties (which implements BasicProperties) to value PERSISTENT_TEXT_PLAIN. */
            //channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes("UTF-8"));
            channel.basicPublish("", queueName,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

        client2Exchange(arguments);

    }

    public static void connect2Server(String[] args) {
        //Read args passed via shell command
        String host=args[0];
        int port=Integer.parseInt(args[1]);
        String queueName=args[2];
        //try-with-resources
        try (Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
             Channel channel=RabbitUtils.createChannel2Server(connection)) {
            /* We must declare a queue to send to (this is idempotent, i.e. it will only be created if it doesn't exist;
               then we can publish a message to the queue;
               The message content is a byte array (can encode whatever we need).
               The previous queue was not Durable... persistent */
            //channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);

            /* Declare a queue as Durable (queue won't be lost even if RabbitMQ restarts);
               RabbitMQ does not allow redefine an existing queue with different parameters (hence create a new one) */
            boolean durable=true;
            channel.queueDeclare(queueName, durable, false, false, null);

            //String message = "Hello...";
            //Receive message to send via argv[3]
            String message= RabbitUtils.getMessage(args, 3);
            System.out.println(message);

            /* To avoid loosing queues when rabbitmq crashes, mark messages as persistent by setting
             MessageProperties (which implements BasicProperties) to value PERSISTENT_TEXT_PLAIN. */
            //channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes("UTF-8"));
            channel.basicPublish("", queueName,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static void client2Exchange(String[] args) throws IOException, TimeoutException {
        String host=args[0];
        int port=Integer.parseInt(args[1]);
        String[] a = args[3].split("!");
        exchangeName = "Exchange " + Integer.parseInt(a[1]);
        Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
        Channel channel=RabbitUtils.createChannel2Server(connection);
        System.out.println("[X] Declare exchange: '" + exchangeName + "' of type " + BuiltinExchangeType.FANOUT);
        /* Set the Exchange type to MAIL_TO_ADDR FANOUT (multicast to all queues)*/
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

        /*Create a non-durable, exclusive, autodelete queue with a generated name.
         The string queueName will contains a random queue name (e.g. amq.gen-JZTY20BRgKO-HjmUJj0wLg) */

        String queue = channel.queueDeclare().getQueue();



        /*Create binding: tell exchange to send messages to a queue;
        The fanout exchange ignores last parameter (routing/binding key)*/

        String routingKey="";
        channel.queueBind(queue, exchangeName, routingKey);

        Logger.getAnonymousLogger().log(Level.INFO, Thread.currentThread().getName()+": Will create Deliver Callback...");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //DeliverCallback is an handler callback (lambda method) to consume messages pushed by the sender.
        //Create an handler callback to receive messages from queue
        DeliverCallback deliverCallback=(consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [X] Consumer tag [" + consumerTag + "] - Received '" + message + "'");
            doWork(message);
        };
        CancelCallback cancelCallback = (consumerTag) -> System.out.println(" [X] Consumer tag [" + consumerTag + "] - Cancel Callback invoked!");
        channel.basicConsume(queue, true, deliverCallback, cancelCallback);
    }
}
