package branch.dpi.atlas.service.cm.handler.pg.stockDeposit;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by R.Nasiri on 02/04/2020.
 */


@XmlType(propOrder ={"actioncode","desc","rrn","respdatetime"})
@XmlRootElement(name = "STOCK_DEPOSIT_RESPONSE")
public class StockDepositResp {

    protected String actioncode;
    protected String desc;
    protected String rrn;
    protected String respdatetime;


    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
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
}



