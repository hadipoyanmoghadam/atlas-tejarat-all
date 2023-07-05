package branch.dpi.atlas.model.tj.entity;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.util.Constants;
import branch.dpi.atlas.util.ImmediateCardUtil;
import org.jpos.iso.ISOUtil;

/**
 * User: Behnaz
 * Date: Feb 6, 2012
 * Time: 9:53:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class CFDAccount {
    String account_no = "";
    String balance = "";
    String account_group = "";
    String account_status = "";
    String creation_date_account = "";
    String edit_date_account = "";
    String sgb_branch_id = "";
    String account_type = "000";
    String account_opener_name = "";
    String withdraw_type = "0";
    String account_title = "1";
    long max_trans_limit = Constants.IGNORE_MAX_TRANS_LIMIT;


    public CFDAccount() {
    }

    public String toString() {
        return " account_no=" + account_no + " balance=" + balance + " account_group=" + account_group + " account_status=" + account_status +
                " creation_date_account=" + creation_date_account + " edit_date_account=" + edit_date_account + " sgb_branch_id=" + sgb_branch_id +
                " account_type=" + account_type + " withdraw_type=" + withdraw_type + " account_opener_name=" + account_opener_name+ " account_title=" + account_title;

    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) throws Exception {
        try {
            account_no = account_no.trim(); //13 characters
            if (!ImmediateCardUtil.validateElement(account_no, BranchMessage.ACCOUNT_NO)) {
                throw new Exception();
            } else {
                this.account_no = account_no;
            }
        } catch (Exception e) {
            throw new Exception("ACCOUNT_NO " + account_no + " in XML File has Error in lenght or other things.");
        }
    }

    public String getAccount_group() {
        return account_group;
    }

    public void setAccount_group(String account_group) throws Exception {
        try {
            account_group = account_group.trim();
            if (!ImmediateCardUtil.validateElement(account_group, BranchMessage.ACCOUNT_GROUP)) {
                throw new Exception();
            } else {
                this.account_group = account_group;
            }
        } catch (Exception e) {
            throw new Exception("ACCOUNT_GROUP " + account_group + " in XML File has Error in lenght or other things.");
        }
    }

    public String getAccount_status() {
        return account_status;
    }

    public void setAccount_status(String account_status) {
        if (account_status != null) {
            account_status = account_status.trim();
            this.account_status = account_status;
        }
    }

    public String getCreation_date_account() {
        return creation_date_account;
    }

    public void setCreation_date_account(String creation_date_account) throws Exception {

        try {
            creation_date_account = creation_date_account.trim(); //6 characters
            if (!ImmediateCardUtil.validateElement(creation_date_account, BranchMessage.CREATE_DATE)) {
                throw new Exception();
            } else {
                this.creation_date_account = creation_date_account;
            }
        } catch (Exception e) {
            throw new Exception("creation_date_account " + creation_date_account + " in XML File has Error in lenght or other things.");
        }
    }

    public String getEdit_date_account() {
        return edit_date_account;
    }

    public void setEdit_date_account(String edit_date_account) throws Exception {
        try {
            edit_date_account = edit_date_account.trim(); //6 characters
            if (!ImmediateCardUtil.validateElement(edit_date_account, BranchMessage.CHANGE_DATE)) {
                throw new Exception();
            } else {
                this.edit_date_account = edit_date_account;
            }
        } catch (Exception e) {
            throw new Exception("edit_date_account " + edit_date_account + " in XML File has Error in lenght or other things.");
        }

    }

    public String getSgb_branch_id() {
        return sgb_branch_id;
    }

    public void setSgb_branch_id(String sgb_branch_id) throws Exception {
        if (ImmediateCardUtil.validateElementLessAndEqual(sgb_branch_id, BranchMessage.BRANCH_CODE)) {
            sgb_branch_id = ISOUtil.zeropad(sgb_branch_id.trim(), BranchMessage.BRANCH_CODE);
            this.sgb_branch_id = sgb_branch_id;
        } else
            throw new Exception("SGB_BRANCH_ID " + sgb_branch_id + " in XML File has Error in lenght or other things.");
    }

    public String getAccount_type() {
        return account_type;
    }

    public String getBalance() {
        return balance;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public void setBalance(String balance) {
        if (balance == null || balance.equals(""))
            balance = "0";
        this.balance = balance;
    }

    public long getMax_trans_limit() {
        return max_trans_limit;
    }

    public void setMax_trans_limit(String max_trans_limit) throws Exception {
        try {
            if (max_trans_limit != null && !max_trans_limit.trim().equals("") && ImmediateCardUtil.validateElementLessThan(max_trans_limit, 19))
                this.max_trans_limit = Long.parseLong(max_trans_limit);
        } catch (Exception e) {
            throw new Exception("MAX_TRANS_LIMIT " + max_trans_limit + " in XML File has Error in lenght or other things.");
        }
    }

    public String getAccount_opener_name() {
        return account_opener_name;
    }

    public void setAccount_opener_name(String account_opener_name) throws Exception {

        try {
            if (ImmediateCardUtil.validateElementLessAndEqual(account_opener_name, BranchMessage.ACCOUNT_OPENER_NAME))
                this.account_opener_name = account_opener_name.trim();
            else
                throw new Exception();
        } catch (Exception e) {
            throw new Exception("account_opener_name " + account_opener_name + " in XML File has Error in lenght or other things.");
        }

    }

    public String getWithdraw_type() {
        return withdraw_type;
    }

    public void setWithdraw_type(String withdraw_type) throws Exception {

        try {
            withdraw_type = withdraw_type.trim(); //1 characters
            if (!ImmediateCardUtil.validateElementLessAndEqual(withdraw_type, BranchMessage.WITHDRAW_TYPE)) {
                throw new Exception();
            } else {
                this.withdraw_type=(withdraw_type.equals("")? "0" : withdraw_type);
            }
        } catch (Exception e) {
            throw new Exception("withdraw_type " + withdraw_type + " in XML File has Error in lenght or other things.");
        }
    }

    public String getAccount_title() {
        return account_title;
    }

    public void setAccount_title(String account_title) throws Exception{
        try {
            account_title = account_title.trim(); //1 characters
            if (!ImmediateCardUtil.validateElementLessAndEqual(account_title, BranchMessage.ACCOUNT_TYPE)) {
                throw new Exception();
            } else {
                this.account_title=(account_title.equals("")? "1" : account_title);
            }
        } catch (Exception e) {
            throw new Exception("account_title " + account_title + " in XML File has Error in lenght or other things.");
        }
    }
}

