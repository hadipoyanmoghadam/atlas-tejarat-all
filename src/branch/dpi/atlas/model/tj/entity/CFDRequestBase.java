package branch.dpi.atlas.model.tj.entity;


import branch.dpi.atlas.util.ImmediateCardUtil;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;


/**
 * User: Behnaz
 * Date: October  14, 2012
 * Time: 9:55:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class CFDRequestBase {
    String requestType;
    String rrn;
    String serial = "";
    String account_flag;
    String card_flag;


    public String toString() {
        return " rrn=" + rrn + " serial=" + serial ;
    }
    

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }


    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) throws Exception {
        try {
            serial = serial.trim();
            if (!ImmediateCardUtil.validateElement(serial, 13)) {
                throw new Exception();
            } else {
                this.serial = Constants.BANKE_TEJARAT_BIN + serial;
            }
        } catch (Exception e) {
            throw new Exception("SERIAL " + serial + " in XML File has Error in lenght or other things.");
        }
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn)  throws Exception  {
        try {
            rrn = rrn.trim();
            if (!ImmediateCardUtil.validateElement(rrn, 12)) {
                throw new Exception();
            } else {
                this.rrn = rrn;
            }
        } catch (Exception e) {
            throw new Exception("RRN " + rrn + " in XML File has Error in lenght or other things.");
        }

    }

    public String getAccount_flag() {
        return account_flag;
    }

    public void setflag(String flag) throws Exception {
        if (ImmediateCardUtil.validateElement(flag, 2)) {
            this.account_flag = "" + flag.charAt(0);
            this.card_flag = "" + flag.charAt(1);
        } else {
            throw new Exception("RECORD_FLAG's lenght In XML File isn't correct ");
        }
    }

    public String getCard_flag() {
        return card_flag;
    }

    public String getMessageType() {
        return (account_flag+card_flag);
    }

    public void setMessageType(String messageType) {
        this.account_flag= messageType.substring(0,1);
        this.card_flag= messageType.substring(1,2);
    }
}