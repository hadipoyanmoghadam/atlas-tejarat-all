package branch.dpi.atlas.service.cm.handler.cms.jointAccountInfo;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

//TODO:: Moshtarak 960426
@XmlType(propOrder = {"actioncode","desc","rrn","accountno","accountgroup","accounttype","namepersian","familypersian",
        "nationalcode","frgCodecode","fathername","sex","birthdate","idnumber","issueplace","mobileno","branchCode","respdatetime"})
@XmlRootElement(name = "JOINTACCOUNTINFO_RESPONSE")
public class JointAccountInfoResp {

    protected String actioncode;
    protected String desc;
    protected String rrn;
    protected String accountno;
    protected String accountgroup;
    protected String accounttype;
    protected String namepersian;
    protected String familypersian;
    protected String nationalcode;
    protected String frgCodecode;
    protected String fathername;
    protected String sex;
    protected String birthdate;
    protected String idnumber;
    protected String issueplace;
    protected String mobileno;
    protected String branchCode;
    protected String respdatetime;

    public String getActioncode() {
        return actioncode;
    }

    @XmlElement(name="ACTIONCODE")
    public void setActioncode(String actioncode) {
        this.actioncode = actioncode;
    }

    public String getDesc() {
        return desc;
    }

    @XmlElement(name="DESC")
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRrn() {
        return rrn;
    }
    @XmlElement(name="RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getNamepersian() {
        return namepersian;
    }
    @XmlElement(name="NAME_PERSIAN")
    public void setNamepersian(String namepersian) {
        this.namepersian = namepersian;
    }

    public String getFamilypersian() {
        return familypersian;
    }
    @XmlElement(name="FAMILY_PERSIAN")
    public void setFamilypersian(String familypersian) {
        this.familypersian = familypersian;
    }

    public String getNationalcode() {
        return nationalcode;
    }
    @XmlElement(name="NATIONALCODE")
    public void setNationalcode(String nationalcode) {
        this.nationalcode = nationalcode;
    }

    public String getFathername() {
        return fathername;
    }
    @XmlElement(name="FATHER_NAME")
    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public String getSex() {
        return sex;
    }
    @XmlElement(name="SEX")
    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthdate() {
        return birthdate;
    }
    @XmlElement(name="BIRTH_DATE")
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getIdnumber() {
        return idnumber;
    }
    @XmlElement(name="ID_NUMBER")
    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public String getIssueplace() {
        return issueplace;
    }
    @XmlElement(name="ISSUE_PLACE")
    public void setIssueplace(String issueplace) {
        this.issueplace = issueplace;
    }

    public String getMobileno() {
        return mobileno;
    }
    @XmlElement(name="MOBILE_NO")
    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getRespdatetime() {
        return respdatetime;
    }
    @XmlElement(name="RESPDATETIME")
    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }

    public String getFrgCodecode() {
        return frgCodecode;
    }
    @XmlElement(name="FRG_CODE")
    public void setFrgCodecode(String frgCodecode) {
        this.frgCodecode = frgCodecode;
    }

    public String getAccountno() {
        return accountno;
    }
    @XmlElement(name="ACCOUNT_NO")
    public void setAccountno(String accountno) {
        this.accountno = accountno;
    }

    public String getAccountgroup() {
        return accountgroup;
    }
    @XmlElement(name="ACCOUNT_GROUP")
    public void setAccountgroup(String accountgroup) {
        this.accountgroup = accountgroup;
    }

    public String getAccounttype() {
        return accounttype;
    }
    @XmlElement(name="ACCOUNT_TYPE")
    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public String getBranchCode() {
        return branchCode;
    }

    @XmlElement(name="BRANCH_CODE")
    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
}
