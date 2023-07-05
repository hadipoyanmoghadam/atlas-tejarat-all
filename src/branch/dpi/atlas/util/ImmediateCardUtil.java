package branch.dpi.atlas.util;

import branch.dpi.atlas.model.tj.entity.*;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Comment;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringReader;
import java.util.*;

/**
 * User: Behnaz
 * Date: Feb 6, 2012
 * Time: 9:32:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImmediateCardUtil {
    private static Log log = LogFactory.getLog(ImmediateCardUtil.class);
    private static final String accountRange1_Min = "0003200000000";
    private static final String accountRange1_Max = "0005400000000";
    private static final String accountRange2_Min = "0008100000000";
    private static final String accountRange2_Max = "0009999999999";


    public static boolean validateElement(String strElement, int len) {
        if (strElement != null) {
            if (strElement.length() == len) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateElementNotZero(String strElement, int len) {
        if (strElement != null && Double.parseDouble(strElement) != 0) {
            if (strElement.length() == len) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateElementISZero(String strElement) {
        if (strElement != null && Double.parseDouble(strElement) != 0) {
            return false;
        }
        return true;
    }

    public static boolean validateElementLessThan(String strElement, int len) {
        if (strElement != null) {
            if (strElement.length() < len) {
                return true;
            }
        }
        return false;
    }
    public static boolean validateElementLessAndEqual(String strElement, int len) {
        if (strElement != null) {
            if (strElement.length() <= len) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkNationalCodeValidity(String strElement) {
        if (strElement != null && !strElement.equals("")) {
            if (strElement.length() == 10 || strElement.length() == 11) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkGiftCardValidity(CFDCustomer customer) throws Exception {
        try {
            int cardType = customer.getPan().getCardType();
            if (cardType == Constants.NORMAL_CARD)
                return true;


            CFDAccount account = (CFDAccount) customer.getAccounts().get(0);
            long maxtransLimit = account.getMax_trans_limit();
            String cardSerialNo = customer.getPan().getSerial();
            if (cardSerialNo.startsWith("49")) {
                String zonePostFix = cardSerialNo.substring(2, 4);
                if (Integer.valueOf(zonePostFix) * 10000 == maxtransLimit)
                    return true;
                return false;
            }
            if (cardSerialNo.startsWith("4")) {
                String zonePostFix = cardSerialNo.substring(1, 4);
                if (Integer.valueOf(zonePostFix) * 100000 == maxtransLimit)
                    return true;
                return false;
            }
            return false;

        } catch (Exception e) {
            throw new Exception("MAX_TRANS_LIMIT = " + ((CFDAccount) customer.getAccounts().iterator().next()).getMax_trans_limit() +
                    " For Card_No = " + customer.getPan().getSerial() + " is different with it's CardZone");

        }
    }

    private static Element getRootBaseElementFromXML(String xmlRequestStr) {
        Element root = null;
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = null;
            try {
                doc = builder.build(new InputSource(new StringReader(xmlRequestStr)));
            } catch (JDOMException e) {
                System.out.println("ERROR: Can not Pars XML Request, It seems it is not a XML Format.");
            }

            root = doc.getRootElement();

        } catch (NullPointerException e) {
            log.error("ERROR: Object is Null." + e.getMessage());
        } catch (Exception e) {
            log.error("Error in Reading recived Data:" + e.getMessage());
        }

        return root;
    }

    public static CFDRequestBase parseImmediateCardRequest(String requestStr) {
        try {
            Element rootElement = getRootBaseElementFromXML(requestStr);
            String requestType = rootElement.getName();
            if (requestType.equals("CUSTOMER")) {
                CFDCustomer customer = getCustomerFromXML(rootElement);
                customer.setRequestType(Constants.CARD_DEFINITION);
                return customer;
            } else if (requestType.equals("BRANCH_GIFT")) {
                String transType = ((Element) rootElement.getChildren().get(0)).getName();
                if (transType.equals("DEPOSIT")) {
                    CFDGiftDeposit giftDeposit = getBranchGiftDepositFromXML(rootElement);
                    giftDeposit.setRequestType(Constants.CARD_DEPOSIT);
                    return giftDeposit;
                } else if (transType.equals("REVOKE")) {
                    CFDGiftRevoke giftRevoke = getBranchGiftRevokeFromXML(rootElement);
                    giftRevoke.setRequestType(Constants.CARD_REVOKE);
                    return giftRevoke;
                } else if (transType.equals("FOLLOWUP")) {
                    CFDFollowUp followup = getBranchFollowUpFromXML(rootElement);
                    followup.setRequestType(Constants.CARD_FOLLOWUP);
                    return followup;
                } else
                    return null;
            } else if (requestType.equals("BRANCH_BALANCE")) {
                CFDBalance cfdBalance = getBranchBalanceFromXML(rootElement);
                cfdBalance.setRequestType(Constants.CARD_BALANCE);
                return cfdBalance;
            } else
                return null;

        } catch (Exception e) {
//            e.printStackTrace();
            log.error("ERROR in parseImmediateCardRequest : " + e.getMessage());
        }
        return null;
    }

    private static CFDBalance getBranchBalanceFromXML(Element branchBalance) throws Exception {
        Element balanceElement = branchBalance.getChild("BALANCE");
        CFDBalance cfdBalance = new CFDBalance();

        cfdBalance.setRrn(balanceElement.getChildText("RRN"));
        cfdBalance.setSerial(balanceElement.getChildText("SERIAL"));
        cfdBalance.setSequence(balanceElement.getChildText("SEQUENCE"));
        cfdBalance.setSrc_account_no(balanceElement.getChildText("ACCOUNT_NO"));
        cfdBalance.setBranch_code(balanceElement.getChildText("BRANCH_CODE"));
        cfdBalance.setCreation_date(balanceElement.getChildText("CREATION_DATE"));
        cfdBalance.setCreation_time(balanceElement.getChildText("CREATION_TIME"));
        cfdBalance.setMessageType(Constants.CARD_DEFAULT_RECORDFLAG);

        return cfdBalance;
    }

    private static CFDGiftDeposit getBranchGiftDepositFromXML(Element branchGiftElement) throws Exception {
        Element depositElement = branchGiftElement.getChild("DEPOSIT");
        CFDGiftDeposit giftDeposit = new CFDGiftDeposit();

        giftDeposit.setRrn(depositElement.getChildText("RRN"));
        giftDeposit.setSerial(depositElement.getChildText("SERIAL"));
        giftDeposit.setSequence(depositElement.getChildText("SEQUENCE"));
        giftDeposit.setSrc_account_no(depositElement.getChildText("SRC_ACCOUNT_NO"));
        giftDeposit.setDest_account_no(depositElement.getChildText("DEST_ACCOUNT_NO"));
        giftDeposit.setAmount(depositElement.getChildText("AMOUNT"));
        giftDeposit.setBranch_code(depositElement.getChildText("BRANCH_CODE"));
        giftDeposit.setBranch_docNo(depositElement.getChildText("BRANCH_DOCNO"));
        giftDeposit.setCreation_date(depositElement.getChildText("CREATION_DATE"));
        giftDeposit.setCreation_time(depositElement.getChildText("CREATION_TIME"));
        giftDeposit.setDesc(depositElement.getChildText("TRANSDESC"));
        giftDeposit.setMessageType(Constants.CARD_FINANCIAL_RECORDFLAG);

        return giftDeposit;
    }

    private static CFDGiftRevoke getBranchGiftRevokeFromXML(Element branchGiftElement) throws Exception {
        Element revokeElement = branchGiftElement.getChild("REVOKE");
        CFDGiftRevoke giftRevoke = new CFDGiftRevoke();

        giftRevoke.setRrn(revokeElement.getChildText("RRN"));
        giftRevoke.setSerial(revokeElement.getChildText("SERIAL"));
        giftRevoke.setSequence(revokeElement.getChildText("SEQUENCE"));
        giftRevoke.setSrc_account_no(revokeElement.getChildText("SRC_ACCOUNT_NO"));
        giftRevoke.setDest_account_no(revokeElement.getChildText("DEST_ACCOUNT_NO"));
        giftRevoke.setBranch_code(revokeElement.getChildText("BRANCH_CODE"));
        giftRevoke.setBranch_docNo(revokeElement.getChildText("BRANCH_DOCNO"));
        giftRevoke.setCreation_date(revokeElement.getChildText("CREATION_DATE"));
        giftRevoke.setCreation_time(revokeElement.getChildText("CREATION_TIME"));
        giftRevoke.setDesc(revokeElement.getChildText("TRANSDESC"));
        giftRevoke.setMessageType(Constants.CARD_FINANCIAL_RECORDFLAG);

        return giftRevoke;
    }

    private static CFDFollowUp getBranchFollowUpFromXML(Element branchFollowupElement) throws Exception {
        Element followupElement = branchFollowupElement.getChild("FOLLOWUP");
        CFDFollowUp followup = new CFDFollowUp();

        followup.setRrn(followupElement.getChildText("RRN"));
        followup.setSerial(followupElement.getChildText("SERIAL"));
        followup.setSequence(followupElement.getChildText("SEQUENCE"));
        followup.setSrc_account_no(followupElement.getChildText("SRC_ACCOUNT_NO"));
        followup.setDest_account_no(followupElement.getChildText("DEST_ACCOUNT_NO"));
        followup.setBranch_code(followupElement.getChildText("BRANCH_CODE"));
        followup.setBranch_docNo(followupElement.getChildText("BRANCH_DOCNO"));
        followup.setTrans_date(followupElement.getChildText("TRANS_DATE"));
        followup.setTrans_time(followupElement.getChildText("TRANS_TIME"));
        followup.setCreation_date(followupElement.getChildText("CREATION_DATE"));
        followup.setCreation_time(followupElement.getChildText("CREATION_TIME"));
        followup.setDesc(followupElement.getChildText("TRANSDESC"));
        followup.setMessageType(Constants.CARD_DEFAULT_RECORDFLAG);

        return followup;
    }

    private static CFDCustomer getCustomerFromXML(Element customerElement) throws Exception {
        CFDCustomer customer = new CFDCustomer();
        customer.setflag(customerElement.getChildText("RECORD_FLAG"));
        customer.setRrn(customerElement.getChildText("RRN"));
        customer.setCustomerInfo(getCustomerInfoFromXML(customerElement.getChild("CUSTOMER_INFO")));
        customer.setAccounts(getAccountsFromXML(customerElement.getChild("ACCOUNTS")));
        customer.setPan(getPANFromXML(customerElement.getChild("PAN")));
        customer.setSerial(customer.getPan().getSerial().substring(6));
        //Settion the Status of Card. this is done, if we XML have status for Card, we must leave status unchanged.
        //if status in not set in XML file, so if card is usual card, it must be define as active and if it is gift card, it must be deactive.
        if (customer.getPan().getCardAccount_status() == Constants.CARD_STATUS_NOT_ASSIGNED_FLAG)
            if (((CFDAccount) customer.getAccounts().iterator().next()).getMax_trans_limit() == Constants.IGNORE_MAX_TRANS_LIMIT)
                customer.getPan().setCardAccount_status("" + Constants.CARD_STATUS_ACTIVE_FLAG);
            else
                customer.getPan().setCardAccount_status("" + Constants.CARD_STATUS_DISABLE_FLAG);

        return customer;
    }

    private static CFDCustomerInfo getCustomerInfoFromXML(Element customerInfoElement) throws Exception {
        CFDCustomerInfo customerInfo = new CFDCustomerInfo();
        if (customerInfoElement != null) {
            customerInfo.setSex(customerInfoElement.getChildText("SEX"));
            customerInfo.setNameFamilyLatin(customerInfoElement.getChildText("NAME_FAMILY_LATIN"));
            customerInfo.setName_persian(customerInfoElement.getChildText("NAME_PERSIAN"));
            customerInfo.setFamily_persian(customerInfoElement.getChildText("FAMILY_PERSIAN"));
            customerInfo.setAddress(customerInfoElement.getChildText("ADDRESS"));
            customerInfo.setPhoneNumber(customerInfoElement.getChildText("PHONE"));
            customerInfo.setNationalCode(customerInfoElement.getChildText("NATIONALCODE"));
            customerInfo.setIDNumber(customerInfoElement.getChildText("ID_NUMBER"));
            customerInfo.setBirthDate(customerInfoElement.getChildText("BIRTH_DATE"));
        }
        return customerInfo;
    }

    private static Vector getAccountsFromXML(Element accountsElement) throws Exception {
        Vector vector = new Vector();

        List accounts_list = accountsElement.getChildren("ACCOUNT");

        Iterator iterator = accounts_list.iterator();
        while (iterator.hasNext()) {
            CFDAccount account = new CFDAccount();
            Element myAccount = (Element) iterator.next();

            account.setAccount_no(myAccount.getChildText("ACCOUNT_NO"));
            account.setBalance(myAccount.getChildText("BALANCE"));
            account.setAccount_group(myAccount.getChildText("ACCOUNT_GROUP"));
            account.setAccount_type(account.getAccount_group());
            account.setAccount_status(myAccount.getChildText("ACCOUNT_STATUS"));
            account.setCreation_date_account(myAccount.getChildText("CREATION_DATE"));
            account.setEdit_date_account(myAccount.getChildText("EDIT_DATE"));
            account.setSgb_branch_id(myAccount.getChildText("SGB_BRANCH_ID"));
            account.setMax_trans_limit(myAccount.getChildText("MAX_TRANS_LIMIT"));
            account.setAccount_opener_name(myAccount.getChildText("ACCOUNT_OPENER_NAME"));
            account.setWithdraw_type(myAccount.getChildText("WITHDRAW_TYPE"));
            account.setAccount_title(myAccount.getChildText("ACCOUNT_TYPE"));
            vector.add(account);
        }
        return vector;
    }

    private static CFDPan getPANFromXML(Element panElement) throws Exception {
        CFDPan pan = new CFDPan();

        String serial = panElement.getChildText("SERIAL");
        if (serial.startsWith("4"))
            pan.setCardType(Constants.GIFT_CARD);
        else pan.setCardType(Constants.NORMAL_CARD);
        pan.setSerial(serial);
        pan.setRow(panElement.getChildText("ROW"));
        pan.setSequence(panElement.getChildText("SEQUENCE"));
        pan.setSparrow_branch_id(panElement.getChildText("SPARROW_BRANCH_ID"));
        pan.setCreation_date_pan(panElement.getChildText("CREATION_DATE"));
        pan.setEdit_date_pan(panElement.getChildText("EDIT_DATE"));
        pan.setCardAccount_status(panElement.getChildText("CARD_STATUS"));
        return pan;
    }

    public static String GenerateImmediateCardResponse(String actionCode, String errorDesc, String recordFlag, String panSerial, String rrn) throws Exception {
        String respdatetime = DateUtil.getSystemDate() + DateUtil.getSystemTime();
        String xmlstr = "";
        Element root = new Element("CUSTOMERRESPONSE");
        Document doc = new Document(root);
        Calendar calc1 = Calendar.getInstance();
        doc.getContent().add(0, new Comment(" Generated: " + calc1.getTime().toString()));

        root.addContent(new Element("ACTIONCODE").setText(actionCode));
        root.addContent(new Element("DESC").setText(errorDesc));
        root.addContent(new Element("RECORD_FLAG").setText(recordFlag));
        root.addContent(new Element("RRN").setText(rrn));
        root.addContent(new Element("PAN_SERIAL").setText(panSerial.substring(6)));
        root.addContent(new Element("RESPDATETIME").setText(respdatetime));

        XMLOutputter out = new XMLOutputter();
        xmlstr = out.outputString(doc);
        return xmlstr;
    }

    public static String GenerateErrorXML(String actionCodeStr, String desc_str) throws Exception {
        String xmlstr = "";
        Element root = new Element("root");
        Document doc = new Document(root);
        doc.getContent().add(0, new Comment(" Generated: 1385/12/28 "));

        Element actionCode = new Element("ACTIONCODE");
        actionCode.setText(actionCodeStr);
        root.addContent(actionCode);
        Element desc = new Element("DESC");
        desc.setText(desc_str);
        root.addContent(desc);

        XMLOutputter out = new XMLOutputter();

        xmlstr = out.outputString(doc);
        return xmlstr;
    }

    public static String GenerateBranchGiftCardResponse(String actionCode, String desc, String panSerial, String rrn) throws Exception {
        String respdatetime = DateUtil.getSystemDate() + DateUtil.getSystemTime();
        String xmlstr = "";
        Element root = new Element("BRANCH_GIFT_RESPONSE");
        Document doc = new Document(root);
        Calendar calc1 = Calendar.getInstance();
        doc.getContent().add(0, new Comment(" Generated: " + calc1.getTime().toString()));

        root.addContent(new Element("ACTIONCODE").setText(actionCode));
        root.addContent(new Element("DESC").setText(desc));
        root.addContent(new Element("RECORD_FLAG").setText(Constants.CARD_FINANCIAL_RECORDFLAG));
        root.addContent(new Element("RRN").setText(rrn));
        root.addContent(new Element("PAN_SERIAL").setText(panSerial.substring(6)));
        root.addContent(new Element("RESPDATETIME").setText(respdatetime));

        XMLOutputter out = new XMLOutputter();
        xmlstr = out.outputString(doc);
        return xmlstr;
    }

    public static String GenerateBranchGiftRevokeResponse(String actionCode, String desc, String panSerial, String rrn, String amount) throws Exception {
        String respdatetime = DateUtil.getSystemDate() + DateUtil.getSystemTime();
        String xmlstr = "";
        Element root = new Element("GIFT_REVOKE_RESPONSE");
        Document doc = new Document(root);
        Calendar calc1 = Calendar.getInstance();
        doc.getContent().add(0, new Comment(" Generated: " + calc1.getTime().toString()));

        root.addContent(new Element("ACTIONCODE").setText(actionCode));
        root.addContent(new Element("DESC").setText(desc));
        root.addContent(new Element("RECORD_FLAG").setText(Constants.CARD_FINANCIAL_RECORDFLAG));
        root.addContent(new Element("RRN").setText(rrn));
        root.addContent(new Element("PAN_SERIAL").setText(panSerial.substring(6)));
        root.addContent(new Element("AMOUNT").setText(amount));
        root.addContent(new Element("RESPDATETIME").setText(respdatetime));

        XMLOutputter out = new XMLOutputter();
        xmlstr = out.outputString(doc);
        return xmlstr;
    }

    public static String GenerateBranchBalanceResponse(String actionCode, String desc, String panSerial, String rrn,
                                                       String availableBalance, String availableBalanceSign, String actualBalance, String actualBalanceSign) throws Exception {
        String respdatetime = DateUtil.getSystemDate() + DateUtil.getSystemTime();
        String xmlstr = "";
        Element root = new Element("BRANCH_BALANCE_RESPONSE");
        Document doc = new Document(root);
        Calendar calc1 = Calendar.getInstance();
        doc.getContent().add(0, new Comment(" Generated: " + calc1.getTime().toString()));

        root.addContent(new Element("ACTIONCODE").setText(actionCode));
        root.addContent(new Element("DESC").setText(desc));
        root.addContent(new Element("RECORD_FLAG").setText(Constants.CARD_DEFAULT_RECORDFLAG));
        root.addContent(new Element("RRN").setText(rrn));
        root.addContent(new Element("PAN_SERIAL").setText(panSerial.substring(6)));
        root.addContent(new Element("AVAILABLEBALANCE").setText(availableBalance));
        root.addContent(new Element("AVAILABLEBALANCE_SIGN").setText(availableBalanceSign));
        root.addContent(new Element("ACTUALBALANCE").setText(actualBalance));
        root.addContent(new Element("ACTUALBALANCE_SIGN").setText(actualBalanceSign));
        root.addContent(new Element("RESPDATETIME").setText(respdatetime));

        XMLOutputter out = new XMLOutputter();
        xmlstr = out.outputString(doc);
        return xmlstr;
    }

    public static String GenerateBranchFollowUpResponse(String actionCode, String desc, String panSerial, String rrn, String amount,String follow_actioncode) throws Exception {
        String respdatetime = DateUtil.getSystemDate() + DateUtil.getSystemTime();
        String xmlstr = "";
        Element root = new Element("BRANCH_FOLLOWUP_RESPONSE");
        Document doc = new Document(root);
        Calendar calc1 = Calendar.getInstance();
        doc.getContent().add(0, new Comment(" Generated: " + calc1.getTime().toString()));

        root.addContent(new Element("ACTIONCODE").setText(actionCode));
        root.addContent(new Element("DESC").setText(desc));
        root.addContent(new Element("RECORD_FLAG").setText(Constants.CARD_DEFAULT_RECORDFLAG));
        root.addContent(new Element("RRN").setText(rrn));
        root.addContent(new Element("PAN_SERIAL").setText(panSerial.substring(6)));
        root.addContent(new Element("FOLLOW_ACTIONCODE").setText(follow_actioncode));
        root.addContent(new Element("AMOUNT").setText(amount));
        root.addContent(new Element("RESPDATETIME").setText(respdatetime));

        XMLOutputter out = new XMLOutputter();
        xmlstr = out.outputString(doc);
        return xmlstr;

    }

    public static boolean faraGroup(String account_group, String accountNO) {
        boolean fara_group = false;
        ArrayList faraList = getFARAHostID();
        for (int i = 0; i < faraList.size(); i++) {
            if (faraList.get(i).equals(account_group))
                fara_group = true;
        }                                                           //add to live 91
        if ((account_group.equals("21") || account_group.equals("021") || account_group.equals("91") || account_group.equals("091"))
                && ((accountNO.compareTo(accountRange1_Min) > 0 && (accountNO.compareTo(accountRange1_Max) < 0)) || (accountNO.compareTo(accountRange2_Min) > 0 && (accountNO.compareTo(accountRange2_Max) < 0))))
            fara_group = false;

        return fara_group;
    }

    public static boolean isChild(int row) throws Exception {
//              return (row > 1 && row < 1000);
        return (row > 1 );
      }

    public static boolean isParent(int row) throws Exception {
              return (row == 1);
      }

    private static ArrayList getGroupCardType() {
        ArrayList groupCardList = new ArrayList();
        groupCardList.add(Constants.GROUP_CARD_ACCOUNT_TYPE1);
        groupCardList.add(Constants.GROUP_CARD_ACCOUNT_TYPE2);
        groupCardList.add(Constants.GROUP_CARD_ACCOUNT_TYPE3);
        return groupCardList;
    }


    private static ArrayList getFARAHostID() {
        ArrayList faraList = new ArrayList();
        faraList.add("21");
        faraList.add("23");
        faraList.add("021");
        faraList.add("023");
        faraList.add("091");     //add to live 91
        faraList.add("91");
        return faraList;
    }

    public static boolean CardAccDigitsIsOK(String accountNo) throws Exception {
        boolean cardAccDigitIsOk = true;

        String acc = accountNo.substring(3);

        int sum = 0;
        for (int i = 10; i >= 4; i--)
            sum += Integer.parseInt(acc.substring(i - 1, i)) * (11 - i);

        sum += Integer.parseInt(acc.substring(0, 1)) * 4;
        sum += Integer.parseInt(acc.substring(1, 2)) * 3;
        sum += Integer.parseInt(acc.substring(2, 3)) * 2;

        if (Long.parseLong(accountNo.substring(0, 3)) != 0 ||
                Long.parseLong(acc) == 0 ||
                ((sum % 11) != 0))
            cardAccDigitIsOk = false;

        return cardAccDigitIsOk;
    }

}



