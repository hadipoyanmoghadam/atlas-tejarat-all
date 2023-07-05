package branch.dpi.atlas.service.cm.source.branch;

import dpi.atlas.service.cm.source.CMSourceBase;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchASCIIChannel;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchChannel;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchServerChannel;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.util.StringUtils;
import org.jpos.util.ThreadPool;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: Mar 27, 2013
 * Time: 10:43:34 AM
 */
public class BranchSource extends CMSourceBase {

    private int dbConnectionPoolInitSize;
    private int dbConnectionPoolOptimalSize;
    private int dbConnectionPoolMaxSize;
    private int port = 9060;
    public static final int PORT_DEFAULT = 9060;
    ThreadPool threadPool;

    private BranchServer server;
    BranchListener listener;

    public BranchSource()
            throws Exception {
        this(PORT_DEFAULT);
    }

    public BranchSource(int port)
            throws Exception {
        this.port = port;
    }

    public void startService()
            throws Exception {

        BranchChannel branchChannel = new BranchASCIIChannel();
        server = new BranchServer(port, (BranchServerChannel) branchChannel, threadPool);

        listener = new BranchListener();

        ChannelFacadeNew.initialize(dbConnectionPoolInitSize, dbConnectionPoolMaxSize, dbConnectionPoolOptimalSize);
        listener.setChainHandler(getChainHandler());
        server.addBranchRequestListener(listener);
        new Thread(server).start();


    }

    /**
     * stops the web server
     *
     * @throws Exception
     */
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

        String s_port = cfg.get("port");
        if ((s_port != null) && (!s_port.trim().equals("")))
            port = Integer.parseInt(s_port);

        StringUtils.checkConfigParameter(threadPoolInitSizeStr);
        StringUtils.checkConfigParameter(threadPoolMaxSizeStr);
        StringUtils.checkConfigParameter(dbConnectionPoolInitSizeStr);
        StringUtils.checkConfigParameter(dbConnectionPoolOptimalSizeStr);
        StringUtils.checkConfigParameter(dbConnectionPoolMaxSizeStr);

        dbConnectionPoolInitSize = Integer.valueOf(dbConnectionPoolInitSizeStr).intValue();
        dbConnectionPoolOptimalSize = Integer.valueOf(dbConnectionPoolOptimalSizeStr);
        dbConnectionPoolMaxSize = Integer.valueOf(dbConnectionPoolMaxSizeStr).intValue();

        if (dbConnectionPoolOptimalSize < dbConnectionPoolInitSize)
            dbConnectionPoolOptimalSize = dbConnectionPoolInitSize;

        if (dbConnectionPoolMaxSize < dbConnectionPoolOptimalSize)
            dbConnectionPoolMaxSize = dbConnectionPoolOptimalSize;


        int threadPoolInitSize = Integer.valueOf(threadPoolInitSizeStr).intValue();
        int threadPoolMaxSize = Integer.valueOf(threadPoolMaxSizeStr).intValue();

        if (threadPoolMaxSize < threadPoolInitSize) {
            threadPoolMaxSize = threadPoolInitSize;
        }
        threadPool = new ThreadPool(threadPoolInitSize, threadPoolMaxSize);

    }

}
