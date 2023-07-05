package branch.dpi.atlas.service.cm.handler.cms.balanceResp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _BALANCERESPONSE_QNAME = new QName("", "BALANCE_RESPONSE");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BALANCERESPONSEType }
     *
     */
    public BALANCERESPONSEType createBALANCERESPONSEType() {
        return new BALANCERESPONSEType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link BALANCERESPONSEType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "BALANCE_RESPONSE")
    public JAXBElement<BALANCERESPONSEType> createBALANCERESPONSE(BALANCERESPONSEType value) {
        return new JAXBElement<BALANCERESPONSEType>(_BALANCERESPONSE_QNAME, BALANCERESPONSEType.class, null, value);
    }

}
