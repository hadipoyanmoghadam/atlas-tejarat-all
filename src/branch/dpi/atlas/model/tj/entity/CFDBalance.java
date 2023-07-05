package branch.dpi.atlas.model.tj.entity;


/**
 * User: R.Nasiri
 * Date: june 25, 2013
 * Time: 08:49:22 AM
 */
public class CFDBalance extends CFDFinancialRequest {

    public CFDBalance() {
    }

    public String toString() {
        return " account_no=" + src_account_no + " branch_code=" + branch_code + " creation_date=" + creation_date;
    }
}