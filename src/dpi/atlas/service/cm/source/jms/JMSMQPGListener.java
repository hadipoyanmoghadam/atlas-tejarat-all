package dpi.atlas.service.cm.source.jms;

import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandler;
import dpi.atlas.service.cm.handler.CMHandlerAware;
import dpi.atlas.service.cm.source.CMSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.util.ThreadPool;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * User: SH.Behnaz
 * Date: September 10, 2017
 * Time: 9:19:34 AM
 */
public class JMSMQPGListener implements MessageListener, CMHandlerAware {
    private static Log log = LogFactory.getLog(JMSMQPGListener.class);
    private CMSource cmSource;
    private ThreadPool pool;
    boolean useInternalConnectionPool = false;
    private int threadPoolMaxSize;
    private CMHandler handler;
    private Connector connector;

    public void onMessage(Message message) {
        if (counter++ % 1000 == 0) {
        log.fatal("ThreadPool idle count: " + pool.getIdleCount());
        log.fatal("ThreadPool size: " + pool.activeCount());
        }
        if (pool.getIdleCount() == 0 && pool.activeCount() >= threadPoolMaxSize) {
            log.fatal("ThreadPool actives more than max size, Total Threads = " + pool.activeCount() + ", Idle Threads = " + pool.getIdleCount() + " , MQListener threw away the request.");
            System.out.println("ThreadPool actives more than max size, Total Threads = " + pool.activeCount() + ", Idle Threads = " + pool.getIdleCount() + " , MQListener threw away the request.");
            try {
                CMMessage responseMsg = new CMMessage();
                if (message.getJMSCorrelationID() != null && !message.getJMSCorrelationID().equals("")) {
                    responseMsg.setAttribute("JMSCorrelationID", message.getJMSCorrelationID());
                } else {
                    responseMsg.setAttribute("JMSCorrelationID", message.getJMSMessageID());
                }
                if (message.getJMSReplyTo() != null)
                    responseMsg.setAttribute("JMSReplyTo", message.getJMSReplyTo());
                String  messageString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><ACTIONCODE>6004</ACTIONCODE><DESC> MQListener threw away the request</DESC></root>";

                responseMsg.setAttribute("result", messageString);
                connector.sendAsync(responseMsg);
            } catch (Exception e) {
            }
            return;
        }
        pool.execute(new PGMessageProcessor(message, handler, cmSource, connector));
    }

    static int counter = 0;


    public void setUseInternalConnectionPool(boolean useInternalConnectionPool) {
        this.useInternalConnectionPool = useInternalConnectionPool;
    }


    public ThreadPool getPool() {
        return pool;
    }

    public void setPool(ThreadPool pool) {
        this.pool = pool;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public void setHandler(CMHandler handler) {
        this.handler = handler;
    }


    public void setCMSource(CMSource cmSource) {
        this.cmSource = cmSource;
    }


    public int getThreadPoolMaxSize() {
        return threadPoolMaxSize;
    }

    public void setThreadPoolMaxSize(int threadPoolMaxSize) {
        this.threadPoolMaxSize = threadPoolMaxSize;
    }
}
