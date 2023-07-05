package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.customer.CUSTOMERType;
import branch.dpi.atlas.service.cm.handler.cms.jointCard.JointCardReq;
import branch.dpi.atlas.service.cm.handler.cms.touristCard.TouristCardReq;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: oCTOBER 31, 2018
 * Time: 14:04:03
 */
public class UpdateCardInfo extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        String cardNo;
        String nameLatin = "";

        try {

            String requestType = msg.getAttributeAsString(CMMessage.REQUEST_TYPE);

            if (requestType.equals(Constants.TOURISTCARD_ELEMENT)) {
                TouristCardReq customerMsg = (TouristCardReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                cardNo = customerMsg.getCardno();
                nameLatin = customerMsg.getNamefamilylatin().trim();
            } else if (requestType.equals(Constants.JOINTACCOUNT_ELEMENT)) {
                JointCardReq customerMsg = (JointCardReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                cardNo = customerMsg.getCardno();
                nameLatin = customerMsg.getNamefamilylatin().trim();
            } else {
                CUSTOMERType customerMsg = (CUSTOMERType) msg.getAttribute(CMMessage.COMMAND_OBJ);
                cardNo = customerMsg.getCardno();
                nameLatin = customerMsg.getNamefamilylatin().trim();
            }

            String customerId = ChannelFacadeNew.getCustomerIdByCardNo(cardNo);
            ChannelFacadeNew.UpdateLatinName4Card(customerId, nameLatin);

        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_CARD_NUMBER));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside UpdateCardInfo.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

