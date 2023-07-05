package dpi.atlas.model.tj.entity;

import dpi.atlas.service.cfs.common.CFSConstants;

import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;

import org.jpos.iso.ISOUtil;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Created by Behnaz (EBanking 87/02/17).
 * To change this template use File | Settings | File Templates.
 */
public class IntermediateAccount implements Serializable {

    private Long id;
    private String channelId;
    private String accountNo;
    private int hostID;
    private int destHostID;



    /**
     * full constructor
     */
    public IntermediateAccount(Long id, String accountNo,String channelId,int hostId,int destHostId ) {
        this.channelId=channelId;
        this.id=id;
        this.accountNo=accountNo;
        this.hostID=hostId;
        this.destHostID=destHostId;
    }

    /**
     * default constructor
     */
    public IntermediateAccount() {
    }

    /**
     * minimal constructor
     */
    public IntermediateAccount(String channelId,int hostId,int destHostId) {
        this.channelId=channelId;
        this.hostID=hostId;
        this.destHostID=destHostId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getHostID() {
        return hostID;
    }

    public void setHostID(int hostID) {
        this.hostID = hostID;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public int getDestHostID() {
        return destHostID;
    }

    public void setDestHostID(int destHostID) {
        this.destHostID = destHostID;
    }

    public String toString() {
        return " accountNo = "+accountNo+" hostID = "+hostID+" DestHostID = "+destHostID+" channelId = "+channelId+" id = "+id;

    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof IntermediateAccount)) return false;
        IntermediateAccount castOther = (IntermediateAccount) other;
        return new EqualsBuilder()
//                .append(this.getLogId(), castOther.getLogId())
                .isEquals();
    }


    public int hashCode() {
        return new HashCodeBuilder()
//                .append(getLogId())
                .toHashCode();
    }

}
