package dpi.atlas.util.mq.mypool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

import dpi.atlas.client.messages.BaseMessage;
import dpi.atlas.client.messages.ACHFundTransferMessage;
import dpi.atlas.server.util.Constants;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.CMMessage;

/**
 * Created by IntelliJ IDEA.
 * User: dp-admin
 * Date: Aug 2, 2008
 * Time: 11:12:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class MQSession {
    private static Log log = LogFactory.getLog(MQSession.class);
    private QueueSession session;
    private Queue receiveQueue;
    private Queue sendQueue;
    private QueueSender sender;
    private long timeout;
    int priority;
    private static String clientID = "Channel";
    private static String lastCorrelationID;
    private static boolean NON_TRANSACTED = false;
    static short correlationIdCounter;
    private String currentCorrelationId;
    String response;
    private boolean isSync;
    public static Map threads = Collections.synchronizedMap(new HashMap());


    public QueueSession getSession() {
        return session;
    }

    public QueueSender getSender() {
        return sender;
    }


    public MQSession(QueueConnection connection, String sendQueueName, String receiveQueueName,
                     long timeout, int priority, boolean isSync) throws JMSException {

        this.timeout = timeout;
        try {
            session = connection.createQueueSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            sendQueue = session.createQueue(sendQueueName);
            if ((receiveQueueName != null) && (!receiveQueueName.equalsIgnoreCase("")))
                receiveQueue = session.createQueue(receiveQueueName);
            sender = session.createSender(sendQueue);
            sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            sender.setPriority(priority);
            this.priority = priority;
            this.isSync = isSync;
        } catch (JMSException e) {
            log.error(e);
            throw e;
        }
    }

    public void send(String messageStr, String correlationId, long timeToLive) throws JMSException {
        Message message = session.createTextMessage(messageStr);
        message.setJMSCorrelationID(correlationId);
//        message.setBooleanProperty("isCompressed", false);
        log.debug("Before sending to queue. Message = " + messageStr);
        sender.send(message, DeliveryMode.NON_PERSISTENT, priority, timeToLive);
        log.debug("After sending to queue.");
        if (session.getTransacted())
            session.commit();
    }

    public void sendACHBytes(byte[] messageBytes, String correlationId, long timeToLive) throws JMSException {
        Message message = session.createBytesMessage();
        ((BytesMessage) message).writeBytes(messageBytes);
        message.setJMSCorrelationID(correlationId);
        message.setBooleanProperty("isCompressed", true);
        log.debug("Before sending to queue. Message = " + messageBytes);
        sender.send(message, DeliveryMode.NON_PERSISTENT, priority, timeToLive);
        log.debug("After sending to queue.");
        if (session.getTransacted())
            session.commit();
    }


    public void sendACH(CMMessage msg, String messageStr, String correlationId, long timeToLive) throws JMSException {
        Message message = session.createTextMessage(messageStr);
        message.setJMSCorrelationID(correlationId);
        message.setStringProperty(Constants.TJ_JAR_CLIENT_VERSION, Constants.TJ_JAR_CLIENT_VERSION_NO);
        message.setStringProperty(Constants.MSGSEQ, correlationId);
        message.setStringProperty(Constants.CHANNELID, "5");
        message.setStringProperty(dpi.atlas.util.Constants.IS_TJ_ACH_FILE, "Yes");
        message.setStringProperty(dpi.atlas.util.Constants.ACH_SRC_IBAN, msg.getAttributeAsString(dpi.atlas.util.Constants.ACH_SRC_IBAN));
        message.setStringProperty(dpi.atlas.util.Constants.ACH_DST_IBAN, msg.getAttributeAsString(dpi.atlas.util.Constants.ACH_DST_IBAN));
        message.setStringProperty(dpi.atlas.util.Constants.ACH_AMOUNT, msg.getAttributeAsString(dpi.atlas.util.Constants.ACH_AMOUNT));
        message.setStringProperty(dpi.atlas.util.Constants.PAYA_REF_ID, msg.getAttributeAsString(dpi.atlas.util.Constants.PAYA_REF_ID));
        message.setStringProperty(dpi.atlas.util.Constants.ACH_SESSION_ID, msg.getAttributeAsString(dpi.atlas.util.Constants.ACH_SESSION_ID));
        message.setStringProperty(dpi.atlas.util.Constants.IS_ACH_REQUEST, msg.getAttributeAsString(dpi.atlas.util.Constants.IS_ACH_REQUEST));
        message.setStringProperty(dpi.atlas.util.Constants.ACH_FT_REQ_MSG, msg.getAttributeAsString(dpi.atlas.util.Constants.ACH_FT_REQ_MSG));
        log.debug("Before sending to queue. Message = " + messageStr);
        sender.send(message, DeliveryMode.NON_PERSISTENT, priority, timeToLive);
        log.debug("After sending to queue.");
        if (session.getTransacted())
            session.commit();
    }

    public void sendBytes(byte[] byteMessage, String correlationId, long timeout, long timeToLive) throws JMSException {
        BytesMessage message = session.createBytesMessage();
        message.writeBytes(byteMessage);
        message.setJMSCorrelationID(correlationId);
        message.setJMSExpiration(timeout);
        log.debug("Before sending to queue. Message = ");
        sender.send(message, DeliveryMode.NON_PERSISTENT, priority, timeToLive);
        log.debug("After sending to queue.");
        if (session.getTransacted())
            session.commit();
    }

    public void sendObj(Object messageObj, String correlationId, long timeToLive) throws JMSException {
        Message message = session.createObjectMessage((BaseMessage) messageObj);
        message.setJMSCorrelationID(correlationId);
        log.debug("Before sending to queue. Message = " + messageObj);
        sender.send(message, DeliveryMode.NON_PERSISTENT, priority, timeToLive);
        log.debug("After sending to queue.");
        if (session.getTransacted())
            session.commit();
    }


    public synchronized static String getCorrelationID() {
        StringBuffer correlationID;
        do {
            correlationID = new StringBuffer(clientID);
            correlationID.append(String.valueOf(correlationIdCounter++));
        } while (correlationID.toString().equals(lastCorrelationID));
        lastCorrelationID = correlationID.toString();
        return correlationID.toString();
    }

    public void setListener(MessageListener listener) throws JMSException {
        QueueReceiver receiver = session.createReceiver(receiveQueue);
        receiver.setMessageListener(listener);
    }

    public String sendAndReceive(String msg, String correlationId, long timeToLive) throws JMSException, MQTimeoutException, Exception {
        response = null;
        if (!isSync)
            throw new Exception("MQPool is not sync");

        //TODO if msg or correlationId = null or "", an error must be issued
        currentCorrelationId = correlationId.trim();
        send(msg, currentCorrelationId, timeToLive);
        threads.put(currentCorrelationId, this);

        try {
            synchronized (currentCorrelationId) {
                currentCorrelationId.wait(timeout);
            }
            threads.remove(currentCorrelationId);
        } catch (Exception e) {
            log.error(e);
            e.toString();
        }
        if (response == null)
            throw new MQTimeoutException();

        return response;
    }

    public String getCurrentCorrelationId() {
        return currentCorrelationId;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}

