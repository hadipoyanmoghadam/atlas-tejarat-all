package branch.dpi.atlas.service.cm.handler.manzume;

import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.model.ModelException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * modified by F.Heydari
 * Date: NOV 3 2022
 * Time: 13:52 PM
 */
public class CheckAccountNature extends TJServiceHandler implements Configurable {

    ArrayList check_pay_code = new ArrayList();


    public void setConfiguration(Configuration configuration) throws ConfigurationException {

        String checkPayCode = configuration.get("checkPayCode");
        check_pay_code = CMUtil.tokenizString(checkPayCode, ",");
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        log.debug("inside CheckAccountNature.doProcess(CMMessage msg, Map holder)");
        ManzumeMessage manzumeMsg;
        String pin="";
        String messageSeq="";
        try {

            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

            List iranInsuranceAccountsList = ChannelFacadeNew.getIranInsuranceAccountList();

            manzumeMsg = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            pin = manzumeMsg.getPin();
            messageSeq=manzumeMsg.getMessageSequence();

            if (pin.equalsIgnoreCase(Constants.PIN_DEPOSIT_MANZUME)) {


                String id1 = manzumeMsg.getId1().trim();
                String id2=manzumeMsg.getId2().trim();
                String amount = manzumeMsg.getAmount();

                if (msg.hasAttribute(Constants.DEST_ACCOUNT_NATURE)) {

                    String destAccountNo = msg.getAttributeAsString(Fields.DEST_ACCOUNT_NO);
                    if (destAccountNo.length() < 13)
                        destAccountNo = ISOUtil.zeropad(destAccountNo, 13);

                    int destAccountNature = Integer.parseInt(msg.getAttributeAsString(Constants.DEST_ACCOUNT_NATURE));


                    String fPayCode = null != id1 ? id1 : "";
                    String sPayCode = null != id2 ? id2 : "";
                    String payCode = fPayCode;
                    if ("".equals(payCode) && !"".equals(sPayCode)) {
                        payCode = sPayCode;
                    }
                    String[] payCds = {fPayCode, sPayCode};

                  //  command.addParam(Fields.ACCOUNT_NATURE, String.valueOf(accountNature));
                    boolean isOneTimeIdPayment = (destAccountNature == Constants.ACCOUNT_NATURE_DEPOSIT_ON_TIME);


                    //add by fereshteh
                    if (destAccountNature == Constants.ACCOUNT_NATURE_DEPOSIT_ON_TIME) {
                        //reject
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                        throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                    }

                    if (destAccountNature == Constants.ACCOUNT_NATURE_NO_DEPOSIT_BY_PATTERN) {
                        try {
                            if (destAccountNature == Constants.ACCOUNT_NATURE_NO_DEPOSIT_BY_PATTERN) {
                                List patterns = ChannelFacadeNew.getPatternsList(destAccountNo);
                                boolean isMatched = CheckDigits4Manzume.isMachedbyPattern(payCds, patterns);
                                if (isMatched) {
                                    msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYMENT_CODE_IS_INVALID_BY_PATTERN);
                                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.PAYMENT_CODE_IS_INVALID_BY_PATTERN));
                                }
                            }
                        } catch (ISOException e) {
                            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
                            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
                        } catch (ModelException e) {
                            msg.setAttribute(Fields.AUTHORISED, Constants.NOTAUTHORISED);
                            if (e.getMessage().equals(Constants.PAYMENT_CODE_IS_INVALID)) {
                                msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYMENT_CODE_IS_INVALID);
                                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.PAYMENT_CODE_IS_INVALID));
                            } else if (e.getMessage().equals(Constants.INVALID_NUMBER_OF_PAYCODES)) {
                                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_NUMBER_OF_PAYMENT_CODE);
                                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_NUMBER_OF_PAYMENT_CODE));
                            } else if (e.getMessage().equals(Constants.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT)) {
                                msg.setAttribute(Params.ACTION_CODE, ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT);
                                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT));
                            }
                        }
                    }

                    //edit by fereshteh
                   // if (destAccountNature == Constants.ACCOUNT_NATURE_NO_DEPOSIT || destAccountNature == Constants.ACCOUNT_NATURE_NO_DEPOSIT_BY_PATTERN || isOneTimeIdPayment) {
                        if (destAccountNature == Constants.ACCOUNT_NATURE_NO_DEPOSIT || destAccountNature == Constants.ACCOUNT_NATURE_NO_DEPOSIT_BY_PATTERN ) {
                        String transAmnt=amount;
                            try {
                                boolean isCheckDigit = CheckDigits4Manzume.isCheckDigitNew(destAccountNo, payCds, transAmnt, msg);
                                if (!isCheckDigit) {

                                    msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYMENT_CODE_IS_INVALID);
                                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.PAYMENT_CODE_IS_INVALID));
                                }


                                //I replace this with the one

                                //commented by fereshteh 4 live 14011129
//                                if (destAccountNature == Constants.ACCOUNT_NATURE_DEPOSIT_ON_TIME) {
//
//                                    if (id1 == null || id1.trim().equals("") || ISOUtil.isZero(id1.trim())) {
//                                        msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYMENT_CODE_IS_INVALID);
//                                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.PAYMENT_CODE_IS_INVALID));
//                                    }
//
//                                    if (ChannelFacadeNew.checkId(destAccountNo, id1)) {
//                                        msg.setAttribute(Params.ACTION_CODE, ActionCode.BILL_PAYMENT_BEFORE_PAID);
//                                        throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.BILL_PAYMENT_BEFORE_PAID));
//                                    }
//                                }

                            } catch (SQLException e) {
                                log.error("DB has encountered an exception during fething data from tbchkdigitalg , tbOneTimeId ...");
                                msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
                                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_DATA_ERROR));
                            } catch (ISOException e) {
                                msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
                                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_DATA_ERROR));
                            } catch (ModelException e) {
                                msg.setAttribute(Fields.AUTHORISED, Constants.NOTAUTHORISED);
                                if (e.getMessage().equals(Constants.NO_RELATED_ALG_EXIST)) {
                                    msg.setAttribute(Params.ACTION_CODE, ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT);
                                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT));
                                } else if (e.getMessage().equals(Constants.INVALID_NUMBER_OF_PAYCODES)) {
                                    msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_NUMBER_OF_PAYMENT_CODE);
                                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_NUMBER_OF_PAYMENT_CODE));
                                } else if (e.getMessage().equals(Constants.INVALID_PAYMENT_CODE)) {
                                    msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYMENT_CODE_IS_INVALID);
                                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.PAYMENT_CODE_IS_INVALID));
                                }
                            }
                      //  }
                    }

               }
            }
        } catch (CMFault fe) {
            throw fe;
        } catch (ModelException e) {
            if (e.getMessage().equals(Constants.PAYMENT_CODE_IS_INVALID)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYMENT_CODE_IS_INVALID);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.PAYMENT_CODE_IS_INVALID));
            } else if (e.getMessage().equals(Constants.INVALID_NUMBER_OF_PAYCODES)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_NUMBER_OF_PAYMENT_CODE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_NUMBER_OF_PAYMENT_CODE));
            } else if (e.getMessage().equals(Constants.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT));
            }

        } catch (Exception e) {
            log.error("Exception"+pin+messageSeq);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }


}