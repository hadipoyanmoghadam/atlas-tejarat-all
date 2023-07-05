package branch.dpi.atlas.service.cm.source.branch;

import branch.dpi.atlas.service.cm.source.branch.socket.BranchBaseChannel;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchChannel;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchServerChannel;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchServerSocketFactory;
import dpi.atlas.service.cm.common.CMConstants;
import branch.dpi.atlas.service.cm.handler.exception.SocketConnectionException;
import dpi.atlas.service.cm.iso2000.ActionCode;
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
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * User: R.Nasiri
 * Date: Jun 13, 2015
 * Time: 11:43 AM
 */
public class AMXServer extends Observable
        implements Runnable, Observer {
    private static Log log = LogFactory.getLog(AMXServer.class);

    int port;
    BranchChannel clientSideChannel;
    Class clientSideChannelClass;
    Collection listeners;
    ThreadPool pool;
    ThreadPoolExecutor threadPoolExecutor;


    public static final int DEFAULT_MAX_THREADS = 100;
    String name;
    protected BranchServerSocketFactory socketFactory = null;
    public static final int CONNECT = 0;
    public static final int SIZEOF_CNT = 1;
    private int[] cnt;
    private boolean shutdown = false;
    private ServerSocketChannel serverSocketChannel;
    private Collection channels;
    private static int socketStatus = CMConstants.INTERNAL_SERVERSOCKET_INIT;
    private int threadPoolMaxSize;
    private float threadPoolIdleSizeThreshold = 100;
    private float threadPoolPendingSizeThreshold = 10;
    private SocketChannel socketChannel;
    private static BranchServerChannel channel;

    public static int getSocketStatus() {
        return socketStatus;
    }

    public static BranchServerChannel getChannel() {
        return channel;
    }


    public void setThreadPoolMaxSize(int threadPoolMaxSize) {
        this.threadPoolMaxSize = threadPoolMaxSize;
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

    public AMXServer(int port, BranchServerChannel clientSide, ThreadPool pool) {
        super();
        this.port = port;
        this.clientSideChannel = clientSide;
        this.clientSideChannelClass = clientSide.getClass();
        this.pool = (pool == null) ?
                new ThreadPool(1, DEFAULT_MAX_THREADS) : pool;

        listeners = new Vector();
        name = "";
        channels = new HashSet();
        cnt = new int[SIZEOF_CNT];
    }

    public AMXServer(int port, BranchServerChannel clientSide, ThreadPoolExecutor pool) {
        super();
        this.port = port;
        this.clientSideChannel = clientSide;
        this.clientSideChannelClass = clientSide.getClass();
        this.threadPoolExecutor = (pool == null) ?
                new ThreadPoolExecutor(50, 200, 100000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(1000)) : pool;

        listeners = new Vector();
        name = "";
        channels = new HashSet();
        cnt = new int[SIZEOF_CNT];

    }

    public void addBranchRequestListener(BranchRequestListener l) {
        listeners.add(l);
    }

    public void removeBranchRequestListener(BranchRequestListener l) {
        listeners.remove(l);
    }

    /**
     * Shutdown this server
     */
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

    static int messageCounter = 0;

    public static int getBranchSocketStatus() { // being 'static' is useful ?????
        if ((socketStatus % 2) == 0) {
            if (socketStatus < 7) {
                return CMConstants.EXTERNAL_SERVERSOCKET_INIT;
            } else {
                if (!channel.isConnected()) return CMConstants.EXTERNAL_SERVERSOCKET_NOT_CONNECTED_ERROR;
                if (socketStatus == CMConstants.INTERNAL_SERVERSOCKET_WAITING_FOR_REQUEST)
                    return CMConstants.EXTERNAL_SERVERSOCKET_WAITING_FOR_REQUEST;
                if (socketStatus == CMConstants.INTERNAL_SERVERSOCKET_WAITING_FOR_SOCKET_ESTABLISHMENT)
                    return CMConstants.EXTERNAL_SERVERSOCKET_WAITING_FOR_SOCKET_ESTABLISHMENT;
                return CMConstants.EXTERNAL_SERVERSOCKET_OK;
            }
        } else {
            if (socketStatus == CMConstants.INTERNAL_NULL_CHANNEL_ERROR)
                return CMConstants.EXTERNAL_NULL_CHANNEL_ERROR;


            if (socketStatus == CMConstants.INTERNAL_SERVERSOCKET_CREATION_ERROR)
                return CMConstants.EXTERNAL_SERVERSOCKET_CREATION_ERROR;

            if (socketStatus == CMConstants.INTERNAL_SERVERSOCKET_ACCEPTING_CONNECTION_ERROR)
                return CMConstants.EXTERNAL_SERVERSOCKET_ACCEPTING_CONNECTION_ERROR;

            if ((socketStatus == CMConstants.INTERNAL_SERVERSOCKET_RECEIVING_ERROR) || (socketStatus == CMConstants.INTERNAL_SERVERSOCKET_SENDING_ERROR))
                return CMConstants.EXTERNAL_SERVERSOCKET_SENDING_RECEIVING_ERROR;

            if (!channel.isConnected()) return CMConstants.EXTERNAL_SERVERSOCKET_NOT_CONNECTED_ERROR;

            return CMConstants.EXTERNAL_UKNOWN_ERROR;
        }
    }

    public void run() {

        while (!shutdown) {
            socketStatus = CMConstants.INTERNAL_SERVERSOCKET_READY;
            try {
                try {
                    if (socketFactory != null)
                        serverSocketChannel = socketFactory.createServerSocket(port);
                    else {
                        serverSocketChannel = ServerSocketChannel.open();
                        serverSocketChannel.socket().bind(new InetSocketAddress(port));
                    }
                    socketStatus = CMConstants.INTERNAL_SERVERSOCKET_CREATION_OK;
                } catch (IOException e) {
                    socketStatus = CMConstants.INTERNAL_SERVERSOCKET_CREATION_ERROR;
                    log.error("Error in creating socket - e :: " + e);
                    throw e;
                }

                while (!shutdown) {
                    try {
                        channel = (BranchServerChannel) clientSideChannelClass.newInstance();
                        if (channel == null) {
                            socketStatus = CMConstants.INTERNAL_NULL_CHANNEL_ERROR;
                            throw new Exception("channel is null!!!!");
                        }

                        if (clientSideChannel instanceof BranchBaseChannel) {
                            ((BranchBaseChannel) channel).setTimeout(
                                    ((BranchBaseChannel) clientSideChannel).getTimeout());
                        }

                        if (channel instanceof Observable)
                            ((Observable) channel).addObserver(this);
                        try {
                            socketStatus = CMConstants.INTERNAL_SERVERSOCKET_WAITING_FOR_SOCKET_ESTABLISHMENT;
                            channel.accept(serverSocketChannel);
                            serverSocketChannel.close();
                            socketStatus = CMConstants.INTERNAL_SERVERSOCKET_CONNECTION_OK;
                        } catch (IOException e) {
                            socketStatus = CMConstants.INTERNAL_SERVERSOCKET_ACCEPTING_CONNECTION_ERROR;
                            log.error("Error in connecting to socket - e :: " + e);
                            throw e;
                        }


                        if ((cnt[CONNECT]++) % 100 == 0)
                            purgeChannels();
                        channels.add(new WeakReference(channel));

                        for (; ; ) {

                            if (messageCounter++ % 200 == 0) {
                                log.fatal("ThreadPool idle count: " + getIdleCount());
                                log.fatal("ThreadPool available count: " + getActiveCount());
                            }

                            String messageString = null;
                            try {
                                socketStatus = CMConstants.INTERNAL_SERVERSOCKET_WAITING_FOR_REQUEST;
                                messageString = channel.receiveBranch();
                                socketStatus = CMConstants.INTERNAL_SERVERSOCKET_RECEIVING_OK;
                            } catch (SocketConnectionException e) {
                                socketStatus = CMConstants.INTERNAL_SERVERSOCKET_CONNECTION_BROKEN;
                                log.error("Error, connection was broken - e :: " + e);
                                throw e;
                            } catch (IOException e) {
                                socketStatus = CMConstants.INTERNAL_SERVERSOCKET_RECEIVING_ERROR;
                                log.error("Error in receiving message - e :: " + e);
                                throw e;
                            }

                            if (getIdleCount() == 0 && getActiveCount() >= getMaxPoolSize()) {

                                log.fatal("ThreadPool actives more than max size, pool.activeCount()= " + pool.activeCount() +
                                        ", pool.getAvailableCount()= " + pool.getAvailableCount() +
                                        " , pool.getIdleCount()= " + pool.getIdleCount() +
                                        " , pool.getJobCount()= " + pool.getJobCount() +
                                        " , getActiveCount()= " + getActiveCount() +
                                        " , MaxPoolSize = " + getMaxPoolSize() +
                                        " , pool.getPoolSize()= " + pool.getPoolSize() + " -- Request = " + messageString);

                                messageString = messageString.substring(0, 38) + ActionCode.TOO_MANY_RECORDS_REQUESTED;

                                try {
                                    channel.sendBranch(messageString);
                                    socketStatus = CMConstants.INTERNAL_SERVERSOCKET_SENDING_OK;
                                } catch (SocketConnectionException e) {
                                    socketStatus = CMConstants.INTERNAL_SERVERSOCKET_CONNECTION_BROKEN;
                                    log.error("Error, connection was broken - e :: " + e);
                                    throw e;

                                } catch (IOException e) {
                                    socketStatus = CMConstants.INTERNAL_SERVERSOCKET_SENDING_ERROR;
                                    log.error("Error in sending message - e :: " + e);
                                    throw e;

                                }

                                continue;
                            }
                            if (pool != null) {
                                pool.execute(new MessageSession(messageString, channel));
                            } else {
                                threadPoolExecutor.execute(new MessageSession(messageString, channel));
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
        cnt = new int[SIZEOF_CNT];
    }

    /**
     * @return number of connections accepted by this server
     */
    public int getConnectionCount() {
        return cnt[CONNECT];
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
//            return (pool.getPoolSize() - pool.getIdleCount());
        else
            return threadPoolExecutor.getActiveCount();
    }

    public int getPendingCount() {
        if (pool != null)
            return pool.getPendingCount();
        else
            return Integer.valueOf("" + (threadPoolExecutor.getLargestPoolSize())).intValue();
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
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
            while (iter.hasNext())
                if (((BranchRequestListener) iter.next()).process(channel, msg))
                    break;
        }

    }

    private synchronized ThreadPool getPool() {
        return pool;
    }
}
