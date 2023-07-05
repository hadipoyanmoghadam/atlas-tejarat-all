package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.entity.Customer;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: July 8 2013
 * Time: 9:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class CheckCustomer extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        try{
        if (!map.containsKey(Constants.CUSTOMER_ACCOUNT)) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        }
        Customer customer = null;


            if (!map.containsKey(Fields.BRANCH_CUSTOMER_ID)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
            }
            String customerId = (String) map.get(Fields.BRANCH_CUSTOMER_ID);
            customer = ChannelFacadeNew.getCustomerInfo(customerId);
            map.put(Constants.CUSTOMER, customer);
        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside checkCustomer.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
