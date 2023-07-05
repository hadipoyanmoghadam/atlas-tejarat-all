package branch.dpi.atlas.model.tj.entity;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import branch.dpi.atlas.util.ImmediateCardUtil;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

/**
 * User: Behnaz
 * Date: Feb 6, 2012
 * Time: 9:50:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class CFDPan {
    String sparrow_branch_id = "";
    String serial = "";
    int row = 0;
    String creation_date_pan = "";
    String edit_date_pan = "";
    String sequence = "0";
    int cardAccount_status = -1;
    int cardType = 0;

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public CFDPan() {
    }

    public String toString() {
        return " sparrow_branch_id =" + sparrow_branch_id + " serial=" + serial + " creation_date_pan=" +
                creation_date_pan + " edit_date_pan=" + edit_date_pan + " sequence=" + sequence + " cardType=" + cardType;
    }

    public String getSparrow_branch_id() {
        return sparrow_branch_id;
    }

    public void setSparrow_branch_id(String sparrow_branch_id) {
        if (sparrow_branch_id != null) {
            sparrow_branch_id = sparrow_branch_id.trim();
            this.sparrow_branch_id = sparrow_branch_id;
        }
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) throws Exception {
        try {
            serial = serial.trim();
            if (!ImmediateCardUtil.validateElement(serial, 13)) {
                throw new Exception();
            } else {
                this.serial = Constants.BANKE_TEJARAT_BIN + serial;
            }
        } catch (Exception e) {
            throw new Exception("SERIAL " + serial + " in XML File has Error in lenght or other things.");
        }
    }

    public String getCreation_date_pan() {
        return creation_date_pan;
    }

    public void setCreation_date_pan(String creation_date_pan) {
        if (creation_date_pan != null) {
            creation_date_pan = creation_date_pan.trim();
            this.creation_date_pan = creation_date_pan;
        }
    }

    public String getEdit_date_pan() {
        return edit_date_pan;
    }

    public void setEdit_date_pan(String edit_date_pan) {
        if (edit_date_pan != null) {
            edit_date_pan = edit_date_pan.trim();
            this.edit_date_pan = edit_date_pan;
        }
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        if (sequence != null) {
            sequence = sequence.trim();
            this.sequence = sequence;
        }
    }

    public int getCardAccount_status() {
        return cardAccount_status;
    }

    public void setCardAccount_status(String cardAccount_status) {
        try {
            if (cardAccount_status != null && !cardAccount_status.trim().equals("") && ImmediateCardUtil.validateElement(cardAccount_status, 1))
                this.cardAccount_status = Integer.parseInt(cardAccount_status.trim());
        } catch (Exception e) {
//            throw new Exception("CARD_STATUS " + cardAccount_status + " in XML File has Error in lenght or other things.");
        }
    }

    public int getRow() {
        return row;
    }

    public void setRow(String row) {
        if ((row == null) || ("".equals(row)))
            this.row = 0;
        else
            this.row = Integer.parseInt(row.trim());
    }
}
