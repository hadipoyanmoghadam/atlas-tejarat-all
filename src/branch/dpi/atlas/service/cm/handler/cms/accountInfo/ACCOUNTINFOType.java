package branch.dpi.atlas.service.cm.handler.cms.accountInfo;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="ACCOUNTINFO")
public class ACCOUNTINFOType {

    @XmlPath("RRN/text()")
    protected String rrn;
    @XmlPath("ACCOUNT_NO/text()")
    protected String accountno;

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
}
