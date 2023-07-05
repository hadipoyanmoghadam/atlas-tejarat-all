package branch.dpi.atlas.service.cm.handler.credits;

import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.iso.ISOUtil;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Jan 5 2016
 * Time: 02:37 PM
 */
public class CheckAccountNature extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        log.debug("inside CheckAccountNature.doProcess(CMMessage msg, Map holder)");
        CreditsMessage creditsMessage;
        String pin = "";
        try {

            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

            creditsMessage = (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            pin = creditsMessage.getPin();

            if (pin.equalsIgnoreCase(Constants.PIN_CREDITS_DEPOSIT)) {

                String id1 = creditsMessage.getId1().trim();

                if (msg.hasAttribute(Constants.DEST_ACCOUNT_NATURE)) {

                    String destAccountNo = command.getParam(Fields.DEST_ACCOUNT);
                    if (destAccountNo.length() < 13)
                        destAccountNo = ISOUtil.zeropad(destAccountNo, 13);

                    int destAccountNature = Integer.parseInt(msg.getAttributeAsString(Constants.DEST_ACCOUNT_NATURE));
                    if (destAccountNature == Constants.ACCOUNT_NATURE_DEPOSIT_ON_TIME) {
                        if (id1==null || id1.trim().equals("") || ISOUtil.isZero(id1.trim())) {
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYMENT_CODE_IS_INVALID);
                            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.PAYMENT_CODE_IS_INVALID));
                        }
                        if (ChannelFacadeNew.checkId(destAccountNo, id1)) {
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.BILL_PAYMENT_BEFORE_PAID);
                            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.BILL_PAYMENT_BEFORE_PAID));
                        }
                    }
                }
            }
        } catch (CMFault fe) {
            throw fe;
        } catch (Exception e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}