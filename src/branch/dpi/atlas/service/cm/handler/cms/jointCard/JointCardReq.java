package branch.dpi.atlas.service.cm.handler.cms.jointCard;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//TODO:: Moshtarak 960426
@XmlRootElement(name = "JOINTACCOUNT")
public class JointCardReq {
    protected String recordflag;
    protected String rrn;
    protected String accountno;
    protected String accountgroup;
    protected String cardno;
    protected String namefamilylatin;
    protected String editdate;
    protected String nationalCode;
    protected String frgCode;

    public String getRecordflag() {
        return recordflag;
    }

    @XmlElement(name = "RECORD_FLAG")
    public void setRecordflag(String recordflag) {
        this.recordflag = recordflag;
    }

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

    public String getAccountgroup() {
        return accountgroup;
    }

    @XmlElement(name = "ACCOUNT_GROUP")
    public void setAccountgroup(String accountgroup) {
        this.accountgroup = accountgroup;
    }

    public String getCardno() {
        return cardno;
    }

    @XmlElement(name = "CARDNO")
    public void setCardno(String cardno) {
        this.cardno = cardno;
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

    public String getNamefamilylatin() {
        return namefamilylatin;
    }

    @XmlElement(name = "NAME_FAMILY_LATIN")
    public void setNamefamilylatin(String namefamilylatin) {
        this.namefamilylatin = namefamilylatin;
    }

    public String getEditdate() {
        return editdate;
    }

    @XmlElement(name = "EDIT_DATE")
    public void setEditdate(String editdate) {
        this.editdate = editdate;
    }
}
