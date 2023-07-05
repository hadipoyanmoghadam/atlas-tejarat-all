package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Set;


/**
 * @author Hibernate CodeGenerator
 */
public class Customer implements Serializable {

    /** identifier field */
//    private Long customerPk;

    /**
     * nullable persistent field
     */
    private String customerId;

    /**
     * nullable persistent field
     */
    private String firstName;

    /**
     * nullable persistent field
     */
    private String lastName;

    /**
     * nullable persistent field
     */
    private String nid;

    /**
     * nullable persistent field
     */
    private Long bplace;

    /**
     * nullable persistent field
     */
    private String address1;

    /**
     * nullable persistent field
     */
    private String address2;

    /**
     * nullable persistent field
     */
    private String homePhone;

    /**
     * nullable persistent field
     */
    private String officePhone;

    /**
     * nullable persistent field
     */
    private String creationDate;

    /**
     * nullable persistent field
     */
    private String creationTime;

    private int templateID;

    /**
     * persistent field
     */
    private Set cards;

    /**
     * persistent field
     */
    private Set customerAccounts;

    /**
     * persistent field
     */
    private String ibCountry;

    /**
     * persistent field
     */
    private Long ibStyle;

    private String ownerIndex;

    private int statementType;

    private String origEditDate;

    private String fatherName;

    private int gender;

    private String nationalCode;

    private String birthDate;

    private String idNumber;

    private String idSerialNumber;

    private String idSeries;

    private String idIssueDate;

    private String idIssueCode;

    private String idIssuePlace;

    private String englishFirstName;

    private String englishLastName;

    private String externalIdNumber;

    private String foreignCountryCode;

    private String telNumber1;

    private String telNumber2;

    private String cellPhone;

    private String fax;

    private String postalCode;

    private int statusMelli;

    private String emailAddress;
    private String foreingCode;
    /**
     * full constructor
     */
    public Customer(String customerId, String firstName, String lastName, String nid, Long bplace, String address1, String address2, String homePhone, String officePhone, String creationDate, String creationTime, Set cards, Set customerAccounts, String ibCountry, Long ibStyle) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nid = nid;
        this.bplace = bplace;
        this.address1 = address1;
        this.address2 = address2;
        this.homePhone = homePhone;
        this.officePhone = officePhone;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.cards = cards;
        this.customerAccounts = customerAccounts;
        this.ibCountry = ibCountry;
        this.ibStyle = ibStyle;
    }

    /**
     * default constructor
     */
    public Customer() {
    }

    /**
     * minimal constructor
     */
    public Customer(Set cards, Set customerAccounts) {
        this.cards = cards;
        this.customerAccounts = customerAccounts;
    }

    public Customer(String customerId, String firstName, String lastName, String address1, String address2, String creationDate, String ownerIndex, int statementType, String origEditDate, String fatherName, int gender, String nationalCode, String birthDate, String idNumber, String idSerialNumber, String idSeries, String idIssueDate, String idIssueCode, String idIssuePlace, String englishFirstName, String englishLastName, String externalIdNumber, String foreignCountryCode, String telNumber1, String telNumber2, String cellPhone, String fax, String postalCode,int statusMelli,String emailAddress) {
        this.customerId = customerId;
        this.ownerIndex = ownerIndex;
        this.statementType = statementType;
        this.creationDate = creationDate;
        this.origEditDate = origEditDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fatherName = fatherName;
        this.gender = gender;
        this.nationalCode = nationalCode;
        this.birthDate = birthDate;
        this.idNumber = idNumber;
        this.idSerialNumber = idSerialNumber;
        this.idSeries = idSeries;
        this.idIssueDate = idIssueDate;
        this.idIssueCode = idIssueCode;
        this.idIssuePlace = idIssuePlace;
        this.englishFirstName = englishFirstName;
        this.englishLastName = englishLastName;
        this.externalIdNumber = externalIdNumber;
        this.foreignCountryCode = foreignCountryCode;
        this.telNumber1 = telNumber1;
        this.telNumber2 = telNumber2;
        this.cellPhone = cellPhone;
        this.fax = fax;
        this.address1 = address1;
        this.address2 = address2;
        this.postalCode = postalCode;
        this.statusMelli=statusMelli;
        this.emailAddress=emailAddress;
    }

