package dpi.atlas.service.cm.source.jms;

import dpi.atlas.service.cm.handler.CMHandler;
import dpi.atlas.service.cm.handler.CMHandlerAware;
import dpi.atlas.service.cm.source.CMSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.util.ThreadPool;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * User: R.Nasiri
 * Date: Aug 09, 2020
 * Time: 01:51 PM
 */
public class JMSMQPAYAListener implements MessageListener, CMHandlerAware {
    private static Log log = LogFactory.getLog(JMSMQPAYAListener.class);
    private CMSource cmSource;
    private ThreadPool pool;
    boolean useInternalConnectionPool = false;
    private int threadPoolMaxSize;
    private CMHandler handler;

    public void onMessage(Message message) {
        if (counter++ % 1000 == 0) {
        log.fatal("ThreadPool idle count: " + pool.getIdleCount());
        log.fatal("ThreadPool size: " + pool.activeCount());
        }
        if (pool.getIdleCount() == 0 && pool.activeCount() >= threadPoolMaxSize) {
            log.fatal("ThreadPool actives more than max size, Total Threads = " + pool.activeCount() + ", Idle Threads = " + pool.getIdleCount() + " , MQListener threw away the request.");
            return;
        }
        pool.execute(new PAYAMessageProcessor(message, handler));
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
