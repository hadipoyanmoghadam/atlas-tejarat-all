package dpi.atlas.service.cfs.handler;

import dpi.atlas.service.cm.core.TJCommand;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.util.Constants;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.model.NotFoundException;

import java.util.Map;
import java.util.List;
import java.sql.SQLException;

/**
 * Created by Nasiri (Sparrow 91/05/09).
 * To change this template use File | Settings | File Templates.
 */
public class GetCustomerStatement extends CFSHandlerBase implements Configurable {
    private String accountDescription;


    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        if (msg.getAttributeAsString(Fields.CARD_TYPE).equals(CFSConstants.GROUP_CARD_CHILD))
            childStatment(msg, holder);
        else if (msg.getAttributeAsString(Fields.MESSAGE_TYPE).equalsIgnoreCase( TJCommand.CMD_CMS_CHARGE_TRANSACTION))
            chargeStatement(msg, holder);
        else if (msg.getAttributeAsString(Fields.MESSAGE_TYPE).equalsIgnoreCase( TJCommand.CMD_CMS_WFP_STATEMENT))
            wfpStatement(msg, holder);
        else if (msg.getAttributeAsString(Fields.SERVICE_TYPE).equalsIgnoreCase(Fields.SERVICE_NASIM) ||
                msg.getAttributeAsString(Fields.SERVICE_TYPE).equalsIgnoreCase(Fields.SERVICE_CREDITS)||
                msg.getAttributeAsString(Fields.SERVICE_TYPE).equalsIgnoreCase(Fields.SERVICE_SIMIN))
            branchStatement(msg, holder);
        else if (msg.getAttributeAsString(Fields.SERVICE_TYPE).equalsIgnoreCase(Fields.SERVICE_TOURIST_CARD))
            touristStatement(msg, holder);
        else if ((msg.getAttributeAsString(Fields.SERVICE_TYPE).equalsIgnoreCase(Fields.SERVICE_PG) ||
                msg.getAttributeAsString(Fields.SERVICE_TYPE).equalsIgnoreCase(Fields.SERVICE_GROUP_WEB)) &&
                msg.getAttributeAsString(Fields.CARD_TYPE).equals(CFSConstants.GROUP_CARD_PARENT))
            childStatment(msg, holder);
        else if ((msg.getAttributeAsString(Fields.SERVICE_TYPE).equalsIgnoreCase(Fields.SERVICE_PG) ||
                msg.getAttributeAsString(Fields.SERVICE_TYPE).equalsIgnoreCase(Fields.SERVICE_GROUP_WEB))&&
                msg.getAttributeAsString(Fields.MESSAGE_TYPE).equalsIgnoreCase( TJCommand.CMD_GROUP_ACCOUNT_STATEMENT))
            accountStatement(msg, holder);
        else
            normalStatement(msg, holder);

    }

    public void normalStatement(CMMessage msg, Map holder) throws CFSFault {
        String acc = (String) msg.getAttribute(accountDescription);
        try {
            acc = ISOUtil.zeropad(acc, 13);
        } catch (ISOException e) {
            log.error("Can not zeropad acc = '" + acc + "'" + e);
        }
        int transCount = Integer.parseInt(msg.getAttributeAsString(Fields.TRANS_COUNT));

        if (acc == null) {
            throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        } else {
            List tran = null;
            try {
                tran = CFSFacadeNew.getSPWTxList(acc, transCount);
            } catch (NotFoundException e) {
                throw new CFSFault(CFSFault.FLT_UNABLE_TO_LOCATE_RECORD_ON_FILE, new Exception(ActionCode.UNABLE_TO_LOCATE_RECORD_ON_FILE));
            } catch (SQLException e) {
                log.error("Can not run SQL Statement: " + e.getMessage());
                throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
            }
            if (tran != null) {
                holder.put(CFSConstants.STATEMENT, tran);
                holder.put(Constants.RESULT_SIZE, String.valueOf(tran.size()));
            } else {
                holder.put(Constants.RESULT_SIZE, "0");
            }
        }
    }

    public void childStatment(CMMessage msg, Map holder) throws CFSFault {
        int transCount = Integer.parseInt(msg.getAttributeAsString(Fields.TRANS_COUNT));
        String cardNo = (String) msg.getAttribute(Fields.PAN);
        String fromDate= (String) msg.getAttribute(Fields.FROM_DATE);
        String toDate= (String) msg.getAttribute(Fields.TO_DATE);
        if (cardNo == null || cardNo.equals("")) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
            throw new CFSFault(CFSFault.FLT_INVALID_CARD_NUMBER, new Exception(ActionCode.INVALID_CARD_NUMBER));
        } else {
            List tran = null;
            try {
                tran = CFSFacadeNew.getCardTxList(cardNo, transCount,fromDate,toDate);
            } catch (NotFoundException e) {
                throw new CFSFault(CFSFault.FLT_UNABLE_TO_LOCATE_RECORD_ON_FILE, new Exception(ActionCode.UNABLE_TO_LOCATE_RECORD_ON_FILE));
            } catch (SQLException e) {
                log.error("Can not run SQL Statement: " + e.getMessage());
                throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
            }
            if (tran != null) {
                holder.put(CFSConstants.STATEMENT, tran);
                holder.put(Constants.RESULT_SIZE, String.valueOf(tran.size()));
            } else {
                holder.put(Constants.RESULT_SIZE, "0");
            }
        }
    }

    public void branchStatement(CMMessage msg, Map holder) throws CFSFault {
        String acc = (String) msg.getAttribute(accountDescription);
        try {
            acc = ISOUtil.zeropad(acc, 13);
        } catch (ISOException e) {
            log.error("Can not zeropad acc = '" + acc + "'" + e);
        }
        int transCount = Integer.parseInt(msg.getAttributeAsString(Fields.TRANS_COUNT));
        if (transCount == 0)
            transCount = CFSConstants.MIN_MINISTATEMENT_SIZE;
        if (transCount > CFSConstants.MAX_MINISTATEMENT_SIZE)
            transCount = CFSConstants.MAX_MINISTATEMENT_SIZE;

        String minAmount = msg.getAttributeAsString(Fields.MIN_AMOUNT);
        String maxAmount = msg.getAttributeAsString(Fields.MAX_AMOUNT);
        String fromDate = msg.getAttributeAsString(Fields.FROM_DATE);
        String toDate = msg.getAttributeAsString(Fields.TO_DATE);
        String fromTime = msg.getAttributeAsString(Fields.FROM_TIME);
        String toTime = msg.getAttributeAsString(Fields.TO_TIME);
        String opCode = msg.getAttributeAsString(Fields.OPERATION_CODE);
        String creditDebit = msg.getAttributeAsString(Fields.DEBIT_CREDIT);
        String branchDocNo = msg.getAttributeAsString(Fields.BRANCH_DOC_NO);
        String fromSequence = msg.getAttributeAsString(Fields.MN_RRN);
        String cardNo = msg.getAttributeAsString(Fields.PAN);

        if (acc == null) {
            throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
        } else {
            if(msg.getAttributeAsString(Fields.REQUEST_TYPE).equalsIgnoreCase("0")){
            List tran = null;
            try {
                tran = CFSFacadeNew.getBranchTxList(acc, cardNo, fromDate, toDate, fromTime, toTime, minAmount, maxAmount, transCount, opCode, branchDocNo, creditDebit, fromSequence);
            } catch (SQLException e) {
                log.error("Can not run SQL Statement: " + e.getMessage());
                throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
            }
            if (tran != null) {
                holder.put(CFSConstants.STATEMENT, tran);
                holder.put(Constants.RESULT_SIZE, String.valueOf(tran.size()));
            } else {
                holder.put(Constants.RESULT_SIZE, "0");
            }
            }else if(msg.getAttributeAsString(Fields.REQUEST_TYPE).equalsIgnoreCase("1")){
                int count;
                try {
                    count = CFSFacadeNew.getBranchTxCount(acc, cardNo, fromDate, toDate, fromTime, toTime, minAmount, maxAmount, opCode, branchDocNo, creditDebit, fromSequence);
                } catch (SQLException e) {
                    log.error("Can not run SQL Statement: " + e.getMessage());
                    throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
                }
                msg.setAttribute(Constants.TRAN_COUNT,String.valueOf(count));

            }else if(msg.getAttributeAsString(Fields.REQUEST_TYPE).equalsIgnoreCase("2")){ //full statement
                List tran = null;
                try {
                    tran = CFSFacadeNew.getBranchFullStatement(acc, cardNo, fromDate, toDate, fromTime, toTime, minAmount, maxAmount, transCount, opCode, branchDocNo, creditDebit, fromSequence);
                } catch (SQLException e) {
                    log.error("Can not run SQL Statement: " + e.getMessage());
                    throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
                }
                if (tran != null) {
                    holder.put(CFSConstants.STATEMENT, tran);
                    holder.put(Constants.RESULT_SIZE, String.valueOf(tran.size()));
                } else {
                    holder.put(Constants.RESULT_SIZE, "0");
                }
            }
        }
    }

    public void touristStatement(CMMessage msg, Map holder) throws CFSFault {
        String acc = (String) msg.getAttribute(accountDescription);
        try {
            acc = ISOUtil.zeropad(acc, 13);
        } catch (ISOException e) {
            log.error("Can not zeropad acc = '" + acc + "'" + e);
        }
        int transCount = Integer.parseInt(msg.getAttributeAsString(Fields.TRANS_COUNT));
        if (transCount == 0)
            transCount = CFSConstants.MIN_MINISTATEMENT_SIZE;
        if (transCount > CFSConstants.MAX_MINISTATEMENT_SIZE)
            transCount = CFSConstants.MAX_MINISTATEMENT_SIZE;

        String fromDate = msg.getAttributeAsString(Fields.FROM_DATE);
        String toDate = msg.getAttributeAsString(Fields.TO_DATE);
        String fromTime = msg.getAttributeAsString(Fields.FROM_TIME);
        String toTime = msg.getAttributeAsString(Fields.TO_TIME);
        String fromSequence = msg.getAttributeAsString(Fields.MN_RRN);
        String cardNo = msg.getAttributeAsString(Fields.PAN);

        if (cardNo == null) {
            throw new CFSFault(CFSFault.FLT_INVALID_CARD_NUMBER, ActionCode.INVALID_CARD_NUMBER);
        } else {
            List tran = null;
            try {

                if (msg.getAttributeAsString(Fields.MESSAGE_TYPE).equalsIgnoreCase(TJCommand.CMD_TOURIST_CARD_STATEMENT) &&
                        msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED).equalsIgnoreCase("0"))
                    tran = CFSFacadeNew.getCardStatement(acc, cardNo, fromDate, toDate, fromTime, toTime, transCount, fromSequence);
                else if (msg.getAttributeAsString(Fields.MESSAGE_TYPE).equalsIgnoreCase(TJCommand.CMD_TOURIST_CARD_STATEMENT) &&
                        msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED).equalsIgnoreCase("1"))
                    tran = CFSFacadeNew.getTouristStatement(acc, fromDate, toDate, fromTime, toTime, transCount, fromSequence);
                else if (msg.getAttributeAsString(Fields.MESSAGE_TYPE).equalsIgnoreCase(TJCommand.CMD_TOURIST_CHARGE_STATEMENT))
                    tran = CFSFacadeNew.getChargeStatement(acc, cardNo, fromDate, toDate, fromTime, toTime, transCount, fromSequence);

            } catch (SQLException e) {
                log.error("Can not run SQL Statement: " + e.getMessage());
                throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
            }
            if (tran != null) {
                holder.put(CFSConstants.STATEMENT, tran);
                holder.put(Constants.RESULT_SIZE, String.valueOf(tran.size()));
            } else {
                holder.put(Constants.RESULT_SIZE, "0");
            }

        }
    }

    public void chargeStatement(CMMessage msg, Map holder) throws CFSFault {
          String acc = (String) msg.getAttribute(accountDescription);
          try {
              acc = ISOUtil.zeropad(acc, 13);
          } catch (ISOException e) {
              log.error("Can not zeropad acc = '" + acc + "'" + e);
          }

          String fromDate = msg.getAttributeAsString(Fields.FROM_DATE);
          String toDate = msg.getAttributeAsString(Fields.TO_DATE);

          if (acc == null) {
              throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
          } else {
              List tran;
              try {
                  tran = CFSFacadeNew.getChargeTxList(acc,fromDate, toDate);
              } catch (SQLException e) {
                  log.error("Can not run SQL Statement: " + e.getMessage());
                  throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
              }
              if (tran != null) {
                  holder.put(CFSConstants.STATEMENT, tran);
                  holder.put(Constants.RESULT_SIZE, String.valueOf(tran.size()));
              } else {
                  holder.put(Constants.RESULT_SIZE, "0");
              }
          }
      }

    public void accountStatement(CMMessage msg, Map holder) throws CFSFault {
        int transCount = Integer.parseInt(msg.getAttributeAsString(Fields.TRANS_COUNT));
        String acc = (String) msg.getAttribute(accountDescription);
        try {
            acc = ISOUtil.zeropad(acc, 13);
        } catch (ISOException e) {
            log.error("Can not zeropad acc = '" + acc + "'" + e);
        }
        String fromDate= (String) msg.getAttribute(Fields.FROM_DATE);
        String toDate= (String) msg.getAttribute(Fields.TO_DATE);
        if (acc == null || acc.equals("")) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        } else {
            List tran = null;
            try {
                tran = CFSFacadeNew.getAccountTxList(acc, transCount,fromDate,toDate);
            } catch (NotFoundException e) {
                throw new CFSFault(CFSFault.FLT_UNABLE_TO_LOCATE_RECORD_ON_FILE, new Exception(ActionCode.UNABLE_TO_LOCATE_RECORD_ON_FILE));
            } catch (SQLException e) {
                log.error("Can not run SQL Statement: " + e.getMessage());
                throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
            }
            if (tran != null) {
                holder.put(CFSConstants.STATEMENT, tran);
                holder.put(Constants.RESULT_SIZE, String.valueOf(tran.size()));
            } else {
                holder.put(Constants.RESULT_SIZE, "0");
            }
        }
    }

    public void wfpStatement(CMMessage msg, Map holder) throws CFSFault {
        String acc = (String) msg.getAttribute(accountDescription);
        try {
            acc = ISOUtil.zeropad(acc, 13);
        } catch (ISOException e) {
            log.error("Can not zeropad acc = '" + acc + "'" + e);
        }

        String fromDate = msg.getAttributeAsString(Fields.FROM_DATE);
        String toDate = msg.getAttributeAsString(Fields.TO_DATE);

        if (acc == null) {
            throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
        } else {
            List tran;
            try {
                tran = CFSFacadeNew.getWfpTxList(acc,fromDate, toDate);
            } catch (SQLException e) {
                log.error("Can not run SQL Statement: " + e.getMessage());
                throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
            }
            if (tran != null) {
                holder.put(CFSConstants.STATEMENT, tran);
                holder.put(Constants.RESULT_SIZE, String.valueOf(tran.size()));
            } else {
                holder.put(Constants.RESULT_SIZE, "0");
            }
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountDescription = cfg.get(CFSConstants.ACCOUNT_DESCRIPTION);
        if ((accountDescription == null) || (accountDescription.trim().equals("")))
            log.fatal("Account Description is not Specified");
    }
}
