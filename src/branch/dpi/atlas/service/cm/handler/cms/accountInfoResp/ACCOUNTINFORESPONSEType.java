package branch.dpi.atlas.service.cm.handler.cms.accountInfoResp;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.xml.bind.annotation.*;

                                           //todo 970928

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ACCOUNTINFO_RESPONSE")
public class ACCOUNTINFORESPONSEType {

    @XmlPath("ACTIONCODE/text()")
    protected String actioncode;
    @XmlPath("DESC/text()")
    protected String desc;
    @XmlPath("RRN/text()")
    protected String rrn;
    @XmlPath("ACCOUNT_NO/text()")
    protected String accountno;
    @XmlPath("ACCOUNT_GROUP/text()")
    protected String accountgroup;
    @XmlPath("ACCOUNT_TYPE/text()")
    protected String accounttype;
    @XmlPath("NAME_PERSIAN/text()")
    protected String namepersian;
    @XmlPath("FAMILY_PERSIAN/text()")
    protected String familypersian;
    @XmlPath("NATIONALCODE/text()")
    protected String nationalcode;
    @XmlPath("FRG_CODE/text()")
    protected String frgCodecode;
    @XmlPath("FATHER_NAME/text()")
    protected String fathername;
    @XmlPath("SEX/text()")
    protected String sex;
    @XmlPath("BIRTH_DATE/text()")
    protected String birthdate;
    @XmlPath("ID_NUMBER/text()")
    protected String idnumber;
    @XmlPath("ISSUE_PLACE/text()")
    protected String issueplace;
    @XmlPath("MOBILE_NO/text()")
    protected String mobileno;
    @XmlPath("CARD_TYPE/text()")
    protected String cardType;
    @XmlPath("BRANCH_CODE/text()")
    protected String branchCode;
    @XmlPath("RESPDATETIME/text()")
    protected String respdatetime;


    public String getActioncode() {
        return actioncode;
    }

    public void setActioncode(String actioncode) {
        this.actioncode = actioncode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public String getNamepersian() {
        return namepersian;
    }

    public void setNamepersian(String namepersian) {
        this.namepersian = namepersian;
    }

    public String getFamilypersian() {
        return familypersian;
    }

    public void setFamilypersian(String familypersian) {
        this.familypersian = familypersian;
    }

    public String getNationalcode() {
        return nationalcode;
    }

    public void setNationalcode(String nationalcode) {
        this.nationalcode = nationalcode;
    }

    public String getFathername() {
        return fathername;
    }

    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public String getIssueplace() {
        return issueplace;
    }

    public void setIssueplace(String issueplace) {
        this.issueplace = issueplace;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getRespdatetime() {
        return respdatetime;
    }

    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }

    public String getFrgCodecode() {
        return frgCodecode;
    }

    public void setFrgCodecode(String frgCodecode) {
        this.frgCodecode = frgCodecode;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
}
