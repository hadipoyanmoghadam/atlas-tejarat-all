package branch.dpi.atlas.service.cm.source.branch.socket;

import java.io.IOException;

public interface BranchChannel extends BranchSource {
    public static final int CONNECT = 0;
    public static final int TX = 1;
    public static final int RX = 2;
    public static final int SIZEOF_CNT = 3;

    public void connect() throws IOException;

    public void disconnect() throws IOException;

    public void reconnect() throws IOException;

    public boolean isConnected();

    public String receiveBranch() throws IOException;

    public void sendBranch(String m) throws IOException;

    public void setUsable(boolean b);

}
