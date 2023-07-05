package branch.dpi.atlas.service.cm.handler.pg.chargeReport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by sh.Behnaz on 8/27/18.
 */


@XmlType(propOrder ={"amount","date","time","docNo"})
@XmlRootElement(name = "TRANSACTION")
public class Transaction {

    protected String amount;
    protected String date;
    protected String time;
    protected String docNo;
//    protected String crdb;

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

//    public String getCrdb() {
//        return crdb;
//    }
//
//    public void setCrdb(String crdb) {
//        this.crdb = crdb;
//    }
}


