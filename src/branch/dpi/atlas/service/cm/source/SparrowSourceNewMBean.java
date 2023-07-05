package branch.dpi.atlas.service.cm.source;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Nov 30, 2007
 * Time: 8:41:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SparrowSourceNewMBean {
    void setSparrowStatus(long sparrowStatus) throws java.lang.Exception;

//    int getDBPoolFreeSize(String handlerName);

    String getDBPoolHandlerNames();



    int getConnectionCount();

    // ThreadPoolMBean implementation (delegate calls to pool)
    int getThreadPoolJobCount();

    int getThreadPoolSize();

    int getThreadPoolMaxSize();

    int getThreadPoolIdleCount();

    int getThreadPoolPendingCount();

    float getThreadPoolIdleSizeThreshold();

    void setThreadPoolIdleSizeThreshold(float threadPoolIdleSizeThreshold);

    float getThreadPoolPendingSizeThreshold() ;

    void setThreadPoolPendingSizeThreshold(float threadPoolPendingSizeThreshold);


}
