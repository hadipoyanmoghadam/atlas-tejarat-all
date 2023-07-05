package branch.dpi.atlas.model.tj.entity;

import branch.dpi.atlas.util.ImmediateCardUtil;

/**
 * User: Behnaz
 * Date: ?October  14, 2012
 * Time: 9:53:34 AM
 */
public class CFDGiftDeposit extends CFDFinancialRequest {

    String dest_account_no = "";
    String amount = "";
    String branch_docNo = "";

    public CFDGiftDeposit() {
    }

    public String toString() {
        return " src_account_no=" + src_account_no + " dest_account_no=" + dest_account_no +
                " amount=" + amount + " branch_code=" + branch_code + " branch_docNo=" + branch_docNo +
                " creation_date=" + creation_date;
    }

    public String getDest_account_no() {
        return dest_account_no;
    }

    public void setDest_account_no(String dest_account_no) throws Exception {
        try {
            dest_account_no = dest_account_no.trim(); //13 characters
            if (!ImmediateCardUtil.validateElementNotZero(dest_account_no, 13)) {
                throw new Exception();
            } else {
                this.dest_account_no = dest_account_no;
            }
        } catch (Exception e) {
            throw new Exception("DEST_ACCOUNT_NO " + dest_account_no + " in XML File has Error in lenght or other things.");
        }

    }

    public String getBranch_docNo() {
        return branch_docNo;
    }

    public void setBranch_docNo(String branch_docNo) throws Exception {
        try {
            branch_docNo = branch_docNo.trim(); //7 characters
            if (!ImmediateCardUtil.validateElement(branch_docNo, 6)) {
                throw new Exception();
            } else {
                this.branch_docNo = branch_docNo;
            }
        } catch (Exception e) {
            throw new Exception("branch_docNo " + branch_docNo + " in XML File has Error in lenght or other things.");
        }
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) throws Exception {

        try {
            amount = amount.trim();
            if (amount.equals("0") || amount.equals("") || !ImmediateCardUtil.validateElementLessThan(amount, 18)) {
                throw new Exception();
            } else {
                this.amount = amount;
            }
        } catch (Exception e) {
            throw new Exception("amount " + amount + " in XML File has Error in lenght or other things.");
        }
    }
}