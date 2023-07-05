package dpi.atlas.util.mq.mypool;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import java.util.*;


/**
 * @author Kianoosh Rezabeigi.
 *         Date: Jun 18, 2007
 *         Time: 2:13:02 AM
 *         M.S. Student of Sharif University
 */
public class MQSessionPool {
    private static Log log = LogFactory.getLog(MQSessionPool.class);

    List connections = Collections.synchronizedList(new ArrayList());
    List idleMQSessions = Collections.synchronizedList(new ArrayList());
    List workingMQSessions = Collections.synchronizedList(new ArrayList());

    MQPoolConfig config;
    boolean isStarted = false;

    public MQSessionPool(MQPoolConfig config) throws JMSException {
        this.config = config;
        start();
    }

    private synchronized void start() throws JMSException {
        MQQueueConnectionFactory connectionFactory = new MQQueueConnectionFactory();
        QueueConnection connection;
        connectionFactory.setUseConnectionPooling(true);
//        config.setTransportType(MQPoolConfig.LOCAL_TRANSPORT);
        config.setTransportType(MQPoolConfig.REMOTE_TRANSPORT);

//        synchronized (workingMQSessions)
        {
//            synchronized (idleMQSessions)
            {
//                synchronized (connections)
                {


                    try {
                        if (config.getTransportType().equalsIgnoreCase(MQPoolConfig.REMOTE_TRANSPORT)) {
                            connectionFactory.setTransportType(MQPoolConfig.REMOTE_TRANSPORT_ID);
                            connectionFactory.setHostName(config.getHostName());
                            connectionFactory.setChannel(config.getChannel_name());
                        }
                        connectionFactory.setPort(config.getHostPort());
                        connectionFactory.setQueueManager(config.getQueue_manager_name());
                        log.info("Start of creating QueueConnections and QueueSessions.");
                        long start = System.currentTimeMillis();

                        for (int i = 0; i < config.getNumberOfConnections(); i++) {
                            if (log.isInfoEnabled())
                                start = System.currentTimeMillis();

//                            System.out.println("I am trying to get a connection to qm name:" + connectionFactory.getQueueManager() +
//                                    ", qm IP:" + connectionFactory.getHostName() + " , qm port:" + connectionFactory.getPort() +
//                                    " and transport type:" + connectionFactory.getTransportType());
                            log.info("Creating QueueConnection #" + (i + 1) + "  ...");
                            connection = connectionFactory.createQueueConnection(config.getMQusername(), config.getMQuserPass());
//                            System.out.println("I got my connection");
                            log.info("Creating QueueConnection #" + (i + 1) + " lasted " + (System.currentTimeMillis() - start) + " ms");
                            
                            if (log.isInfoEnabled())
                                start = System.currentTimeMillis();
                            log.info("Starting QueueConnection #" + (i + 1) + " ...");
                            connection.start();
                            log.info("Starting QueueConnection #" + (i + 1) + " lasted " + (System.currentTimeMillis() - start) + " ms");
                            connections.add(connection);
                        }
                        Iterator connectionsIterator = connections.iterator();

                        int connNo = 1;
                        while (connectionsIterator.hasNext()) {
                            QueueConnection conn = (QueueConnection) connectionsIterator.next();
                            log.info("Start of creating QueueSessions of QueueConnection #" + connNo);
                            for (int j = 0; j < config.getNumberOfSessionsPerConnection(); j++) {
                                if (log.isInfoEnabled()) {
                                    start = System.currentTimeMillis();
                                }
                                addIdleMQSession(new MQSession(conn, config.getSendQueueName(),
                                        config.getReciveQueueName(), config.getTimeout(), config.getPriority(),
                                        config.isSyncConnector()));
                                log.info("Creating QueueSession #" + (j + 1) + " of QueueConnection #" + connNo + " lasted " + (System.currentTimeMillis() - start) + " ms");
                            }
                            connNo++;
                        }
                    } catch (JMSException e) {
//                        System.out.println("There is an JMSException happening herer when reciver queue is:!!" +
//                                config.getReciveQueueName() +
//                                " and send queu is:" + config.getSendQueueName() +
//                                " and transport type is:" + config.getTransportType() + "," +
//                                config.getQueue_manager_name() + "," + config.isSyncConnector());
                        if (e.getLinkedException() != null)
                            log.error(e.getLinkedException());
                        log.error(e);
                        throw e;
                    }
                }
            }
        }
        isStarted = true;
        if (config.isSyncConnector())
            try {
                getMQSession().setListener(new ResponseListener());
            } catch (Exception e) {
//                System.out.println("***** here there is an Execption in isSyncConnector part");
                log.error(e);
            }
    }

