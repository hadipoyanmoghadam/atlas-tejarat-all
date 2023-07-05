package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import java.sql.SQLException;
import java.util.Map;

public class CalculateWageAmount extends CFSHandlerBase implements Configurable {

    private String amountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        try {
            long amount = Long.parseLong((String) msg.getAttribute(amountField));
            msg.setAttribute(Fields.ORIG_AMOUNT,msg.getAttribute(amountField));
            String groupType = msg.getAttributeAsString(Fields.GROUP_TYPE);
            String groupNo = msg.getAttributeAsString(Fields.GROUP_NO);
            int groupCount = 0;
            if (groupNo != null && !groupNo.trim().equalsIgnoreCase(""))
                groupCount = Integer.parseInt(groupNo.trim());
            String filler = (String) msg.getAttributeAsString(Fields.FILLER);
            String messageType = msg.getAttributeAsString(Fields.MESSAGE_TYPE);

            Map<Long, String> map = CFSFacadeNew.fillCMParamMap(messageType);
            if (map.size() == 0) {
                throw new NotFoundException("");
            }
            if (messageType.equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE)) {
                if (map.size() != 3) {
                    throw new NotFoundException("");
                }
                //ID=0 is percent of the amount
                double percent = Double.parseDouble(map.get(0L));
                //ID=1 is maxWage
                long maxWage = Long.parseLong(map.get(1L));
                //ID=2 is description
                String description = map.get(2L);
                if (description != null)
                    description = description.trim();
                else
                    description = "";
                msg.setAttribute(Fields.DOCUMENT_DESCRIPTION, description);

                //calculate wage
                amount = (long) (Math.floor((amount * percent / 100) / 10) * 10);
                if (amount > maxWage)
                    amount = maxWage;
                msg.setAttribute(amountField, String.valueOf(amount));
            } else { //ACH Wage
                if (map.size() != 5) {
                    throw new NotFoundException("");
                }
                //ID=0 is percent of the amount
                double percent = Double.parseDouble(map.get(0L));
                //ID=1 is minWage
                long minWage = Long.parseLong(map.get(1L));
                //ID=2 is maxWage
                long maxWage = Long.parseLong(map.get(2L));
                //ID=3 is Group Ach Wage
                long GroupAchWage = Long.parseLong(map.get(3L));
                //ID=4 is description
                String description = map.get(4L);
                if (description != null)
                    description = description.trim();
                else
                    description = "";
                msg.setAttribute(Fields.DOCUMENT_DESCRIPTION, description);

                if (groupType.equalsIgnoreCase(Fields.SINGLE_ACH) ||
                        (groupType.equalsIgnoreCase(Fields.GROUP_ACH) && groupCount <= 10)) {
                    //calculate wage for single Ach
                    amount = (long) (Math.floor((amount * percent / 100) / 10) * 10);
                    if (amount > maxWage)
                        amount = maxWage;
                    else if (amount < minWage)
                        amount = minWage;
                    msg.setAttribute(amountField, String.valueOf(amount));
                } else {
                    //Group Paya and transaction>10
                    amount = groupCount * GroupAchWage;
                    msg.setAttribute(amountField, String.valueOf(amount));
                }
            }

        } catch (SQLException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        } catch (NotFoundException e1) {
            throw new CFSFault(CFSFault.FLT_WAGE_INFORMATION_IS_NOT_DEFINED, ActionCode.WAGE_INFORMATION_IS_NOT_DEFINED);
        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        amountField = cfg.get(CFSConstants.AMOUNT_FIELD);
        if ((amountField == null) || (amountField.trim().equals(""))) {
            log.fatal("Amount Field is not Specified");
            throw new ConfigurationException("Amount Field is not Specified");
        }
    }
}
