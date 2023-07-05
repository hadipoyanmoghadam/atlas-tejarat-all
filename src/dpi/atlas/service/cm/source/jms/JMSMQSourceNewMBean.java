package dpi.atlas.service.cm.source.jms;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Dec 5, 2007
 * Time: 5:55:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface JMSMQSourceNewMBean {
    int getThreadPoolJobCount();

    int getThreadPoolSize();

    int getThreadPoolMaxSize();

    int getThreadPoolIdleCount();

    int getThreadPoolPendingCount();

    String getDBPoolHandlerNames();

    long getUsedConnectionCount();

    long getUnUsedConnectionCount();
    
}
