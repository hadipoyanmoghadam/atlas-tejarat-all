package dpi.atlas.model.tj.entity;

/**
 * @author Amir hosein Ahmadi.
 *         Date: Jun 13, 2007
 *         Time: 5:53:21 PM
 *         M.S. Student of Sharif University
 */

import dpi.atlas.service.cfs.common.CFSConstants;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;

public class SAFLog
        implements Serializable {

    public static final String SEQUENCE_NAME = "tbSAFLog";
    public static final String SAF_ISO = "ISO";
    public static final String SAF_OFS = "OFS";
    public static final int STATUS_UNHANDLED = 0;
    public static final int STATUS_HANDLED = 1;
    public static final int STATUS_UNDELIVERED = 2;
    public static final int STATUS_NOTAPPROVED = 3;
    private long sequencer;
    private String SAFName;
    private Timestamp insertDate;
    private String msgString;
    private int SAFPriority;
    private int msgPriority;
    private int tryCount;
    private Timestamp lastTryDate;
    private int handled;
    private int maxTryCount;
    private int waitTime;
    public static final int MAX_TRYCOUNT_OFS = 10;
    private Integer partNo;

    public SAFLog() {
        handled = 0;
        maxTryCount = 0;
        waitTime = 0;
        int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.PARTITION_SIZE;
        if (part == 0)
            part = CFSConstants.PARTITION_SIZE;
        partNo = new Integer(part);
    }

    public boolean issHandled() {
        return handled != 0;
    }

    public int getHandled() {
        return handled;
    }

    public void setHandled(int handled) {
        this.handled = handled;
    }

    public long getSequencer() {
        return sequencer;
    }

    public void setSequencer(long sequencer) {
        this.sequencer = sequencer;
    }

    public String getSAFName() {
        return SAFName;
    }

    public void setSAFName(String SAFName) {
        this.SAFName = SAFName;
    }

    public Timestamp getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Timestamp insertDate) {
        this.insertDate = insertDate;
    }

    public String getMsgString() {
        return msgString;
    }

    public void setMsgString(String msgString) {
        this.msgString = msgString;
    }

    public int getSAFPriority() {
        return SAFPriority;
    }

    public void setSAFPriority(int SAFPriority) {
        this.SAFPriority = SAFPriority;
    }

    public int getMsgPriority() {
        return msgPriority;
    }

    public void setMsgPriority(int msgPriority) {
        this.msgPriority = msgPriority;
    }

    public int getTryCount() {
        return tryCount;
    }

    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    public Timestamp getLastTryDate() {
        return lastTryDate;
    }

    public void setLastTryDate(Timestamp lastTryDate) {
        this.lastTryDate = lastTryDate;
    }

    public void incTryCount() {
        tryCount++;
    }

    public int getMaxTryCount() {
        return maxTryCount;
    }

    public void setMaxTryCount(int maxTryCount) {
        this.maxTryCount = maxTryCount;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }


    public Integer getPartNo() {
        return partNo;
    }

    public void setPartNo(Integer partNo) {
        this.partNo = partNo;
    }
}