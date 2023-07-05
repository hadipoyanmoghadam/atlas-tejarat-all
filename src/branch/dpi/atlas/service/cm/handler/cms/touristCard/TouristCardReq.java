package branch.dpi.atlas.service.cm.handler.cms.touristCard;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TOURISTCARD")
public class TouristCardReq {
    protected String recordflag;
    protected String rrn;
    protected String accountno;
    protected String accountgroup;
    protected String cardno;
    protected String row;
    protected String namefamilylatin;
    protected String editdate;

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

    public String getRow() {
        return row;
    }

    @XmlElement(name = "ROW")
    public void setRow(String row) {
        this.row = row;
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