    class ResponseListener implements MessageListener {

        public void onMessage(Message message) {
            try {
                MQSession session = (MQSession) MQSession.threads.get(message.getJMSCorrelationID());
                if (session != null) {
                    session.setResponse(((TextMessage) message).getText());
                    synchronized (session.getCurrentCorrelationId()) {
                        session.getCurrentCorrelationId().notify();
                    }
                } else {
                    log.error("Received response was not mapped to any request based on its correlationID.");
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    private void addIdleMQSession(MQSession session) {
//        synchronized (idleMQSessions)
        {
            idleMQSessions.add(session);
        }
    }

    public synchronized MQSession getMQSession() throws NoIdleSessionException, JMSException {
        if (!isStarted())
            throw new NoIdleSessionException("MQSessionPool is not started");
//        synchronized (idleMQSessions)
        {
//            synchronized (connections)
            {
                {
                    if (idleMQSessions.size() > 0) {
                        log.debug("Free MQ Sessions NO = " + idleMQSessions.size());
                        MQSession session = (MQSession) idleMQSessions.remove(0);
//                        synchronized (workingMQSessions)
                        {
                            workingMQSessions.add(session);
                        }
                        return session;
                    } else if (connections.size() > 0) {
                        MQSession session = new MQSession((QueueConnection) connections.get(0), config.getSendQueueName(),
                                config.getReciveQueueName(), config.getTimeout(), config.getPriority(),
                                config.isSyncConnector());
                        log.info("New Session created out of the pool !");
//                        synchronized (workingMQSessions)
                        {
                            workingMQSessions.add(session);
                        }
                        return session;
                    }
                }
            }
        }
        throw new NoIdleSessionException("No more free MQ Session is available in SessionPool.");
    }

    public synchronized void freeMQSession(MQSession session) {
        if (session != null)
//            synchronized (workingMQSessions)
        {
//                synchronized (idleMQSessions)
            {
                log.debug("MQSessions NO: Free = " + idleMQSessions.size() + "  Allocated = " + workingMQSessions.size());
                if (workingMQSessions.contains(session))
                    workingMQSessions.remove(session);
                addIdleMQSession(session);
            }
        }
    }

    public synchronized void stop() {
        isStarted = false;
        synchronized (workingMQSessions) {
            synchronized (idleMQSessions) {
                synchronized (connections) {

                    try {
                        while (idleMQSessions.size() > 0) {
                            MQSession session = (MQSession) idleMQSessions.remove(0);
                            if (session.getSession().getTransacted())
                                session.getSession().rollback();
                            session.getSender().close();
                            session.getSession().close();
                        }
                        while (workingMQSessions.size() > 0) {
                            MQSession session = (MQSession) workingMQSessions.remove(0);
                            if (session.getSession().getTransacted())
                                session.getSession().rollback();
                            session.getSender().close();
                            session.getSession().close();
                        }

                    } catch (JMSException e) {
                    } finally {
                        try {
                            while (connections.size() > 0) {
                                QueueConnection connection = (QueueConnection) connections.remove(0);
                                connection.stop();
                                connection.close();
                            }
                        } catch (Exception e) {
                            log.error(e);
                        }
                        connections.clear();
                        idleMQSessions.clear();
                        workingMQSessions.clear();
                    }
                }
            }
        }
    }

    public synchronized void reconnect() throws JMSException {
        synchronized (this) {
            stop();
            start();
        }
    }

    public boolean isStarted() {
        return isStarted;
    }

    public synchronized void remove(MQSession session) {
        if (idleMQSessions.contains(session))
            idleMQSessions.remove(session);
        if (workingMQSessions.contains(session))
            workingMQSessions.remove(session);
    }

}

