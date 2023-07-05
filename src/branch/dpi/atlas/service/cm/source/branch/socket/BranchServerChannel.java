package branch.dpi.atlas.service.cm.source.branch.socket;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

public interface BranchServerChannel extends BranchChannel {
    public void accept(ServerSocketChannel s) throws IOException;
}
