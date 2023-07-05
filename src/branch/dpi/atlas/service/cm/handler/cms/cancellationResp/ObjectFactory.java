package branch.dpi.atlas.service.cm.handler.cms.cancellationResp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _CANCELLATIONRESPONSE_QNAME = new QName("", "CANCELLATION_RESPONSE");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CANCELLATIONRESPONSEType }
     *
     */
    public CANCELLATIONRESPONSEType createCANCELLATIONRESPONSEType() {
        return new CANCELLATIONRESPONSEType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link CANCELLATIONRESPONSEType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CANCELLATION_RESPONSE")
    public JAXBElement<CANCELLATIONRESPONSEType> createCANCELLATIONRESPONSE(CANCELLATIONRESPONSEType value) {
        return new JAXBElement<CANCELLATIONRESPONSEType>(_CANCELLATIONRESPONSE_QNAME, CANCELLATIONRESPONSEType.class, null, value);
    }

}
