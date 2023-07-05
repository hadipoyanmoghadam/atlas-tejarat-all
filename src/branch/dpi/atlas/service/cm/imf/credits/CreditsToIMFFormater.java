package branch.dpi.atlas.service.cm.imf.credits;

import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;

import java.util.Map;


/**
 * User: R.Nasiri
 * Date: Sep 4, 2017
 * Time: 02:13 AM
 */
public interface CreditsToIMFFormater {
    public CMCommand format(CreditsMessage creditsMessage);
    public String createResponse(CMMessage msg, Map map) throws CMFault;
    
}
