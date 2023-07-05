package branch.dpi.atlas.service.cm.handler.cms.customerResp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _CUSTOMERRESPONSE_QNAME = new QName("", "CUSTOMER_RESPONSE");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CUSTOMERRESPONSEType }
     * 
     */
    public CUSTOMERRESPONSEType createCUSTOMERRESPONSEType() {
        return new CUSTOMERRESPONSEType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CUSTOMERRESPONSEType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CUSTOMER_RESPONSE")
    public JAXBElement<CUSTOMERRESPONSEType> createCUSTOMERRESPONSE(CUSTOMERRESPONSEType value) {
        return new JAXBElement<CUSTOMERRESPONSEType>(_CUSTOMERRESPONSE_QNAME, CUSTOMERRESPONSEType.class, null, value);
    }

}
