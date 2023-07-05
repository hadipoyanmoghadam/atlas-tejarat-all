package branch.dpi.atlas.service.cm.source.branch;

import dpi.atlas.service.cm.handler.CMHandler;

public interface BranchRequestListener {
    public boolean process(branch.dpi.atlas.service.cm.source.branch.socket.BranchSource source, String m);

    public void setChainHandler(CMHandler cmHandler);

}
