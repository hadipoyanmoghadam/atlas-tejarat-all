package branch.dpi.atlas.service.cm.handler.cms.jointAccountInfo;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//TODO:: Moshtarak 960426
@XmlRootElement(name = "JOINTACCOUNTINFO")
public class JointAccountInfoReq {

    protected String rrn;
    protected String accountno;
    protected String nationalCode;
    protected String frgCode;


    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getAccountno() {
        return accountno;
    }

    @XmlElement(name = "ACCOUNT_NO")
    public void setAccountno(String accountno) {
        this.accountno = accountno;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    @XmlElement(name = "NATIONALCODE")
    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getFrgCode() {
        return frgCode;
    }

    @XmlElement(name = "FRG_CODE")
    public void setFrgCode(String frgCode) {
        this.frgCode = frgCode;
    }

}
