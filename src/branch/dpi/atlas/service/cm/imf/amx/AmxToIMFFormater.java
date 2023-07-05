package branch.dpi.atlas.service.cm.imf.amx;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;

import java.util.Map;


/**
 * User: R.Nasiri
 * Date: Dec 5, 2018
 * Time: 03:46 PM
 */
public interface AmxToIMFFormater {
    public CMCommand format(AmxMessage amxMessage);
    public String createResponse(CMMessage msg, Map map) throws CMFault;
    
}
