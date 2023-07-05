package branch.dpi.atlas.service.cm.imf.manzume;

import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;

import java.util.Map;

public interface ManzumeToIMFFormater {
    public CMCommand format(ManzumeMessage manzumeMessage);
    public String createResponse(CMMessage msg, Map map) throws CMFault;
}
