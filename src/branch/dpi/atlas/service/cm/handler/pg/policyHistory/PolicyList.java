package branch.dpi.atlas.service.cm.handler.pg.policyHistory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by sh.Behnaz on 8/27/18.
 */


@XmlType(propOrder ={"policy"})
@XmlRootElement(name = "POLICES")
public class PolicyList {

    protected List<Policy> policy;

    public List<Policy> getPolicy() {
        return policy;
    }
    @XmlElement(name = "POLICY")
    public void setPolicy(List<Policy> policy) {
        this.policy = policy;
    }
}


