package branch.dpi.atlas.service.cm.handler.cms.giftChargeResp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _GIFTCHARGERESPONSE_QNAME = new QName("", "CHARGE_GIFTCARD_RESPONSE");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GIFTCHARGERESPONSEType }
     *
     */
    public GIFTCHARGERESPONSEType createGIFTCHARGERESPONSEType() {
        return new GIFTCHARGERESPONSEType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link GIFTCHARGERESPONSEType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CHARGE_GIFTCARD_RESPONSE")
    public JAXBElement<GIFTCHARGERESPONSEType> createGIFTCHARGERESPONSE(GIFTCHARGERESPONSEType value) {
        return new JAXBElement<GIFTCHARGERESPONSEType>(_GIFTCHARGERESPONSE_QNAME, GIFTCHARGERESPONSEType.class, null, value);
    }

}
