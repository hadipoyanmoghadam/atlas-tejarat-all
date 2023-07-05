package dpi.atlas.service.cm.source.jms;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.source.CMSourceBase;
import dpi.atlas.util.Constants;
import dpi.atlas.util.mq.mypool.MQPoolConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.util.ThreadPool;

import javax.jms.*;

/**
 * Created by IntelliJ IDEA.
 * User: SH.Behnaz
 * Date: September 6, 2017
 * Time: 10:35:40 AM
 */
public class JMSMQPGSource extends CMSourceBase {
    private static Log log = LogFactory.getLog(JMSMQPGSource.class);

    private String queueManagerName;
    private String receiveQueue;
    private String qListenerClassName;
    private String quMngHostIP, quMngTransType;
    private int quMngHostPort;

    ThreadPool threadPool;
    private int dbConnectionPoolInitSize;
    private int dbConnectionPoolMaxSize;
    private int dbConnectionPoolOptimalSize;
    private Connector connector;
    int threadPoolInitSize;
    int threadPoolMaxSize;
    private static final boolean TRANSACTED = false;

    public JMSMQPGSource() {
    }

    public void startService() throws Exception {
        QueueConnectionFactory queueConnectionFactory;
        QueueConnection queueConnection;
        Queue queue;
        try {
            queueConnectionFactory = new MQQueueConnectionFactory();
            ((MQQueueConnectionFactory) queueConnectionFactory).setQueueManager(queueManagerName);
            ((MQQueueConnectionFactory) queueConnectionFactory).setHostName(quMngHostIP);
            ((MQQueueConnectionFactory) queueConnectionFactory).setPort(quMngHostPort);
            ((MQQueueConnectionFactory) queueConnectionFactory).setChannel("SEPAMSRV");
            ((MQQueueConnectionFactory) queueConnectionFactory).setTransportType(MQPoolConfig.REMOTE_TRANSPORT_ID);
            queueConnection = queueConnectionFactory.createQueueConnection("MUSR_MQADMIN", "");


            queueConnection.start();
            QueueSession queueSession = queueConnection.createQueueSession(TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            queue = queueSession.createQueue(receiveQueue);
            JMSMQPGListener listener = (JMSMQPGListener) Class.forName(qListenerClassName).newInstance();
            ChannelFacadeNew.initialize(dbConnectionPoolInitSize, dbConnectionPoolMaxSize, dbConnectionPoolOptimalSize);
            log.info("Database Connection pool initialized.");

            listener.setHandler(getChainHandler());
            listener.setCMSource(this);
            listener.setThreadPoolMaxSize(threadPoolMaxSize);
            listener.setConnector(connector);
            listener.setPool(threadPool);

            queueSession.createReceiver(queue).setMessageListener(listener);
            log.info("MQ Listener started on queue " + receiveQueue);

        } catch (JMSException je) {
            log.error(je);
            Exception le = je.getLinkedException();
            if (le != null) log.error(le);
            relax();
            startService();
//         throw je;
        } catch (Exception e) {
            e.toString();
            log.error(e);
            throw e;
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {

        queueManagerName = cfg.get(Constants.QUEUE_MANAGER_NAME);
        receiveQueue = cfg.get(Constants.RECIEVE_QUEUE);
           quMngTransType = cfg.get(Constants.QUEUE_MANAGER_TRANS_TYPE);
        qListenerClassName = cfg.get(Constants.QUEUE_LISTENER_CLASS_NAME);
        quMngHostIP = cfg.get(Constants.QUEUE_MANAGER_HOST_IP);
        quMngHostPort = Integer.valueOf(cfg.get(Constants.QUEUE_MANAGER_HOST_PORT));
        String dbConPoolInitSizeStr = cfg.get(Constants.DB_POOL_INIT_SIZE);
        String dbConPoolMaxSizeStr = cfg.get(Constants.DB_POOL_MAX_SIZE);
        String dbConPoolOptimalSizeStr = cfg.get(Constants.DB_POOL_OPTIMAL_SIZE);
        String connectorStr = cfg.get(Constants.CONNECTOR);
        String threadPoolInitSizeStr = cfg.get(Constants.THREAD_POOL_INIT_SIZE);
        String threadPoolMaxSizeStr = cfg.get(Constants.THREAD_POOL_MAX_SIZE);

        checkConfigParameter(queueManagerName);
        checkConfigParameter(receiveQueue);
        checkConfigParameter(qListenerClassName);
        checkConfigParameter(quMngTransType);
        checkConfigParameter(dbConPoolInitSizeStr);
        checkConfigParameter(dbConPoolMaxSizeStr);
        checkConfigParameter(dbConPoolOptimalSizeStr);
        checkConfigParameter(connectorStr);
        checkConfigParameter(threadPoolInitSizeStr);
        checkConfigParameter(threadPoolMaxSizeStr);

        dbConnectionPoolInitSize = Integer.valueOf(dbConPoolInitSizeStr).intValue();
        dbConnectionPoolMaxSize = Integer.valueOf(dbConPoolMaxSizeStr).intValue();
        dbConnectionPoolOptimalSize = Integer.valueOf(dbConPoolOptimalSizeStr).intValue();
        connector = ChannelManagerEngine.getInstance().getConnector(connectorStr);
        threadPoolInitSize = Integer.valueOf(threadPoolInitSizeStr).intValue();
        threadPoolMaxSize = Integer.valueOf(threadPoolMaxSizeStr).intValue();
        if (threadPoolMaxSize < threadPoolInitSize)
            threadPoolMaxSize = threadPoolInitSize;
        threadPool = new ThreadPool(threadPoolInitSize, threadPoolMaxSize);

    }

    void checkConfigParameter(String param) throws ConfigurationException {
        if (param == null)
            throw new ConfigurationException();
    }
    private void relax() {
           try {
               Thread.sleep(5000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
}
