package dpi.atlas.util.mq.mypool;

/**
 * Created by IntelliJ IDEA.
 * User: dp-admin
 * Date: Aug 29, 2009
 * Time: 10:27:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class MQPoolConfig {

    public static String LOCAL_TRANSPORT = "local";
    public static String REMOTE_TRANSPORT = "remote";

    public static int LOCAL_TRANSPORT_ID = 0;
    public static int REMOTE_TRANSPORT_ID = 1;

    private String hostName;
    private String queue_manager_name;
    private String reciveQueueName;
    private int numberOfConnections;
    private int numberOfSessionsPerConnection;
    private long timeout;
    private long timeToLive;
    private int priority;
    private boolean isSyncConnector;

    private String sendQueueName;
    private int hostPort;
    private String channel_name;
    private String MQusername;
    private String MQuserPass;
    private String transportType;
    final static boolean IS_SYNC = true;
    final static boolean IS_ASYNC = false;

    public MQPoolConfig(String hostName, String transportType, String MQuserPass, String MQusername, String channel_name, int hostPort, String sendQueueName, long timeout, int numberOfSessionsPerConnection, int numberOfConnections, String reciveQueueName, String queue_manager_name, boolean isSyncConnector) throws Exception {
        this.hostName = hostName;
        this.transportType = transportType;
        this.MQuserPass = MQuserPass;
        this.MQusername = MQusername;
        this.channel_name = channel_name;
        this.hostPort = hostPort;
        this.sendQueueName = sendQueueName;
        this.timeout = timeout;
        this.numberOfSessionsPerConnection = numberOfSessionsPerConnection;
        this.numberOfConnections = numberOfConnections;
        this.reciveQueueName = reciveQueueName;
        this.queue_manager_name = queue_manager_name;
        this.isSyncConnector = isSyncConnector;
        this.timeToLive = timeout * 2;
        if (isSyncConnector() && reciveQueueName == null)
            throw new Exception("Sync MQSessionPool requires receive queue to be defined.");
    }


    public MQPoolConfig(String queue_manager_name, String sendQueueName, String reciveQueueName, int poolSize, int priority, long timeout, boolean isSyncConnector) throws Exception {
        this.queue_manager_name = queue_manager_name;
        this.sendQueueName = sendQueueName;
        this.reciveQueueName = reciveQueueName;
        this.timeout = timeout;
        this.timeToLive = timeout * 2;
        this.priority = priority;
        this.isSyncConnector = isSyncConnector;
        this.timeToLive = timeout * 2;
        this.numberOfConnections = 1;
        this.numberOfSessionsPerConnection = poolSize;
        if (isSyncConnector() && reciveQueueName == null)
            throw new Exception("Sync MQSessionPool requires receive queue to be defined.");
    }

    public MQPoolConfig(String queue_manager_name, String sendQueueName, int poolSize, int priority, long timeout, boolean isSyncConnector) throws Exception {
        this.queue_manager_name = queue_manager_name;
        this.sendQueueName = sendQueueName;
        this.timeout = timeout;
        this.timeToLive = timeout * 2;
        this.priority = priority;
        this.isSyncConnector = isSyncConnector;
        this.timeToLive = timeout * 2;
        this.numberOfConnections = 1;
        this.numberOfSessionsPerConnection = poolSize;
        if (isSyncConnector() && reciveQueueName == null)
            throw new Exception("Sync MQSessionPool requires receive queue to be defined.");
    }


    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getNumberOfSessionsPerConnection() {
        return numberOfSessionsPerConnection;
    }

    public void setNumberOfSessionsPerConnection(int numberOfSessionsPerConnection) {
        this.numberOfSessionsPerConnection = numberOfSessionsPerConnection;
    }

    public int getNumberOfConnections() {
        return numberOfConnections;
    }

    public void setNumberOfConnections(int numberOfConnections) {
        this.numberOfConnections = numberOfConnections;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getQueue_manager_name() {
        return queue_manager_name;
    }

    public void setQueue_manager_name(String queue_manager_name) {
        this.queue_manager_name = queue_manager_name;
    }


    public String getReciveQueueName() {
        return reciveQueueName;
    }

    public void setReciveQueueName(String reciveQueueName) {
        this.reciveQueueName = reciveQueueName;
    }

    public String getSendQueueName() {
        return sendQueueName;
    }

    public void setSendQueueName(String sendQueueName) {
        this.sendQueueName = sendQueueName;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getMQusername() {
        return MQusername;
    }

    public void setMQusername(String MQusername) {
        this.MQusername = MQusername;
    }

    public String getMQuserPass() {
        return MQuserPass;
    }

    public void setMQuserPass(String MQuserPass) {
        this.MQuserPass = MQuserPass;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    public boolean isSyncConnector() {
        return isSyncConnector;
    }

    public void setSyncConnector(boolean syncConnector) {
        isSyncConnector = syncConnector;
    }
}
