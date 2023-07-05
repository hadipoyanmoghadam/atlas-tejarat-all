package branch.dpi.atlas.service.cm.imf.sms;

import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;

import java.util.Map;

/**
 * User: F.Heydari
 * Date: NOV ,12, 2019
 * Time: 1:50:33 PM
 */
public interface SMSToIMFFormater {
    public CMCommand format(SMSMessage smsMsg);

    public String createResponse(CMMessage msg, Map map) throws CMFault;

}


