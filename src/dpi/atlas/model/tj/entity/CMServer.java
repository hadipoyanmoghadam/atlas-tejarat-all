package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.crypto.SecretKey;
import java.io.Serializable;


public class CMServer implements Serializable {

    private long ID;
    private String serverUser;
    private String serverPwd;
    private long status;
    private String IP;
    private String currentState;
    private String state1;
    private String state2;
    private long sourceId;
    private  SecretKey key;

    public CMServer() {
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getServerUser() {
        return serverUser;
    }

    public void setServerUser(String serverUser) {
        this.serverUser = serverUser;
    }

    public String getServerPwd() {
        return serverPwd;
    }

    public void setServerPwd(String serverPwd) {
        this.serverPwd = serverPwd;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getState1() {
        return state1;
    }

    public void setState1(String state1) {
        this.state1 = state1;
    }

    public String getState2() {
        return state2;
    }

    public void setState2(String state2) {
        this.state2 = state2;
    }

    public SecretKey getKey() {
        return key;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof CMServer)) return false;
        CMServer castOther = (CMServer) other;
        return new EqualsBuilder()
                .append(this.getID(), castOther.getID())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getID())
                .append(getIP())
                .toHashCode();
    }
}
