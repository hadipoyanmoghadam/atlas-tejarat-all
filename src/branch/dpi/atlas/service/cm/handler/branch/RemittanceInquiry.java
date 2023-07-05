package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;


import java.util.Map;
/**
 * User: F.Heidari
 * Date: July 27 2019
 * Time: 8:35 AM
 */
public class RemittanceInquiry extends CMHandlerBase {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        log.debug("inside RemittanceInquiry.doProcess()");
        BranchMessage branchMsg = null;
        String pin = "";
        String SerialNumber="";

        try {

            branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            pin = branchMsg.getPin();
            SerialNumber=branchMsg.getRemittance_request_no();
            if (pin.equalsIgnoreCase(Constants.PIN_REMITTANCE_INQUIRY)) {
                String[] remittance = ChannelFacadeNew.remittanceInquiry(branchMsg.getRemittance_request_no(),
                        branchMsg.getRemittanceDate(), branchMsg.getNationalCode(), branchMsg.getExt_IdNumber());
                if (remittance.equals("") || remittance == null) {
                    log.error("Exception::" + pin);
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
                } else {
                    branchMsg.setRemittance_request_no(remittance[0]);
                    branchMsg.setRemittanceDate(remittance[1]);
                    branchMsg.setNationalCode(remittance[2]);
                    String ext_IdNumber=remittance[3];
                    if(ext_IdNumber==null || ext_IdNumber.trim().equalsIgnoreCase(""))
                        ext_IdNumber="";
                    branchMsg.setExt_IdNumber(ext_IdNumber);
                    branchMsg.setBirthDate_remittance(remittance[4]);
                    branchMsg.setTelNumber_remittance(remittance[5]);
                    branchMsg.setExpirDate_remittance(remittance[6]);
                    branchMsg.setRemittanceAmount(remittance[7]);
                    branchMsg.setRemittanceFeeAmount(remittance[8]);
                    branchMsg.setRemittanceStatus(remittance[9]);
                    branchMsg.setRemittanceMessage_Sequence(remittance[10]);
                }
            }
        }catch(NotFoundException e){
            log.error("Exception::"+pin+"Can Not Find Any remittance With Serialnumber:"+SerialNumber);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DOCUMENT_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DOCUMENT_NOT_FOUND));
        }
        catch (Exception e) {
            log.error("Exception::" + pin);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}
