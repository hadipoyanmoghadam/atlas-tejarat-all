package branch.dpi.atlas.service.cm.handler.manzume;

import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.iso.ISOUtil;


import java.sql.SQLException;
import java.util.Map;

public class FindCustomerFromCif extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        ManzumeMessage manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        log.debug("Inside FindCustomerFromCif:process()");

        try {
            String accountNo=manzumeMessage.getAccountNo().trim();
            accountNo= ISOUtil.padleft(accountNo,13,'0');

            String[] accInfo =  ChannelFacadeNew.getAccountInfoFromCif(accountNo);

            manzumeMessage.setCustomerName( null != accInfo[0] ? accInfo[0] : "");
            manzumeMessage.setCustomerId( null != accInfo[1] ? accInfo[1] : "");
            if(accInfo[2] !="" && accInfo[2] !="9999999999" && accInfo[2] !=null)
            {
                manzumeMessage.setIdentificationCode(null != accInfo[2] ? accInfo[2] : "");
            }
            else
            {
                manzumeMessage.setIdentificationCode(null != accInfo[3] ? accInfo[3] : "" );
            }

            manzumeMessage.setMobileNo(null != accInfo[4] ? accInfo[4] : "");
            manzumeMessage.setPostalCode(null != accInfo[5] ? accInfo[5] : "");
            manzumeMessage.setAddress(null != accInfo[6] ? accInfo[6] : "");

        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));

        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        }catch (ModelException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        }
        catch (Exception e) {
            log.error("ERROR :::Inside FindCustomerFromCif.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}
