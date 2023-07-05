package branch.dpi.atlas.service.cm.handler.cms.giftCharge;


import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _GIFTCHARGE_QNAME = new QName("", "CHARGE_GIFTCARD");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: GIFTCHARGE
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GIFTCHARGEType }
     *
     */
    public GIFTCHARGEType createGIFTCHARGEType() {
        return new GIFTCHARGEType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link GIFTCHARGEType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CHARGE_GIFTCARD")
    public JAXBElement<GIFTCHARGEType> createGIFTCHARGE(GIFTCHARGEType value) {
        return new JAXBElement<GIFTCHARGEType>(_GIFTCHARGE_QNAME, GIFTCHARGEType.class, null, value);
    }

}
