package edu.ufp.inf.sd.rabbitmqservices.project.chatgui;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rui
 */
public class Observer {

    //Reference for gui
    private final ObserverGuiClient gui;

    //Preferences for exchange...
    private final Channel channelToRabbitMq;
    private final String exchangeName;
    private final BuiltinExchangeType exchangeType;
    //private final String[] exchangeBindingKeys;
    private final String messageFormat;

    //Store received message to be get by gui
    private String receivedMessage;

    /**
     * @param gui
     */
    public Observer(ObserverGuiClient gui, String host, int port, String user, String pass, String exchangeName, BuiltinExchangeType exchangeType, String messageFormat) throws IOException, TimeoutException {
        this.gui=gui;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " going to attach observer to host: " + host + "...");

        Connection connection=RabbitUtils.newConnection2Server(host, port, user, pass);
        this.channelToRabbitMq=RabbitUtils.createChannel2Server(connection);

        this.exchangeName=exchangeName;
        this.exchangeType=exchangeType;
        //String[] bindingKeys={"",""};
        //this.exchangeBindingKeys=bindingKeys;
        this.messageFormat=messageFormat;

        bindExchangeToChannelRabbitMQ();
        attachConsumerToChannelExchangeWithKey();
    }

    /**
     * Binds the channel to given exchange name and type.
     */
    private void bindExchangeToChannelRabbitMQ() throws IOException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Declaring Exchange '" + this.exchangeName + "' with type " + this.exchangeType);

        /* TODO: Declare exchange type  */
        this.channelToRabbitMq.exchangeDeclare(this.exchangeName, this.exchangeType);
    }

    /**
     * Creates a Consumer associated with an unnamed queue.
     */
    public void attachConsumerToChannelExchangeWithKey() {
        try {
            /* TODO: Create a non-durable, exclusive, autodelete queue with a generated name.
                The string queueName will contain a random queue name (e.g. amq.gen-JzTY20BRgKO-HjmUJj0wLg) */

            String queueName = this.channelToRabbitMq.queueDeclare().getQueue();


            /* TODO: Create binding: tell exchange to send messages to a queue; fanout exchange ignores the last parameter (binding key) */
            String routingKey="";
            this.channelToRabbitMq.queueBind(queueName, exchangeName, routingKey);


            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " Created consumerChannel bound to Exchange " + this.exchangeName + "...");

            /* Use a DeliverCallback lambda function instead of DefaultConsumer to receive messages from queue;
               DeliverCallback is an interface which provides a single method:
                void handle(String tag, Delivery delivery) throws IOException; */
            DeliverCallback deliverCallback=(consumerTag, delivery) -> {
                String message=new String(delivery.getBody(), messageFormat);

                //Store the received message
                setReceivedMessage(message);
                System.out.println(" [x] Consumer Tag [" + consumerTag + "] - Received '" + message + "'");

                // TODO: Notify the GUI about the new message arrive
                gui.updateTextArea();


            };
            CancelCallback cancelCallback=consumerTag -> {
                System.out.println(" [x] Consumer Tag [" + consumerTag + "] - Cancel Callback invoked!");
            };

            // TODO: Consume with deliver and cancel callbacks
            this.channelToRabbitMq.basicConsume(queueName, true, deliverCallback, cancelCallback);

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString());
        }
    }

    /**
     * Publish messages to existing exchange instead of the nameless one.
     * - The routingKey is empty ("") since the fanout exchange ignores it.
     * - Messages will be lost if no queue is bound to the exchange yet.
     * - Basic properties can be: MessageProperties.PERSISTENT_TEXT_PLAIN, etc.
     */
    public void sendMessage(String msgToSend) throws IOException {
        //RoutingKey will be ignored by FANOUT exchange
        String routingKey="";
        BasicProperties prop = MessageProperties.PERSISTENT_TEXT_PLAIN;

        // TODO: Publish message
        this.channelToRabbitMq.basicPublish(this.exchangeName, routingKey, prop, msgToSend.getBytes("UTF-8"));
    }

    /**
     * @return the most recent message received from the broker
     */
    public String getReceivedMessage() {
        return receivedMessage;
    }

    /**
     * @param receivedMessage the received message to set
     */
    public void setReceivedMessage(String receivedMessage) {
        this.receivedMessage=receivedMessage;
    }
}
