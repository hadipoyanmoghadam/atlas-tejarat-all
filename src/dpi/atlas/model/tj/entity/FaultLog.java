package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


/**
 * @author Hibernate CodeGenerator
 */
public class FaultLog implements Serializable {

    /**
     * identifier field
     */
    private String sessionId;

    /**
     * nullable persistent field
     */
    private String actionCode;

    public FaultLog(String sessionId, String actionCode) {
        this.sessionId = sessionId;
        this.actionCode = actionCode;
    }

    /**
     * default constructor
     */
    public FaultLog() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("sessionId", getSessionId())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof FaultLog)) return false;
        FaultLog castOther = (FaultLog) other;
        return new EqualsBuilder()
                .append(this.getSessionId(), castOther.getSessionId())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getSessionId())
                .toHashCode();
    }
}