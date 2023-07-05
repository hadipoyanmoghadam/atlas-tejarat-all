package branch.dpi.atlas.service.cm.handler.pg.cardStatement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by sh.Behnaz on 03/29/17.
 */


@XmlType(propOrder ={"actioncode","desc","rrn","cardno","fromDate","toDate","transCount","tx","respdatetime"})
@XmlRootElement(name = "STATEMENT_GROUPCARD_RESPONSE")
public class CardStatementResp {

    protected String rrn;
    protected String cardno;
    protected String desc;
    protected String actioncode;
    protected String respdatetime;
    protected String fromDate;
    protected String toDate;
    protected String transCount;
    protected StatementTxList tx;


    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getCardno() {
        return cardno;
    }

    @XmlElement(name = "CARDNO")
    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getActioncode() {
        return actioncode;
    }

    @XmlElement(name = "ACTIONCODE")
    public void setActioncode(String actioncode) {
        this.actioncode = actioncode;
    }

    public String getDesc() {
        return desc;
    }

    @XmlElement(name = "DESC")
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRespdatetime() {
        return respdatetime;
    }

    @XmlElement(name = "RESPDATETIME")
    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }

    public String getFromDate() {
        return fromDate;
    }
    @XmlElement(name = "FROMDATE")
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }
    @XmlElement(name = "TODATE")
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getTransCount() {
        return transCount;
    }
    @XmlElement(name = "TRANSCOUNT")
    public void setTransCount(String transCount) {
        this.transCount = transCount;
    }

    public StatementTxList getTx() {
        return tx;
    }
    @XmlElement(name = "TRANSACTIONS")
    public void setTx(StatementTxList tx) {
        this.tx = tx;
    }
}



