package branch.dpi.atlas.service.cm.handler.pg.wfp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by sh.Behnaz on 7/1/19.
 */


@XmlType(propOrder ={"amount","date","time","docNo","opCode","branchCode","lastBal"})
@XmlRootElement(name = "TRANSACTION")
public class WFPTransaction {

    protected String amount;
    protected String date;
    protected String time;
    protected String docNo;
    protected String opCode;
    protected String branchCode;
    protected String lastBal;


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

    public String getAmount() {
        return amount;
    }
    @XmlElement(name = "AMNT")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOpCode() {
        return opCode;
    }
    @XmlElement(name = "OPCODE")
    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }
    public String getBranchCode() {
        return branchCode;
    }
    @XmlElement(name = "BRANCHCODE")
    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getLastBal() {
        return lastBal;
    }
    @XmlElement(name = "LASTBAL")
    public void setLastBal(String lastBal) {
        this.lastBal = lastBal;
    }
}


