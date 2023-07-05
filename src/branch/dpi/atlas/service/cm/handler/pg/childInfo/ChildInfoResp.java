package branch.dpi.atlas.service.cm.handler.pg.childInfo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"actioncode", "desc", "rrn", "cardno", "accountno", "name", "family", "nationalCode", "frgCode",
        "availableBalance", "availableBalanceSign", "type", "amount", "chargeDate", "efectiveDate", "respdatetime"})
@XmlRootElement(name = "CHILDINFO_RESPONSE")
public class ChildInfoResp {

    protected String actioncode;
    protected String desc;
    protected String rrn;
    protected String cardno;
    protected String respdatetime;
    protected String accountno;
    protected String name;
    protected String family;
    protected String nationalCode;
    protected String frgCode;
    protected String availableBalance;
    protected String availableBalanceSign;
    protected String type;
    protected String amount;
    protected String chargeDate;
    protected String efectiveDate;


    public String getAccountno() {
        return accountno;
    }

    @XmlElement(name = "ACCOUNT_NO")
    public void setAccountno(String accountno) {
        this.accountno = accountno;
    }

    public String getAmount() {
        return amount;
    }

    @XmlElement(name = "AMOUNT")
    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getRespdatetime() {
        return respdatetime;
    }

    @XmlElement(name = "RESPDATETIME")
    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
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

    public String getAvailableBalance() {
        return availableBalance;
    }

    @XmlElement(name = "AVAILABLEBALANCE")
    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    public String getAvailableBalanceSign() {
        return availableBalanceSign;
    }

    @XmlElement(name = "AVAILABLEBALANCE_SIGN")
    public void setAvailableBalanceSign(String availableBalanceSign) {
        this.availableBalanceSign = availableBalanceSign;
    }

    public String getType() {
        return type;
    }

    @XmlElement(name = "CHARGE_TYPE")
    public void setType(String type) {
        this.type = type;
    }

    public String getChargeDate() {
        return chargeDate;
    }

    @XmlElement(name = "CHARGE_DATE")
    public void setChargeDate(String chargeDate) {
        this.chargeDate = chargeDate;
    }

    public String getEfectiveDate() {
        return efectiveDate;
    }

    @XmlElement(name = "EFECTIVE_DATE")
    public void setEfectiveDate(String efectiveDate) {
        this.efectiveDate = efectiveDate;
    }

}
