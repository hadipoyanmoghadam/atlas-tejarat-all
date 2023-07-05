package branch.dpi.atlas.service.cm.handler.cms.customer;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import javax.xml.bind.annotation.*;
                                              //todo 970928

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CUSTOMER")
public class CUSTOMERType {
    @XmlPath( "RECORD_FLAG/text()")
    protected String recordflag;
    @XmlPath( "RRN/text()")
    protected String rrn;
    @XmlPath( "ACCOUNT_NO/text()")
    protected String accountno;
    @XmlPath( "ACCOUNT_GROUP/text()")
    protected String accountgroup;
    @XmlPath( "CARDNO/text()")
    protected String cardno;
    @XmlPath( "ROW/text()")
    protected String row;
    @XmlPath( "NAME_FAMILY_LATIN/text()")
    protected String namefamilylatin;
    @XmlPath( "EDIT_DATE/text()")
    protected String editdate;
    @XmlPath( "CARD_TYPE/text()")
    protected String cardType;

    public String getRecordflag() {
        return recordflag;
    }

    public void setRecordflag(String recordflag) {
        this.recordflag = recordflag;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getAccountno() {
        return accountno;
    }

    public void setAccountno(String accountno) {
        this.accountno = accountno;
    }

    public String getAccountgroup() {
        return accountgroup;
    }

    public void setAccountgroup(String accountgroup) {
        this.accountgroup = accountgroup;
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getNamefamilylatin() {
        return namefamilylatin;
    }

    public void setNamefamilylatin(String namefamilylatin) {
        this.namefamilylatin = namefamilylatin;
    }

    public String getEditdate() {
        return editdate;
    }

    public void setEditdate(String editdate) {
        this.editdate = editdate;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
