package branch.dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;


/**
 * User: R.Nasiri
 * Date: Agu 02, 2020
 * Time: 16:30 PM
 */
public class PayaRequest implements Serializable {

    private String branchSend;
    private String serial;
    private String dueDate;
    private String effectiveDate;
    private String createDate;
    private int cycleNo;
    private int step;
    private int statusX;
    private String createUser;
    private String updateUser;
    private String bankSend;
    private String bankRecv;
    private long amount;
    private String sourceAcc;
    private String sourceIban;
    private String paymentCode;
    private String destIban;
    private String nameSend;
    private String senderMeliCode;
    private String senderpostalCode;
    private String senderAddress;
    private String senderTell;
    private String receiverName;
    private String refId;
    private String description;
    private String expCode;
    private String expDescription;
    private int opId;
    private long startTrack;
    private long endTrack;
    private String senderShahab;
    private String reciverShahab;
    private String reciverMeliCode;
    private String reciverPostalCode;
    private String reason;

    public PayaRequest(String serial, String dueDate, String bankSend, String bankRecv,
                       long amount, String createDate, String sourceIban, String paymentCode,
                       String destIban, String nameSend, String senderMeliCode, String senderpostalCode,
                       String senderAddress, String senderTell, String receiverName, String description,
                       String senderShahab,String reciverShahab,String reciverMeliCode,String reciverPostalCode,
                       String reason) {
        this.serial = serial;
        this.dueDate = dueDate;
        this.bankSend = bankSend;
        this.bankRecv = bankRecv;
        this.amount = amount;
        this.createDate = createDate;
        this.sourceIban = sourceIban;
        this.paymentCode = paymentCode;
        this.destIban = destIban;
        this.nameSend = nameSend;
        this.senderMeliCode = senderMeliCode;
        this.senderpostalCode = senderpostalCode;
        this.senderAddress = senderAddress;
        this.senderTell = senderTell;
        this.receiverName = receiverName;
        this.description = description;
        this.senderShahab = senderShahab;
        this.reciverShahab = reciverShahab;
        this.reciverMeliCode = reciverMeliCode;
        this.reciverPostalCode = reciverPostalCode;
        this.reason = reason;
    }

    public PayaRequest(String branchSend, String dueDate,String createDate, String bankSend, String bankRecv,
                       long amount, String sourceIban, String paymentCode,
                       String destIban, String nameSend, String senderMeliCode, String senderpostalCode,
                       String senderAddress, String senderTell, String receiverName, String serial,
                       String senderShahab,String reciverShahab,String reciverMeliCode,String reciverPostalCode,
                       String reason) {
        this.branchSend = branchSend;
        this.dueDate = dueDate;
        this.createDate = createDate;
        this.bankSend = bankSend;
        this.bankRecv = bankRecv;
        this.amount = amount;
        this.sourceIban = sourceIban;
        this.paymentCode = paymentCode;
        this.destIban = destIban;
        this.nameSend = nameSend;
        this.senderMeliCode = senderMeliCode;
        this.senderpostalCode = senderpostalCode;
        this.senderAddress = senderAddress;
        this.senderTell = senderTell;
        this.receiverName = receiverName;
        this.serial = serial;
        this.senderShahab = senderShahab;
        this.reciverShahab = reciverShahab;
        this.reciverMeliCode = reciverMeliCode;
        this.reciverPostalCode = reciverPostalCode;
        this.reason = reason;
    }

    public String getBranchSend() {
        return branchSend;
    }

    public void setBranchSend(String branchSend) {
        this.branchSend = branchSend;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getCycleNo() {
        return cycleNo;
    }

    public void setCycleNo(int cycleNo) {
        this.cycleNo = cycleNo;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getStatusX() {
        return statusX;
    }

    public void setStatusX(int statusX) {
        this.statusX = statusX;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getBankSend() {
        return bankSend;
    }

    public void setBankSend(String bankSend) {
        this.bankSend = bankSend;
    }

    public String getBankRecv() {
        return bankRecv;
    }

    public void setBankRecv(String bankRecv) {
        this.bankRecv = bankRecv;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getSourceAcc() {
        return sourceAcc;
    }

    public void setSourceAcc(String sourceAcc) {
        this.sourceAcc = sourceAcc;
    }

    public String getSourceIban() {
        return sourceIban;
    }

    public void setSourceIban(String sourceIban) {
        this.sourceIban = sourceIban;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getDestIban() {
        return destIban;
    }

    public void setDestIban(String destIban) {
        this.destIban = destIban;
    }

    public String getNameSend() {
        return nameSend;
    }

    public void setNameSend(String nameSend) {
        this.nameSend = nameSend;
    }

    public String getSenderMeliCode() {
        return senderMeliCode;
    }

    public void setSenderMeliCode(String senderMeliCode) {
        this.senderMeliCode = senderMeliCode;
    }

    public String getSenderpostalCode() {
        return senderpostalCode;
    }

    public void setSenderpostalCode(String senderpostalCode) {
        this.senderpostalCode = senderpostalCode;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderTell() {
        return senderTell;
    }

    public void setSenderTell(String senderTell) {
        this.senderTell = senderTell;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpCode() {
        return expCode;
    }

    public void setExpCode(String expCode) {
        this.expCode = expCode;
    }

    public String getExpDescription() {
        return expDescription;
    }

    public void setExpDescription(String expDescription) {
        this.expDescription = expDescription;
    }

    public int getOpId() {
        return opId;
    }

    public void setOpId(int opId) {
        this.opId = opId;
    }

    public long getStartTrack() {
        return startTrack;
    }

    public void setStartTrack(long startTrack) {
        this.startTrack = startTrack;
    }

    public long getEndTrack() {
        return endTrack;
    }

    public void setEndTrack(long endTrack) {
        this.endTrack = endTrack;
    }

    public String getSenderShahab() {
        return senderShahab;
    }

    public void setSenderShahab(String senderShahab) {
        this.senderShahab = senderShahab;
    }

    public String getReciverShahab() {
        return reciverShahab;
    }

    public void setReciverShahab(String reciverShahab) {
        this.reciverShahab = reciverShahab;
    }

    public String getReciverMeliCode() {
        return reciverMeliCode;
    }

    public void setReciverMeliCode(String reciverMeliCode) {
        this.reciverMeliCode = reciverMeliCode;
    }

    public String getReciverPostalCode() {
        return reciverPostalCode;
    }

    public void setReciverPostalCode(String reciverPostalCode) {
        this.reciverPostalCode = reciverPostalCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof PayaRequest)) return false;
        PayaRequest castOther = (PayaRequest) other;
        return new EqualsBuilder()
                .append(this.getSerial(), castOther.getSerial())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getSerial())
                .toHashCode();
    }
}
