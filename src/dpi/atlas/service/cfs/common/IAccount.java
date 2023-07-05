package dpi.atlas.service.cfs.common;

/**
 * Created by IntelliJ IDEA.
 * User: hi
 * Date: May 10, 2006
 * Time: 4:46:11 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IAccount {

    public String getAccountNo();

    public void setAccountNo(String accountNo);

    public long getAccountBalance();

    public void setAccountBalance(long accountBalance);
}
