package branch.dpi.atlas.service.cm.handler.pg.cardStatement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by sh.Behnaz on 04/29/17.
 */


@XmlType(propOrder ={"opCode","amount","crdb","date","time","docNo","acquirerId","row"})
@XmlRootElement(name = "TRANSACTION")
public class StatementTx {

    protected String opCode;
    protected String amount;
    protected String crdb;
    protected String date;
    protected String time;
    protected String docNo;
    protected String acquirerId;
    protected String row;

    public String getOpCode() {
        return opCode;
    }

    @XmlElement(name = "OPCODE")
    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public String getAmount() {
        return amount;
    }

    @XmlElement(name = "AMNT")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCrdb() {
        return crdb;
    }

    @XmlElement(name = "CRDB")
    public void setCrdb(String crdb) {
        this.crdb = crdb;
    }

    public String getDate() {
        return date;
    }

    @XmlElement(name = "DT")
    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    @XmlElement(name = "TM")
    public void setTime(String time) {
        this.time = time;
    }

    public String getDocNo() {
        return docNo;
    }

    @XmlElement(name = "DOCNO")
    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String getAcquirerId() {
        return acquirerId;
    }

    @XmlElement(name = "AQID")
    public void setAcquirerId(String acquirerId) {
        this.acquirerId = acquirerId;
    }

    public String getRow() {
        return row;
    }

    @XmlElement(name = "ROW")
    public void setRow(String row) {
        this.row = row;
    }
}


