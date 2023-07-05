package branch.dpi.atlas.service.cm.source.branch;

import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchBaseChannel;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchChannel;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchServerChannel;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchServerSocketFactory;
import dpi.atlas.util.Constants;
import edu.emory.mathcs.backport.java.util.concurrent.ArrayBlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.util.ThreadPool;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.*;

/**
 * User: R.Nasiri
 * Date: April 13, 2014
 * Time: 01:41 PM
 */

public class CFDServer extends Observable
        implements Runnable, Observer {
    private static Log log = LogFactory.getLog(CFDServer.class);

    int port;
    BranchChannel clientSideChannel;
    Class clientSideChannelClass;
    Collection listeners;
    ThreadPool pool;
    ThreadPoolExecutor threadPoolExecutor;
    String securityClassName = null;


    public static final int DEFAULT_MAX_THREADS = 100;
    String name;
    protected BranchServerSocketFactory socketFactory = null;
    public static final int CONNECT = 0;
    public static final int SIZEOF_CNT = 1;
    private int[] cnt;
    //    private String[] allow;
    private boolean shutdown = false;
    private ServerSocketChannel serverSocketChannel;
    private Collection channels;

    private Map messageTypes;

    private float threadPoolIdleSizeThreshold = 100;
    private float threadPoolPendingSizeThreshold = 10;
//    private SocketChannel socketChannel;
//    private static BranchServerChannel channel;

//    public static BranchServerChannel getChannel() {
//        return channel;
//    }

    public void setSecurityClassName(String securityClassName) {
        this.securityClassName = securityClassName;
    }

    public float getThreadPoolIdleSizeThreshold() {
        return threadPoolIdleSizeThreshold;
    }

    public void setThreadPoolIdleSizeThreshold(float threadPoolIdleSizeThreshold) {
        this.threadPoolIdleSizeThreshold = threadPoolIdleSizeThreshold;
    }

    public float getThreadPoolPendingSizeThreshold() {
        return threadPoolPendingSizeThreshold;
    }

    public void setThreadPoolPendingSizeThreshold(float threadPoolPendingSizeThreshold) {
        this.threadPoolPendingSizeThreshold = threadPoolPendingSizeThreshold;
    }

    public CFDServer(int port, BranchServerChannel clientSide, ThreadPool pool) {
        super();
        this.port = port;
        this.clientSideChannel = clientSide;
        this.clientSideChannelClass = clientSide.getClass();
        this.pool = (pool == null) ?
                new ThreadPool(1, CFDServer.DEFAULT_MAX_THREADS) : pool;
        listeners = new Vector();
        name = "";
        channels = new HashSet();
        cnt = new int[CFDServer.SIZEOF_CNT];
    }

    public CFDServer(int port, BranchServerChannel clientSide, ThreadPoolExecutor pool) {
        super();
        this.port = port;
        this.clientSideChannel = clientSide;
        this.clientSideChannelClass = clientSide.getClass();
        this.threadPoolExecutor = (pool == null) ?
                new ThreadPoolExecutor(50, 250, 100000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(100)) : pool;
        listeners = new Vector();
        name = "";
        channels = new HashSet();
        cnt = new int[CFDServer.SIZEOF_CNT];
    }

    public void addBranchRequestListener(BranchRequestListener l) {
        listeners.add(l);
    }

    public void removeBranchRequestListener(BranchRequestListener l) {
        listeners.remove(l);
    }

    public void shutdown() {
        shutdown = true;
        new Thread() {
            public void run() {
                shutdownServer();
                shutdownChannels();
            }
        }.start();
    }

    private void shutdownServer() {
        try {
            if (serverSocketChannel != null)
                serverSocketChannel.close();
        } catch (IOException e) {
            //Logger.log (new LogEvent (this, "shutdown", e));
        }
    }

    private void shutdownChannels() {
        Iterator iter = channels.iterator();
        while (iter.hasNext()) {
            BranchChannel c = (BranchChannel) ((WeakReference) iter.next()).get();
            if (c != null) {
                try {
                    c.disconnect();
                } catch (IOException e) {
                    //Logger.log (new LogEvent (this, "shutdown", e));
                }
            }
        }
    }

    private void purgeChannels() {
        Iterator iter = channels.iterator();
        while (iter.hasNext()) {
            WeakReference ref = (WeakReference) iter.next();
            BranchChannel c = (BranchChannel) ref.get();
            if (c == null || (!c.isConnected()))
                iter.remove();
        }
    }

    public void run() {
        while (!shutdown) {
            try {
                if (socketFactory != null) {
                    serverSocketChannel = socketFactory.createServerSocket(port);
                } else {
                    serverSocketChannel = ServerSocketChannel.open();
                    serverSocketChannel.socket().bind(new InetSocketAddress(port));
                }

                while (!shutdown) {

                    try {
                        BranchServerChannel    channel = (BranchServerChannel) clientSideChannelClass.newInstance();
                        if (clientSideChannel instanceof BranchBaseChannel) {
                            ((BranchBaseChannel) channel).setTimeout(
                                    ((BranchBaseChannel) clientSideChannel).getTimeout());
                        }

                        if (channel instanceof Observable)
                            ((Observable) channel).addObserver(this);

                        channel.accept(serverSocketChannel);
                        serverSocketChannel.close();

                        if ((cnt[CFDServer.CONNECT]++) % 100 == 0)
                            purgeChannels();
                        channels.add(new WeakReference(channel));

                        for (; ; ) {
                            String messageString = channel.receiveBranch();
                            if (log.isDebugEnabled()) log.debug("messageString:" + messageString);

                            if (getIdleCount() == 0 && getActiveCount() >= getMaxPoolSize()) {

                                log.fatal("ThreadPool actives more than max size, pool.activeCount()= " + pool.activeCount() +
                                        ", pool.getAvailableCount()= " + pool.getAvailableCount() +
                                        " , pool.getIdleCount()= " + pool.getIdleCount() +
                                        " , pool.getJobCount()= " + pool.getJobCount() +
                                        " , getActiveCount()= " + getActiveCount() +
                                        " , MaxPoolSize = " + getMaxPoolSize() +
                                        " , pool.getPoolSize()= " + pool.getPoolSize() + " -- Request = " + messageString);

//                                messageString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><ACTIONCODE>6004</ACTIONCODE><DESC> Listener threw away the request</DESC></root>";

                                channel.sendBranch(messageString);

                                continue;
                            }

                            if (messageString.equals(Constants.CONNECTION_TEST)) {
                                channel.sendBranch(Constants.CONNECTION_TEST);
                            } else {

//----------------------------------------------------------------------------------------------------------------

                                if (pool != null) pool.execute(new CFDServer.MessageSession(messageString, channel));
                                else {
                                    threadPoolExecutor.execute(new CFDServer.MessageSession(messageString, channel));
                                }
                            }
                        }

                    } catch (Exception e) {
                        log.error("Exception : " + e);
                        serverSocketChannel = ServerSocketChannel.open();
                        serverSocketChannel.socket().bind(new InetSocketAddress(port));
                        continue;
                    }

                }
            } catch (Throwable e) {
                log.error("Throwable Exception : " + e);
                relax();
            }
        }
    }


    private void sendError(String messageString, BranchServerChannel channel, String internalErrorMsg) {
        try {
            channel.sendBranch(messageString);
        } catch (IOException e) {
            log.error("Can not send recent message to CFD : " + internalErrorMsg + ": " + e);
        }
    }

    private void relax() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
    }

    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public BranchServerSocketFactory getSocketFactory() {
        return socketFactory;
    }

    /**
     * Sets the specified Socket Factory to create sockets
     */
    public void setSocketFactory(BranchServerSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    public int getPort() {
        return port;
    }

    public void resetCounters() {
        cnt = new int[CFDServer.SIZEOF_CNT];
    }

    /**
     * @return number of connections accepted by this server
     */
    public int getConnectionCount() {
        return cnt[CFDServer.CONNECT];
    }

    // ThreadPoolMBean implementation (delegate calls to pool)
    public int getJobCount() {
        if (pool != null)
            return pool.getJobCount();
        else return Integer.valueOf("" + threadPoolExecutor.getCompletedTaskCount()).intValue();
//        return 0;
    }

    public int getPoolSize() {
        if (pool != null)
            return pool.getPoolSize();
        else return Integer.valueOf("" + threadPoolExecutor.getPoolSize()).intValue();
    }

    public int getMaxPoolSize() {
        if (pool != null)
            return pool.getMaxPoolSize();
        else return Integer.valueOf("" + threadPoolExecutor.getMaximumPoolSize()).intValue();
    }

    public int getIdleCount() {
        if (pool != null)
            return pool.getIdleCount();
        else
            return Integer.valueOf("" + (threadPoolExecutor.getMaximumPoolSize() - threadPoolExecutor.getActiveCount())).intValue();
    }

    public int getActiveCount() {
        if (pool != null)
            return (pool.activeCount());
        else
            return threadPoolExecutor.getActiveCount();
    }

    public int getPendingCount() {
        if (pool != null)
            return pool.getPendingCount();
        else
            return Integer.valueOf("" + (threadPoolExecutor.getLargestPoolSize())).intValue();
    }


    private class MessageSession implements Runnable {
        String msg;
        BranchServerChannel channel;


        public MessageSession(String msg, BranchServerChannel channel) {
            this.msg = msg;
            this.channel = channel;
        }

        public void run() {
            Iterator iter = listeners.iterator();
            while (iter.hasNext()) {
                BranchRequestListener branchRequestListener = ((BranchRequestListener) iter.next());
                if (branchRequestListener.process(channel, msg))
                    break;
            }
        }

    }


}
