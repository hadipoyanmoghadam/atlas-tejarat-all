package branch.dpi.atlas.service.cm.imf.branch;
import dpi.atlas.service.cm.ib.format.CMCommand;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;

import java.util.Map;

/**
 * User: H.Ghayoumi
 * Date: Apr 17, 2013
 * Time: 10:00:33 AM
 */
public interface BranchToIMFFormater {
    public CMCommand format(BranchMessage branchMsg);
    public String createResponse(CMMessage msg, Map map) throws CMFault;
    
}
