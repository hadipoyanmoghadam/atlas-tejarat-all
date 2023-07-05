package branch.dpi.atlas.service.cm.handler;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.ServiceHandler;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.seq.SequenceGenerator;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Sep 20, 2006
 * Time: 1:41:45 PM
 * To change this template use File | Settings | File Templates.
 */

public class GetMessageFields extends ServiceHandler {
    public void _process(CMMessage msg, Map holder) throws CMFault {
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        String message = command.toString();
        msg.setAttribute(Fields.MSG_STR, message);

        if (command.getCommandName() != null)
            msg.setAttribute(Fields.COMMAND, command.getCommandName());
        if (command.getHeaderParam(Fields.CLIENT_ID) != null)
            msg.setAttribute(Fields.CLIENT_ID, command.getHeaderParam(Fields.CLIENT_ID));


        if (command.getHeaderParam(Fields.SERVICE_TYPE) != null)
            msg.setAttribute(Fields.SERVICE_TYPE, command.getHeaderParam(Fields.SERVICE_TYPE));
        if (command.getHeaderParam(Fields.SERVICE_PWD) != null)
            msg.setAttribute(Fields.SERVICE_PWD, command.getHeaderParam(Fields.SERVICE_PWD));
        if (command.getHeaderParam(Fields.SERVICE_ID) != null)
            msg.setAttribute(Fields.SERVICE_ID, command.getHeaderParam(Fields.SERVICE_ID));


        if (command.getHeaderParam(Fields.MESSAGE_ID) != null)
            msg.setAttribute(Fields.MESSAGE_ID, command.getHeaderParam(Fields.MESSAGE_ID));
        if (command.getHeaderParam(Fields.CUSTOMER_PIN) != null && !command.getHeaderParam(Fields.CUSTOMER_PIN).trim().equals(""))
            msg.setAttribute(Fields.CUSTOMER_PIN, command.getHeaderParam(Fields.CUSTOMER_PIN));
        else if (command.getParam(Fields.CUSTOMER_PIN) != null)
            msg.setAttribute(Fields.CUSTOMER_PIN, command.getParam(Fields.CUSTOMER_PIN));

        // The following field is null for IVR, but it will be set in CustomerFinderByAccount Handler
        if (command.getHeaderParam(Fields.CUSTOMER_ID) != null)
            msg.setAttribute(Fields.CUSTOMER_ID, command.getHeaderParam(Fields.CUSTOMER_ID));


        if (command.getParam(Fields.OLD_PIN) != null)
            msg.setAttribute(Fields.OLD_PIN, command.getParam(Fields.OLD_PIN));
        if (command.getParam(Fields.NEW_PIN) != null)
            msg.setAttribute(Fields.NEW_PIN, command.getParam(Fields.NEW_PIN));
        if (command.getParam(Fields.FROM_DATE) != null)
            msg.setAttribute(Fields.FROM_DATE, command.getParam(Fields.FROM_DATE));
        if (command.getParam(Fields.TO_DATE) != null)
            msg.setAttribute(Fields.TO_DATE, command.getParam(Fields.TO_DATE));
        if (command.getParam(Fields.DAY_COUNT) != null)
            msg.setAttribute(Fields.DAY_COUNT, command.getParam(Fields.DAY_COUNT));
        if (command.getParam(Fields.ORGANIZATION_BRANCH_CODE) != null)
            msg.setAttribute(Fields.ORGANIZATION_BRANCH_CODE, command.getParam(Fields.ORGANIZATION_BRANCH_CODE));
        if (command.getParam(Fields.ORGANIZATION_CODE) != null)
            msg.setAttribute(Fields.ORGANIZATION_CODE, command.getParam(Fields.ORGANIZATION_CODE));
        if (command.getParam(Fields.ORGANIZATION_CITY_CODE) != null)
            msg.setAttribute(Fields.ORGANIZATION_CITY_CODE, command.getParam(Fields.ORGANIZATION_CITY_CODE));


        if (command.getParam(Fields.BILL_CODE) != null)
            msg.setAttribute(Fields.BILL_CODE, command.getParam(Fields.BILL_CODE));
        if (command.getParam(Fields.BILL_NUMBER) != null)
            msg.setAttribute(Fields.BILL_NUMBER, command.getParam(Fields.BILL_NUMBER));
        if (command.getParam(Fields.BILL_AMOUNT) != null)
            msg.setAttribute(Fields.BILL_AMOUNT, command.getParam(Fields.BILL_AMOUNT));
        if (command.getParam(Fields.BILL_CURRENCY) != null)
            msg.setAttribute(Fields.BILL_CURRENCY, command.getParam(Fields.BILL_CURRENCY));
        if (command.getParam(Fields.CHECK_DIGIT) != null)
            msg.setAttribute(Fields.CHECK_DIGIT, command.getParam(Fields.CHECK_DIGIT));
        if (command.getParam(Fields.DESTINATION_TYPE) != null)
            msg.setAttribute(Fields.DESTINATION_TYPE, command.getParam(Fields.DESTINATION_TYPE));
        if (command.getParam(Fields.DESTINATION_ADDRESS) != null)
            msg.setAttribute(Fields.DESTINATION_ADDRESS, command.getParam(Fields.DESTINATION_ADDRESS));
        if (command.getParam(Fields.ACCOUNT_NO) != null)
            msg.setAttribute(Fields.ACCOUNT_NO, command.getParam(Fields.ACCOUNT_NO));
        if (command.getParam(Fields.SOURCE_ACCOUNT_NO) != null)
            msg.setAttribute(Fields.SOURCE_ACCOUNT_NO, command.getParam(Fields.SOURCE_ACCOUNT_NO));
        if (command.getParam(Fields.DEST_ACCOUNT_NO) != null)
            msg.setAttribute(Fields.DEST_ACCOUNT_NO, command.getParam(Fields.DEST_ACCOUNT_NO));
        if (command.getParam(Fields.TOTAL_DEST_ACCOUNT) != null)
            msg.setAttribute(Fields.TOTAL_DEST_ACCOUNT, command.getParam(Fields.TOTAL_DEST_ACCOUNT));
        if (command.getParam(Fields.ACCOUNT_NICKNAME) != null)
            msg.setAttribute(Fields.ACCOUNT_NICKNAME, command.getParam(Fields.ACCOUNT_NICKNAME));
        if (command.getParam(Fields.HELP_TOPIC) != null)
            msg.setAttribute(Fields.HELP_TOPIC, command.getParam(Fields.HELP_TOPIC));

        if (command.getParam(Fields.DUE_DATE) != null)
            msg.setAttribute(Fields.DUE_DATE, command.getParam(Fields.DUE_DATE));

        if (command.getParam(Fields.STYLE_ID) != null)
            msg.setAttribute(Fields.STYLE_ID, command.getParam(Fields.STYLE_ID));

        if (command.getParam(Fields.COUNTRY_CODE) != null)
            msg.setAttribute(Fields.COUNTRY_CODE, command.getParam(Fields.COUNTRY_CODE));

        if (command.getParam(Fields.AMOUNT) != null)
            msg.setAttribute(Fields.AMOUNT, command.getParam(Fields.AMOUNT));

        if (command.getHeaderParam(Fields.SESSION_ID) != null)
            msg.setAttribute(Fields.SESSION_ID, command.getHeaderParam(Fields.SESSION_ID));
        else {
            SequenceGenerator sg = SequenceGenerator.getInstance();
            long sequenceNumber = sg.getNextNumberInSequence("tbMsgLog");

            try {
                String sessionId = ISOUtil.zeropad(String.valueOf(sequenceNumber), 12);
                msg.setAttribute(Fields.SESSION_ID, sessionId);
                command.addHeaderParam(Fields.SESSION_ID, sessionId);
            } catch (ISOException e) {
                throw new CMFault(CMFault.FAULT_EXTERNAL);
            }
        }

        if (command.getParam(Fields.REFRENCE_NUMBER) != null)
            msg.setAttribute(Fields.REFRENCE_NUMBER, command.getParam(Fields.REFRENCE_NUMBER));

        //TODO: maybe it needs change for reference_no = 3, 4, ... for reversals, etc.
        msg.setAttribute(Fields.MESSAGE_ID, Constants.REQUEST);
        command.addHeaderParam(Fields.MESSAGE_ID, Constants.REQUEST);
        msg.setAttribute(CMMessage.REQUEST, command);

        if (command.getParam(Fields.DEBIT_CREDIT) != null)
            msg.setAttribute(Fields.DEBIT_CREDIT, command.getParam(Fields.DEBIT_CREDIT));
        if (command.getParam(Fields.Effective_Date) != null)
            msg.setAttribute(Fields.Effective_Date, command.getParam(Fields.Effective_Date));

    }
}