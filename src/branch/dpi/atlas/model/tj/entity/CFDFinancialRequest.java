package branch.dpi.atlas.model.tj.entity;

import dpi.atlas.model.tj.entity.Account;
import branch.dpi.atlas.util.ImmediateCardUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;

/**
 * User: Behnaz
 * Date: September  22, 2013
 * Time: 9:53:34 AM
 */
public class CFDFinancialRequest extends CFDRequestBase {
    private static Log log = LogFactory.getLog(Account.class);

    String sequence = "0";
    String src_account_no = "";
    String branch_code = "";
    String creation_date = "";
    String creation_time = "";
    String desc = "";
    
    public CFDFinancialRequest() {
    }

    public String toString() {
        return " src_account_no=" + src_account_no +
                " branch_code=" + branch_code +
                " creation_date=" + creation_date;
    }

    public String getSrc_account_no() {
        return src_account_no;
    }

    public void setSrc_account_no(String src_account_no) throws Exception {
        try {
            src_account_no = src_account_no.trim(); //13 characters
            if (!ImmediateCardUtil.validateElementNotZero(src_account_no, 13)) {
                throw new Exception();
            } else {
                this.src_account_no = src_account_no;
            }
        } catch (Exception e) {
            throw new Exception("SRC_ACCOUNT_NO " + src_account_no + " in XML File has Error in lenght or other things.");
        }

    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        if (sequence != null) {
            sequence = sequence.trim();
            this.sequence = sequence;
        }
    }


    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        if (creation_date != null) {
            creation_date = creation_date.trim();
            this.creation_date = creation_date;
        }
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        if (creation_time != null) {
            creation_time = creation_time.trim();
            this.creation_time = creation_time;
        }
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBranch_code() {
        return branch_code;
    }

    public void setBranch_code(String branch_code) throws Exception {
        try {

            if (ImmediateCardUtil.validateElementLessThan(branch_code, 7)) {
                branch_code = ISOUtil.zeropad(branch_code.trim(), 6);
                this.branch_code = branch_code;
            } else
                throw new Exception("branch_code " + branch_code + " in XML File has Error in lenght or other things.");

        } catch (ISOException e) {
            log.error(e);
            throw e;
        }
    }
}