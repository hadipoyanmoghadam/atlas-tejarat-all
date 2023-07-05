package branch.dpi.atlas.service.cm.source.branch.socket;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

public interface BranchServerSocketFactory {
    public ServerSocketChannel createServerSocket(int port) throws IOException;
}
