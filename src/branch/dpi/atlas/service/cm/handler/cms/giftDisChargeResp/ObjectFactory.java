package branch.dpi.atlas.service.cm.handler.cms.giftDisChargeResp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _GIFTDISCHARGERESPONSE_QNAME = new QName("", "DISCHARGE_GIFTCARD_RESPONSE");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GIFTDISCHARGERESPONSEType }
     *
     */
    public GIFTDISCHARGERESPONSEType createGIFTDISCHARGERESPONSEType() {
        return new GIFTDISCHARGERESPONSEType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link GIFTDISCHARGERESPONSEType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DISCHARGE_GIFTCARD_RESPONSE")
    public JAXBElement<GIFTDISCHARGERESPONSEType> createBALANCERESPONSE(GIFTDISCHARGERESPONSEType value) {
        return new JAXBElement<GIFTDISCHARGERESPONSEType>(_GIFTDISCHARGERESPONSE_QNAME, GIFTDISCHARGERESPONSEType.class, null, value);
    }

}
