package branch.dpi.atlas.service.cm.imf.tourist;

import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;

import java.util.Map;


/**
 * User: R.Nasiri
 * Date: April 24, 2017
 * Time: 08:52 AM
 */
public interface TouristToIMFFormater {
    public CMCommand format(TouristMessage touristMessage);
    public String createResponse(CMMessage msg, Map map) throws CMFault;
    
}
