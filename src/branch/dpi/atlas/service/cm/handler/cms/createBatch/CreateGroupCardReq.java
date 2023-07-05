package branch.dpi.atlas.service.cm.handler.cms.createBatch;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "POLICY_CREATE_BATCH")
public class CreateGroupCardReq {
    protected String rrn;
    protected String cardno;
    protected String row;
    protected String namefamilylatin;
    protected String editdate;
    protected String amount;
    protected String accountNo;
    protected String accountgroup;
    protected String type;
    protected String startDate;
    protected int count;
    protected int interval;
    protected String  intervalType;
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
    protected String accountType;


    public String getAccountgroup() {
        return accountgroup;
    }

    @XmlElement(name = "ACCOUNT_GROUP")
    public void setAccountgroup(String accountgroup) {
        this.accountgroup = accountgroup;
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


    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getCardno() {
        return cardno;
    }

    @XmlElement(name = "CARDNO")
    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getAmount() {
        return amount;
    }

    @XmlElement(name = "AMOUNT")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAccountNo() {
        return accountNo;
    }

    @XmlElement(name = "ACCOUNT_NO")
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getType() {
        return type;
    }

    @XmlElement(name = "CHARGE_TYPE")
    public void setType(String type) {
        this.type = type;
    }

    public String getStartDate() {
        return startDate;
    }

    @XmlElement(name = "START_DATE")
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getCount() {
        return count;
    }

    @XmlElement(name = "CHARGE_COUNT")
    public void setCount(int count) {
        this.count = count;
    }

    @XmlElement(name = "CHARGE_INTERVAL")
    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    public String getIntervalType() {
        return intervalType;
    }
    @XmlElement(name = "INTERVAL_TYPE")
    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

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

    public String getAccountType() {
        return accountType;
    }
    @XmlElement(name = "ACCOUNT_TYPE")
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}



