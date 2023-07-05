package branch.dpi.atlas.service.cm.handler.cms.reCreateResp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName _RECREATERESPONSE_QNAME = new QName("", "RE_CREATE_RESPONSE");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RECREATERESPONSEType }
     * 
     */
    public RECREATERESPONSEType createRECREATERESPONSEType() {
        return new RECREATERESPONSEType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RECREATERESPONSEType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "RE_CREATE_RESPONSE")
    public JAXBElement<RECREATERESPONSEType> createRECREATERESPONSE(RECREATERESPONSEType value) {
        return new JAXBElement<RECREATERESPONSEType>(_RECREATERESPONSE_QNAME, RECREATERESPONSEType.class, null, value);
    }

}
