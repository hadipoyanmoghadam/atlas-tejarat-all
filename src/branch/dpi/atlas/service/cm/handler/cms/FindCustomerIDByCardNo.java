package branch.dpi.atlas.service.cm.handler.cms;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: July 19, 2017
 * Time: 9:45:27 AM
 */
public class FindCustomerIDByCardNo extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
            String cardNo = msg.getAttributeAsString(Fields.PAN);
            Account account = (Account) holder.get(Constants.CUSTOMER_ACCOUNT);

            Map<Long, String> map = ChannelFacadeNew.fillCMParamMap("TouristAccountNumber");
            String accountNo = account.getAccountNo();
            String cardType = Constants.NORMAL_CARD_TYPE;
            if (msg.getAttributeAsString(Fields.CARD_TYPE) != null)
                cardType = msg.getAttributeAsString(Fields.CARD_TYPE).trim();

            if (account.getAccountTitle().trim().equalsIgnoreCase("3") || (cardType.equalsIgnoreCase(Constants.GROUP_CARD_TYPE_STR) && !map.containsValue(accountNo))) {

                String customerId = ChannelFacadeNew.getCustomerIdByCardNo(cardNo);

                if (customerId != null && !customerId.equals("")) {
                    holder.put(Fields.BRANCH_CUSTOMER_ID, customerId);
                    String subTitle = ChannelFacadeNew.getSubtitleByCustomerId(accountNo,customerId);
                    if(subTitle!=null && !subTitle.trim().equalsIgnoreCase("") &&
                            !subTitle.trim().equalsIgnoreCase("0")) {
                        account.setAccountTitle(subTitle.trim());
                        holder.put(Constants.CUSTOMER_ACCOUNT, account);
                    }
                } else {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_CARD_NUMBER));
                }
            }

        } catch (CMFault e) {
            throw e;
        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_CARD_NUMBER));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside FindCustomerIDByCardNo.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}