package branch.dpi.atlas.service.cm.source.branch;

import branch.dpi.atlas.service.cm.source.branch.socket.BranchASCIIChannel;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchChannel;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchServerChannel;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.source.CMSourceBase;
import branch.dpi.atlas.service.cm.source.SparrowSourceNewMBean;
import dpi.atlas.util.StringUtils;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.util.ThreadPool;

/**
 * User: R.Nasiri
 * Date: March 12, 2020
 * Time: 10:50 AM
 */

public class GroupWebSource extends CMSourceBase implements SparrowSourceNewMBean {

    public static final int PORT_DEFAULT = 9060;
    private int port = 9060;
    private GroupWebServer server;
    ThreadPool threadPool = null;

    private int dbConnectionPoolInitSize;
    private int dbConnectionPoolMaxSize;
    private int dbConnectionPoolOptimalSize;



    public GroupWebSource()
            throws Exception {
        this(PORT_DEFAULT);
    }

    public GroupWebSource(int port)
            throws Exception {
        this.port = port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void startService()
            throws Exception {
        BranchChannel branchChannel = new BranchASCIIChannel();

        server = new GroupWebServer(port, (BranchServerChannel) branchChannel, threadPool);

        GroupWebListener listener = new GroupWebListener();

        ChannelFacadeNew.initialize(dbConnectionPoolInitSize, dbConnectionPoolMaxSize, dbConnectionPoolOptimalSize);

        listener.setChainHandler(getChainHandler());
        server.addBranchRequestListener(listener);
        new Thread(server).start();

    }

    public void stopService()
            throws Exception {
        server.shutdown();
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {

        String dbConnectionPoolInitSizeStr = cfg.get("dbConnectionPoolInitSize");
        String dbConnectionPoolOptimalSizeStr = cfg.get("dbConnectionPoolOptimalSize");
        String dbConnectionPoolMaxSizeStr = cfg.get("dbConnectionPoolMaxSize");
        String threadPoolInitSizeStr = cfg.get("threadPoolInitSize");
        String threadPoolMaxSizeStr = cfg.get("threadPoolMaxSize");
        String strPort = cfg.get("port");

        StringUtils.checkConfigParameter(threadPoolInitSizeStr);
        StringUtils.checkConfigParameter(threadPoolMaxSizeStr);
        StringUtils.checkConfigParameter(dbConnectionPoolInitSizeStr);
        StringUtils.checkConfigParameter(dbConnectionPoolMaxSizeStr);
        StringUtils.checkConfigParameter(dbConnectionPoolOptimalSizeStr);


        dbConnectionPoolOptimalSize = Integer.valueOf(dbConnectionPoolOptimalSizeStr);
        dbConnectionPoolInitSize = Integer.valueOf(dbConnectionPoolInitSizeStr);
        dbConnectionPoolMaxSize = Integer.valueOf(dbConnectionPoolMaxSizeStr);

        if (dbConnectionPoolOptimalSize < dbConnectionPoolInitSize)
            dbConnectionPoolOptimalSize = dbConnectionPoolInitSize;

        if (dbConnectionPoolMaxSize < dbConnectionPoolOptimalSize)
            dbConnectionPoolMaxSize = dbConnectionPoolOptimalSize;

        port = Integer.valueOf(strPort);
        int threadPoolInitSize = Integer.valueOf(threadPoolInitSizeStr);
        int threadPoolMaxSize = Integer.valueOf(threadPoolMaxSizeStr);

        if (threadPoolMaxSize < threadPoolInitSize) {
            threadPoolMaxSize = threadPoolInitSize;
        }
        threadPool = new ThreadPool(threadPoolInitSize, threadPoolMaxSize);

    }

    public void setSparrowStatus(long sparrowStatus) throws Exception {
    }

    public String getDBPoolHandlerNames() {
        return ChannelFacadeNew.getDBPoolHandlerNames();
    }

    public int getConnectionCount() {
        return server.getConnectionCount();
    }

    public int getThreadPoolJobCount() {
        return server.getJobCount();
    }

    public int getThreadPoolSize() {
        return server.getPoolSize();
    }

    public int getThreadPoolMaxSize() {
        return server.getMaxPoolSize();
    }

    public int getThreadPoolIdleCount() {
        return server.getIdleCount();
    }

    public int getThreadPoolPendingCount() {
        return server.getPendingCount();
    }

    public float getThreadPoolIdleSizeThreshold() {
        return server.getThreadPoolIdleSizeThreshold();
    }

    public void setThreadPoolIdleSizeThreshold(float threadPoolIdleSizeThreshold) {
        server.setThreadPoolIdleSizeThreshold(threadPoolIdleSizeThreshold);
    }

    public float getThreadPoolPendingSizeThreshold() {
        return server.getThreadPoolPendingSizeThreshold();
    }

    public void setThreadPoolPendingSizeThreshold(float threadPoolPendingSizeThreshold) {
        server.setThreadPoolPendingSizeThreshold(threadPoolPendingSizeThreshold);
    }

}
