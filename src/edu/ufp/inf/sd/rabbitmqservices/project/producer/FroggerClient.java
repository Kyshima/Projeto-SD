/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.rabbitmqservices.project.producer;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;
import froggermq.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example from RabbitMQ site:
 * https://www.rabbitmq.com/tutorials/tutorial-two-java.html
 *
 * Create a Work Queue (aka: Task Queue) that will be used to distribute
 * time-consuming tasks among multiple workers. Task Queues avoid doing a
 * resource-intensive task immediately and wait for it to complete. Instead
 * we schedule the task to be done later.
 * Encapsulate a task as a message and send it to a queue. A worker process
 * running in background will pop the tasks and eventually execute the job.
 * When running many workers, tasks will be shared between them.
 * This concept is especially useful in web apps where it is impossible to
 * handle a complex task during a short HTTP request time-window.
 *
 * We could send strings that stand for complex tasks (e.g. images to be resized
 * or pdf files to be rendered). Instead we fake tasks with Thread.sleep(1000);
 * for every dot on the message string, e.g., a fake task "Hello..." will
 * take 3 seconds.
 *
 * @author rui
 *
 */
public class FroggerClient {

    // Name of the queue
    //public final static String TASK_QUEUE_NAME = "task_queue";

    /**
     * Allow arbitrary messages to be sent from the command line.
     * @param args
     */
    public static void main(String[] args) throws Exception {
        RabbitUtils.printArgs(args);

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

        }

        String[] a = args[3].split("!");
        String exchangeName = "Exchange " + Integer.parseInt(a[1]);
        Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
        Channel channel=RabbitUtils.createChannel2Server(connection);
        // Declare a queue where to send msg (idempotent, i.e., it will only be created if it doesn't exist);
        //channel.queueDeclare(queueName, false, false, false, null);
        //channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        System.out.println("[X] Declare exchange: '" + exchangeName + "' of type " + BuiltinExchangeType.FANOUT.toString());
        /* Set the Exchange type to MAIL_TO_ADDR FANOUT (multicast to all queues)*/
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

        /*Create a non-durable, exclusive, autodelete queue with a generated name.
         The string queueName will contains a random queue name (e.g. amq.gen-JZTY20BRgKO-HjmUJj0wLg) */

        String queue = channel.queueDeclare().getQueue();
        /*String queue = "To client";
        channel.queueDeclare(queue, true, false, false, null);*/


        /*Create binding: tell exchange to send messages to a queue;
        The fanout exchange ignores last parameter (routing/binding key)*/

        String routingKey="";
        channel.queueBind(queue, exchangeName, routingKey);

        Logger.getAnonymousLogger().log(Level.INFO, Thread.currentThread().getName()+": Will create Deliver Callback...");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //DeliverCallback is an handler callback (lambda method) to consume messages pushed by the sender.
        //Create an handler callback to receive messages from queue
        DeliverCallback deliverCallback=(consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [X] Consumer tag [" + consumerTag + "] - Received '" + message + "'");
                doWork(message);
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(" [X] Consumer tag [" + consumerTag + "] - Cancel Callback invoked!");
        };
        channel.basicConsume(queue, true, deliverCallback, cancelCallback);

            //DO NOT close connection either channel otherwise it will terminate consumer

        /* Lastly, we close the channel and the connection... not anymore since try-with-resources closes resources! */
        //channel.close();
        //connection.close();
    }

    private static void doWork(String task) {
        System.out.println(task);
        String[] a = task.split("!");
        if (a[0].equals("Jogo criado")) {
            Main f = new Main(Integer.parseInt(a[1]));
            f.run();
        } else if (a[0].equals("Game joined")) {
            Main f = new Main(Integer.parseInt(a[1]));
            f.run();
        }
        else if (a[0].equals("Erro")) {
            System.out.println("Jogo cheio");
            System.exit(-1);
        }
    }
}
