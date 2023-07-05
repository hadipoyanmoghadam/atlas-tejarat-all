package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.entity.Customer;
import dpi.atlas.model.tj.entity.CustomerInfo;
import dpi.atlas.model.tj.entity.CustomerService;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.util.CustomerUtil;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.util.Map;

/**
 * LoginHandler class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.3 $ $Date: 2007/10/29 14:04:28 $
 */
public class LoginHandler extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String service = (String) msg.getAttribute(Fields.SERVICE_TYPE);

        String pwd = (String) msg.getAttribute(Fields.CUSTOMER_PIN);

        atlas.hibernate.Customer customer = (atlas.hibernate.Customer) holder.get(Constants.CUSTOMER);
        String customerPWD = CustomerUtil.getServicePWD(customer, service);

        CMResultSet result = null;
        if (log.isDebugEnabled()) log.debug("pwd = [" + pwd + "]");
        if (log.isDebugEnabled()) log.debug("customerPWD = [" + customerPWD + "]");
        if (pwd == null || pwd.equals(""))
            throw new CMFault(CMFault.FAULT_AUTH_SMS_PIN_REQUIRED, new Exception("pin required"));
        if (customerPWD == null || !customerPWD.equals(pwd)) {
            throw new CMFault(CMFault.FAULT_AUTH_SMS_INVALID_PIN, new Exception("invalid pin"));
//            result = new CMResultSet();
//            result.setHeaderField(Fields.ACTION_CODE, ActionCode.INCORRECT_PIN);
//            result.setHeaderField(Fields.ACTION_MESSAGE, "Incorrect pin");
        } else {
            result = new CMResultSet(customer);
            result.setHeaderField(Fields.ACTION_CODE, ActionCode.APPROVED);
        }
        msg.setAttribute(CMMessage.RESPONSE, result);

//        }
    }

    public void doProcess_old(CMMessage msg, Map holder) throws CMFault {

        String pwd = (String) msg.getAttribute(Fields.CUSTOMER_PIN);
        Customer customer = (Customer) holder.get(Constants.CUSTOMER);
        CustomerService customerService = (CustomerService) holder.get(Constants.CUSTOMER_SERVICE);
        Account account = (Account) holder.get(Constants.CUSTOMER_ACCOUNT);
        String customerPWD = customerService.getPin();

        if (pwd == null || pwd.equals("")) {
            CMResultSet result = new CMResultSet();
            result.setHeaderField(Params.ACTION_CODE, ActionCode.INCORRECT_PIN);
            result.setHeaderField(Params.ACTION_MESSAGE, "Pin Required");
            msg.setAttribute(CMMessage.RESPONSE_STRING, result.toString());
            throw new CMFault(CMFault.FAULT_AUTH_SMS_PIN_REQUIRED, new Exception("Pin Required"));
        }
        if (customerPWD == null || !customerPWD.equals(pwd)) {
            CMResultSet result = new CMResultSet();
            result.setHeaderField(Params.ACTION_CODE, ActionCode.INCORRECT_PIN);
            result.setHeaderField(Params.ACTION_MESSAGE, "Invalid Pin");
            msg.setAttribute(CMMessage.RESPONSE_STRING, result.toString());
            throw new CMFault(CMFault.FAULT_AUTH_INVALID_PIN, new Exception("Invalid Pin"));
        } else {
            CustomerInfo customerInfo = new CustomerInfo(customer, customerService, account);
            CMResultSet result = new CMResultSet(customerInfo);
            result.setHeaderField(Fields.ACTION_CODE, ActionCode.APPROVED);
            msg.setAttribute(CMMessage.RESPONSE, result.toString());

        }
    }
}

