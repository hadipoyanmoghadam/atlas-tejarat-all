package dpi.atlas.service.cfs.job.refresh;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Feb 2, 2008
 * Time: 11:40:13 AM
 * To change this template use File | Settings | File Templates.
 */
public interface RefreshJobMBean {

    void setRefreshTime(String timeToBeingUpdated);

    String getRefreshTime();


    void setRefreshDate(String dateToBeingUpdated);

    String getRefreshDate();


    boolean getAccountTypeMinBalance();

    void setAccountTypeMinBalance(boolean isAccountTypeMinBalance);


    boolean getHostLockStatus();

    void setHostLockStatus(boolean isHostLockStatus);


    boolean getCustomerTmplSrvTxValue();

    void setCustomerTmplSrvTxValue(boolean isCustomerTmplSrvTxValue);


    boolean getDevice();

    void setDevice(boolean isDevice);


    boolean getTxTypeSgbTxCode();

    void setTxTypeSgbTxCode(boolean isTxTypeSgbTxCode);


    boolean getBatch();

    void setBatch(boolean isBatch);


    boolean getImd();

    void setImd(boolean isImd);


    boolean getCmServerByID();

    void setCmServerByID(boolean isCmServerByID);


}
