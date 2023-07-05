package branch.dpi.atlas.service.cm.source.branch.socket;


public interface BranchClientChannel extends BranchChannel {
    /**
     * initialize a SparrowChannel
     *
     * @param host server TCP Address
     * @param port server port number
     */
    public void setHost(String host, int port);

    /**
     * @return hostname (may be null)
     */
    public String getHost();

    /**
     * @return port number (may be 0)
     */
    public int getPort();
}