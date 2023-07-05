package branch.dpi.atlas.service.cm.handler.pg.accountStatement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by R.Nasiri on 03/24/19.
 */


@XmlType(propOrder ={"opCode","amount","crdb","date","time","docNo","brCode","lastBal","cardNo","row"})
@XmlRootElement(name = "TRANSACTION")
public class AccountStatementTx {

    protected String opCode;
    protected String amount;
    protected String crdb;
    protected String date;
    protected String time;
    protected String docNo;
    protected String brCode;
    protected String lastBal;
    protected String cardNo;
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

    public String getBrCode() {
        return brCode;
    }

    @XmlElement(name = "BRCODE")
    public void setBrCode(String brCode) {
        this.brCode = brCode;
    }

    public String getLastBal() {
        return lastBal;
    }

    @XmlElement(name = "LASTBAL")
    public void setLastBal(String lastBal) {
        this.lastBal = lastBal;
    }

    public String getCardNo() {
        return cardNo;
    }
    @XmlElement(name = "CARDNO")
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getRow() {
        return row;
    }

    @XmlElement(name = "ROW")
    public void setRow(String row) {
        this.row = row;
    }
}


