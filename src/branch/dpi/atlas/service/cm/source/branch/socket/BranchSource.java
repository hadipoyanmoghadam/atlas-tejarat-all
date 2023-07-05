package branch.dpi.atlas.service.cm.source.branch.socket;

import java.io.IOException;

public interface BranchSource {
    public void sendBranch(String m) throws IOException;

}
