package dpi.atlas.service.cm.source.jms;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cm.handler.CMHandlerAware;
import dpi.atlas.service.cm.source.CMSourceBase;
import dpi.atlas.util.mq.mypool.MQPoolConfig;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.util.ThreadPool;

import javax.jms.*;

/**
 * User: Shahram Boroon
 * Date: Nov 5, 2005
 * Time: 8:35:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class JMSMQSourceNew extends CMSourceBase implements JMSMQSourceNewMBean {
    private static Log log = LogFactory.getLog(JMSMQSourceNew.class);

    private String queue_manager_name;
    private String rec_queue;
    private String qlistener_class_name;
    private String queue_manager_hostname;
    private int queue_manager_port;
    private String quMngTransType;    

    int threadPoolInitSize;
    int threadPoolMaxSize;
    ThreadPool threadPool;
    private int dbConnectionPoolInitSize;
    private int dbConnectionPoolMaxSize;
    private int dbConnectionPoolOptimalSize;
    private static final boolean TRANSACTED = false;

    public JMSMQSourceNew() {
    }

    public void startService() throws Exception {
        QueueConnectionFactory queueConnectionFactory;
        QueueConnection queueConnection;
        QueueSession queueSession;
        Queue queue;

        try {
            queueConnectionFactory = new MQQueueConnectionFactory();
            ((MQQueueConnectionFactory) queueConnectionFactory).setQueueManager(queue_manager_name);
            ((MQQueueConnectionFactory) queueConnectionFactory).setHostName(queue_manager_hostname);
            ((MQQueueConnectionFactory) queueConnectionFactory).setPort(queue_manager_port);
            ((MQQueueConnectionFactory) queueConnectionFactory).setChannel("SYSTEM.DEF.SVRCONN");

            if (quMngTransType.equalsIgnoreCase(MQPoolConfig.LOCAL_TRANSPORT)) {
                ((MQQueueConnectionFactory) queueConnectionFactory).setTransportType(MQPoolConfig.LOCAL_TRANSPORT_ID);
                queueConnection = queueConnectionFactory.createQueueConnection();
            } else {
                ((MQQueueConnectionFactory) queueConnectionFactory).setTransportType(MQPoolConfig.REMOTE_TRANSPORT_ID);
                queueConnection = queueConnectionFactory.createQueueConnection("mquser", "");
            }


            queueConnection.start();
            queueSession = queueConnection.createQueueSession(TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            queue = queueSession.createQueue(rec_queue);




            MessageListener listener = (MessageListener) Class.forName(qlistener_class_name).newInstance();
            ((CMHandlerAware) listener).setHandler(getChainHandler());
            ((CMHandlerAware) listener).setCMSource(this);
            ((MQListner) listener).setPool(threadPool);
            ((MQListner) listener).setThreadPoolMaxSize(threadPoolMaxSize);
            CFSFacadeNew.initialize(dbConnectionPoolInitSize, dbConnectionPoolMaxSize, dbConnectionPoolOptimalSize);
            queueSession.createReceiver(queue).setMessageListener(listener);
//            if (log.isInfoEnabled()) log.info("JMS Channel started on Queue " + rec_queue);
            log.fatal("<<<<< JMS Config: Queue Manager Host:(" + queue_manager_hostname + "), Name:(" +
                    queue_manager_name + "), Port:(" + queue_manager_port + ") >>>>>");
        } catch (JMSException je) {
            log.error("Caught JMSException: " + je);
            Exception le = je.getLinkedException();
            if (le != null) log.error("linked exception: " + le);
        } catch (Exception e) {
            log.error("Exception occurred:" + e.toString());
            throw e;
        }
    }

    public void stopService() throws Exception {
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        queue_manager_name = cfg.get(Constants.QUEUE_MANAGER_NAME);
        queue_manager_hostname = cfg.get(Constants.QUEUE_MANAGER_HOST_IP);
        rec_queue = cfg.get(Constants.RECIEVE_QUEUE);
        quMngTransType = cfg.get(Constants.QUEUE_MANAGER_TRANS_TYPE);
        qlistener_class_name = cfg.get(Constants.QUEUE_LISTENER_CLASS_NAME);
        quMngTransType=MQPoolConfig.REMOTE_TRANSPORT;
        String threadPoolInitSizeStr = cfg.get(Constants.THREAD_POOL_INIT_SIZE);
        String threadPoolMaxSizeStr = cfg.get(Constants.THREAD_POOL_MAX_SIZE);
        checkConfigParameter(threadPoolInitSizeStr);
        checkConfigParameter(threadPoolMaxSizeStr);
        threadPoolInitSize = Integer.valueOf(threadPoolInitSizeStr);
        threadPoolMaxSize = Integer.valueOf(threadPoolMaxSizeStr);
        if (threadPoolMaxSize < threadPoolInitSize)
            threadPoolMaxSize = threadPoolInitSize;
        threadPool = new ThreadPool(threadPoolInitSize, threadPoolMaxSize);
        String dbConPoolInitSizeStr = cfg.get(Constants.DB_POOL_INIT_SIZE);
        String dbConPoolMaxSizeStr = cfg.get(Constants.DB_POOL_MAX_SIZE);
        String dbConPoolOptimalSizeStr = cfg.get(Constants.DB_POOL_OPTIMAL_SIZE);
        checkConfigParameter(dbConPoolInitSizeStr);
        checkConfigParameter(dbConPoolMaxSizeStr);
        checkConfigParameter(dbConPoolOptimalSizeStr);
        dbConnectionPoolOptimalSize = Integer.valueOf(dbConPoolOptimalSizeStr);
        dbConnectionPoolInitSize = Integer.valueOf(dbConPoolInitSizeStr);
        dbConnectionPoolMaxSize = Integer.valueOf(dbConPoolMaxSizeStr);
        String queueManagerPortStr = cfg.get(Constants.QUEUE_MANAGER_HOST_PORT);
        checkConfigParameter(queueManagerPortStr);
        queue_manager_port = Integer.valueOf(queueManagerPortStr);

    }

    private void checkConfigParameter(String param) throws ConfigurationException {
        if (param == null)
            throw new ConfigurationException(" Parameter ::  " + param + " is null");
    }


    public int getThreadPoolJobCount() {
        return threadPool.getJobCount();
    }

    public int getThreadPoolSize() {
        return threadPool.getPoolSize();
    }

    public int getThreadPoolMaxSize() {
        return threadPool.getMaxPoolSize();
    }

    public int getThreadPoolIdleCount() {
        return threadPool.getIdleCount();
    }

    public int getThreadPoolPendingCount() {
        return threadPool.getPendingCount();
    }

    public String getDBPoolHandlerNames() {
        return CFSFacadeNew.getDBPoolHandlerNames();
    }

    public long getUsedConnectionCount() {
        return CFSFacadeNew.getUsedConnectionCount();
    }

    public long getUnUsedConnectionCount() {
        return CFSFacadeNew.getUnUsedConnectionCount();
    }
}
