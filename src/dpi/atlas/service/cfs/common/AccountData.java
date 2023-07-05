package dpi.atlas.service.cfs.common;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Apr 26, 2006
 * Time: 3:25:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountData {
    public static final int ACCOUNT_CFS_CUSTOMER = 1;
    public static final int ACCOUNT_CFS_IMD = 2;
    public static final int ACCOUNT_CFS_DEVICE = 3;
    public static final int ACCOUNT_CFS_UN_DEFINED = 4;
    public static final int ACCOUNT_CFS_BRANCH = 5;

    public static final int ACCOUNT_CREDIT = 1;
    public static final int ACCOUNT_DEBIT = 2;

    public static final String ACCOUNT_CREDIT_STRING = "C";
    public static final String ACCOUNT_DEBIT_STRING = "D";

    public static final int ACCOUNT_BOTH_CREDIT_DEBIT = 3;

    private long totalBlockedAmount;

    String accountNo;
    int type;
    int debitCredit;
    private String accountGroup;
    long accountBalance;
    long minBalance = 0L;
    Object accountHolder;
    String accountType = null;
    String customerId = null;


    public AccountData(String accountNo, int type, int debitCredit, Object accountHolder) {
        this.accountNo = accountNo;
        this.type = type;
        this.debitCredit = debitCredit;
        this.accountHolder = accountHolder;
    }

    public void setDebitCredit(int debitCredit) {
        this.debitCredit = debitCredit;
    }

    public AccountData(String accountNo, int type, int debitCredit, long accountBalance, String accountGroup, Object accountHolder) {
        this.accountNo = accountNo;
        this.type = type;
        this.debitCredit = debitCredit;
        this.accountBalance = accountBalance;
        this.accountHolder = accountHolder;
        this.accountGroup = accountGroup;
    }

    public AccountData(String accountNo, int type, int debitCredit, long accountBalance, String accountGroup, long totalBlockedAmount , Object accountHolder) {
        this.accountNo = accountNo;
        this.type = type;
        this.debitCredit = debitCredit;
        this.accountBalance = accountBalance;
        this.accountHolder = accountHolder;
        this.accountGroup = accountGroup;
        this.totalBlockedAmount = totalBlockedAmount;
    }


    public String getAccountGroup() {
        return accountGroup;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public int getType() {
        return type;
    }

    public int getDebitCredit() {
        return debitCredit;
    }

    public Object getAccountHolder() {
        return accountHolder;
    }

    public long getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(long accountBalance) {
        this.accountBalance = accountBalance;
    }


    public void setMinBalance(long minBalance) {
        this.minBalance = minBalance;
    }


    public long getMinBalance() {
        return minBalance;
    }


    public long getTotalBlockedAmount() {
        return totalBlockedAmount;
    }
}
