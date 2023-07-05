package branch.dpi.atlas.service.cm.handler.cms.childCard;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CHILDCARD")
public class ChildCardReq {
    protected String recordflag;
    protected String rrn;
    protected String accountno;
    protected String accountgroup;
    protected String cardno;
    protected String row;
    protected String namefamilylatin;
    protected String editdate;
    protected String name;
    protected String family;
    protected String nationalCode;
    protected String frgCode;
    protected String fatherName;
    protected String sex;
    protected String birthDate;
    protected String IDNumber;
    protected String issuePlace;
    protected String mobileNo;
    protected String account_type;

    public String getName() {
        return name;
    }
    @XmlElement(name = "NAME_PERSIAN")
    public void setName(String name) {
        this.name = name;
    }
    public String getFamily() {
        return family;
    }
    @XmlElement(name = "FAMILY_PERSIAN")
    public void setFamily(String family) {
        this.family = family;
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

    public String getFatherName() {
        return fatherName;
    }
    @XmlElement(name = "FATHER_NAME")
    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getSex() {
        return sex;
    }
    @XmlElement(name = "SEX")
    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthDate() {
        return birthDate;
    }
    @XmlElement(name = "BIRTH_DATE")
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getIDNumber() {
        return IDNumber;
    }
    @XmlElement(name = "ID_NUMBER")
    public void setIDNumber(String IDNumber) {
        this.IDNumber = IDNumber;
    }

    public String getIssuePlace() {
        return issuePlace;
    }
    @XmlElement(name = "ISSUE_PLACE")
    public void setIssuePlace(String issuePlace) {
        this.issuePlace = issuePlace;
    }

    public String getMobileNo() {
        return mobileNo;
    }
    @XmlElement(name = "MOBILE_NO")
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

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

    public String getAccount_type() {
        return account_type;
    }

    @XmlElement(name = "ACCOUNT_TYPE")
    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }
}