//    public Long getCustomerPk() {
//        return this.customerPk;
//    }
//
//    public void setCustomerPk(Long customerPk) {
//        this.customerPk = customerPk;
//    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNid() {
        return this.nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public Long getBplace() {
        return bplace;
    }

    public void setBplace(Long bplace) {
        this.bplace = bplace;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public Set getCards() {
        return this.cards;
    }

    public void setCards(Set cards) {
        this.cards = cards;
    }

    public Set getCustomerAccounts() {
        return this.customerAccounts;
    }

    public void setCustomerAccounts(Set customerAccounts) {
        this.customerAccounts = customerAccounts;
    }

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templteID) {
        this.templateID = templteID;
    }

    public String getIbCountry() {
        return ibCountry;
    }

    public void setIbCountry(String ibCountry) {
        this.ibCountry = ibCountry;
    }

    public Long getIbStyle() {
        return ibStyle;
    }

    public void setIbStyle(Long ibStyle) {
        this.ibStyle = ibStyle;
    }

    public String getOwnerIndex() {
        return ownerIndex;
    }

    public void setOwnerIndex(String ownerIndex) {
        this.ownerIndex = ownerIndex;
    }

    public int getStatementType() {
        return statementType;
    }

    public void setStatementType(int statementType) {
        this.statementType = statementType;
    }

    public String getOrigEditDate() {
        return origEditDate;
    }

    public void setOrigEditDate(String origEditDate) {
        this.origEditDate = origEditDate;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdSerialNumber() {
        return idSerialNumber;
    }

    public void setIdSerialNumber(String idSerialNumber) {
        this.idSerialNumber = idSerialNumber;
    }

    public String getIdSeries() {
        return idSeries;
    }

    public void setIdSeries(String idSeries) {
        this.idSeries = idSeries;
    }

    public String getIdIssueDate() {
        return idIssueDate;
    }

    public void setIdIssueDate(String idIssueDate) {
        this.idIssueDate = idIssueDate;
    }

    public String getIdIssuePlace() {
        return idIssuePlace;
    }

    public void setIdIssuePlace(String idIssuePlace) {
        this.idIssuePlace = idIssuePlace;
    }

    public String getIdIssueCode() {
        return idIssueCode;
    }

    public void setIdIssueCode(String idIssueCode) {
        this.idIssueCode = idIssueCode;
    }

    public String getEnglishFirstName() {
        return englishFirstName;
    }

    public void setEnglishFirstName(String englishFirstName) {
        this.englishFirstName = englishFirstName;
    }

    public String getEnglishLastName() {
        return englishLastName;
    }

    public void setEnglishLastName(String englishLastName) {
        this.englishLastName = englishLastName;
    }

    public String getExternalIdNumber() {
        return externalIdNumber;
    }

    public void setExternalIdNumber(String externalIdNumber) {
        this.externalIdNumber = externalIdNumber;
    }

    public String getForeignCountryCode() {
        return foreignCountryCode;
    }

    public void setForeignCountryCode(String foreignCountryCode) {
        this.foreignCountryCode = foreignCountryCode;
    }

    public String getTelNumber1() {
        return telNumber1;
    }

    public void setTelNumber1(String telNumber1) {
        this.telNumber1 = telNumber1;
    }

    public String getTelNumber2() {
        return telNumber2;
    }

    public void setTelNumber2(String telNumber2) {
        this.telNumber2 = telNumber2;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public int getStatusMelli() {
        return statusMelli;
    }

    public void setStatusMelli(int statusMelli) {
        this.statusMelli = statusMelli;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getForeingCode() {
        return foreingCode;
    }

    public void setForeingCode(String foreingCode) {
        this.foreingCode = foreingCode;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("customerId", getCustomerId())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof Customer)) return false;
        Customer castOther = (Customer) other;
        return new EqualsBuilder()
                .append(this.getCustomerId(), castOther.getCustomerId())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getCustomerId())
                .toHashCode();
    }

}
