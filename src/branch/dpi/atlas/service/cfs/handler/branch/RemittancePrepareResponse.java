package branch.dpi.atlas.service.cfs.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.lang.SystemUtils;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;
import sun.plugin2.util.SystemUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Aug 10, 2019
 * Time: 3:17 PM
 */
public class RemittancePrepareResponse extends CFSHandlerBase implements Configurable {

    private ArrayList actionCodeList;
    private ArrayList checkPin;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String actionCode = "";
        String sendStr = "";
        String sequenceNo = "";
        String date = "";        // yyyymmdd
        String time = "";        // hhmm
        String amount = "";
        String channelCode = "";
        String branchCode = "";
        String transactionType = "";
        String nationalCode = "";
        String extrnalIdNumber = "";
        String request_date = "";
        String isReversed = "";
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);


        try {
            if (log.isInfoEnabled()) log.info(description);
            actionCode = msg.getAttributeAsString(CFSConstants.ACTION_CODE);
            if (!actionCodeList.contains(actionCode) || !checkPin.contains(command.getCommandName()))
                return;

            isReversed = (msg.hasAttribute(Constants.ISREVERSED)) ? msg.getAttributeAsString(Constants.ISREVERSED) : Constants.FALSE;
            if (command.getCommandName().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_REMITTANCE))
                transactionType = "0";

            sequenceNo = msg.getAttributeAsString(Fields.REQUEST_NO);
            if (sequenceNo == null || sequenceNo.trim().equals("")) sequenceNo = "";
            else sequenceNo = sequenceNo.trim();

            nationalCode = msg.getAttributeAsString(Fields.NATIONAL_CODE);
            if (nationalCode == null || nationalCode.trim().equals("")) nationalCode = "";
            else nationalCode = nationalCode.trim();

            extrnalIdNumber = msg.getAttributeAsString(Fields.EXTERNAL_ID_NUMBER);
            if (extrnalIdNumber == null || extrnalIdNumber.trim().equals("")) extrnalIdNumber = "";
            else extrnalIdNumber = extrnalIdNumber.trim();

            request_date = msg.getAttributeAsString(Fields.REMITTANCE_DATE);
            if (request_date == null || request_date.trim().equals("")) request_date = "";
            else request_date = request_date.trim();

            date = msg.getAttribute(Fields.DATE).toString();
            if(date!=null && date.trim().length()==6)
                date = DateUtil.getSystemDate().substring(0,2) + msg.getAttribute(Fields.DATE).toString();

            time = msg.getAttribute(Fields.TIME).toString();

            channelCode = Constants.BRANCH_CHANNEL_CODE;

            branchCode = msg.getAttribute(Fields.BRANCH_CODE).toString();

            amount = msg.getAttributeAsString(Fields.REMITTANCE_AMOUNT);

            sendStr = ISOUtil.padleft(isReversed,1,' ')+
                      ISOUtil.padleft(transactionType,1,' ')+
                      ISOUtil.padleft(sequenceNo,10,'0')+
                      ISOUtil.padleft(nationalCode,11,' ')+
                      ISOUtil.padleft(extrnalIdNumber,15,' ')+
                      ISOUtil.padleft(amount,18,'0')+
                      ISOUtil.padleft(request_date,8,'0')+
                      ISOUtil.padleft(time,6,'0')+
                      ISOUtil.padleft(date,8,'0')+
                      ISOUtil.padleft(channelCode,2,'0')+
                      ISOUtil.padleft(branchCode,6,'0');

            holder.put(CFSConstants.NOTIFICATION_STR, sendStr);

        } catch (Exception e) {
            e.printStackTrace();
            throw new CFSFault(CFSFault.FLT_REMITTANCE_FORMAT, new Exception(ActionCode.FORMAT_ERROR));
        }


    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        String actionCodes = cfg.get(Fields.ACTIONCODE_TO_BE_CONSIDERED);
        actionCodeList = CMUtil.tokenizString(actionCodes, ",");    //for action code = 0000 , sms have sent
        String checkString = cfg.get(Fields.CHECKED);    //for some command like Deposit , description code have sent
        checkPin = CMUtil.tokenizString(checkString, ",");
    }
}

