package branch.dpi.atlas.service.cm.source.branch.socket;

import branch.dpi.atlas.service.cm.handler.exception.SocketConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Observable;

/**
 * User: R.Nasiri
 * Date: Apr 11, 2013
 * Time: 1:04:03 PM
 */

public class BranchBaseChannel extends Observable
        implements BranchFilteredChannel, BranchClientChannel, BranchServerChannel {
    private static Log log = LogFactory.getLog(BranchBaseChannel.class);

    private SocketChannel socketChannel;
    private String host;
    private int port, timeout;
    protected boolean usable;
    protected ServerSocketChannel serverSocketChannel = null;

    protected int[] cnt;

    public BranchBaseChannel() {
        super();
        cnt = new int[SIZEOF_CNT];
        setHost(null, 0);
    }

    public BranchBaseChannel(String host, int port) {
        this();
        setHost(host, port);
    }

    public BranchBaseChannel(ServerSocketChannel serverSocket) {
        this();
        setServerSocket(serverSocket);
    }

    public void setHost(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    /**
     * Associates this ISOChannel with a server socket
     */
    public void setServerSocket(ServerSocketChannel sock) {
        setHost(null, 0);
        this.serverSocketChannel = sock;
    }

    public void resetCounters() {
        for (int i = 0; i < SIZEOF_CNT; i++)
            cnt[i] = 0;
    }

    public int[] getCounters() {
        return cnt;
    }

    public boolean isConnected() {
        return socketChannel != null;
//        return socket != null && usable;
    }

    protected void connect(SocketChannel socket) throws IOException {
        this.socketChannel = socket;
//        setTimeout(10000);            // Habib added
        applyTimeout();
        socket.socket().setKeepAlive(true);
        usable = true;
        cnt[CONNECT]++;
        setChanged();
        notifyObservers();
    }

    /**
     * factory method pattern (as suggested by Vincent.Greene@amo.com)
     *
     * @throws java.io.IOException Use Socket factory if exists. If it is missing create a normal socket
     */
    protected SocketChannel newSocket() throws IOException {
        return SocketChannel.open(new InetSocketAddress(host, port));
    }

    /**
     * @return current socket
     */

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    /**
     * @return current serverSocket
     */

    public ServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }


    /**
     * sets socket timeout (as suggested by
     * Leonard Thomas <leonard@rhinosystemsinc.com>)
     *
     * @param timeout in milliseconds
     * @throws java.net.SocketException
     */
    public void setTimeout(int timeout) throws SocketException {
        this.timeout = timeout;
        applyTimeout();
    }

    public int getTimeout() {
        return timeout;
    }

    protected void applyTimeout() throws SocketException {
        if (timeout >= 0 && socketChannel != null)
            socketChannel.socket().setSoTimeout(timeout);
    }

    /**
     * Connects client ISOChannel to server
     *
     * @throws java.io.IOException
     */
    public void connect() throws IOException {
        try {
            if (serverSocketChannel != null) {
                accept(serverSocketChannel);
            } else {
                connect(newSocket());
            }
            applyTimeout();
        } catch (ConnectException e) {
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Accepts connection
     *
     * @throws java.io.IOException
     */
    public void accept(ServerSocketChannel s) throws IOException {
        connect(s.accept());
    }

    /**
     * @param b - new Usable state (used by ISOMUX internals to
     *          flag as unusable in order to force a reconnection)
     */
    public void setUsable(boolean b) {
        usable = b;
    }

    protected byte[] streamReceive() throws IOException {
        return new byte[0];
    }

    public void sendBranch(String msg) throws IOException {
        if (!isConnected())
            throw new SocketConnectionException("unconnected SparrowChannel");
        msg = (char) 2 + msg + (char) 3;
        byte[] b = msg.getBytes("CP1256");
            ByteBuffer buffer = ByteBuffer.wrap(b);
            while (buffer.hasRemaining())
                socketChannel.write(buffer);

        cnt[TX]++;
        setChanged();
        notifyObservers(msg);
    }

    public String receiveBranch() throws IOException {
        Charset charset = Charset.forName("CP1256");
        ByteBuffer bb = ByteBuffer.allocate(1);
        bb.clear();
        String msg = "";
        String temp = "";

        if (!isConnected())
            throw new SocketConnectionException("unconnected Channel");
            if (socketChannel.read(bb) == -1)
                throw new IOException();
            else bb.clear();
            for (; ; ) {

                socketChannel.read(bb);
                bb.flip();

                temp = "" + (charset.decode(bb));
                if (temp.equalsIgnoreCase("\u0003"))
                    break;
                bb.position(0);
                msg += (charset.decode(bb));
                bb.clear();
            }

        return msg;
    }

    /**
     * disconnects the TCP/IP session. The instance is ready for
     * a reconnection. There is no need to create a new ISOChannel<br>
     *
     * @throws java.io.IOException
     */
    public void disconnect() throws IOException {
        try {
            usable = false;
            setChanged();
            notifyObservers();
            if (socketChannel != null) {
                socketChannel.close();
            }
        } catch (IOException e) {
            throw e;
        }
        socketChannel = null;
    }

    /**
     * Issues a disconnect followed by a connect
     *
     * @throws java.io.IOException
     */
    public void reconnect() throws IOException {
        disconnect();
        connect();
    }
}

