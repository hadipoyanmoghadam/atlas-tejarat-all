package branch.dpi.atlas.service.cm.handler.cms.giftDisCharge;


import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _GIFTDISCHARGE_QNAME = new QName("", "DISCHARGE_GIFTCARD");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: GIFTDISCHARGE
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GIFTDISCHARGEType }
     *
     */
    public GIFTDISCHARGEType createGIFTDISCHARGEType() {
        return new GIFTDISCHARGEType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link GIFTDISCHARGEType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "DISCHARGE_GIFTCARD")
    public JAXBElement<GIFTDISCHARGEType> createGIFTDISCHARGE(GIFTDISCHARGEType value) {
        return new JAXBElement<GIFTDISCHARGEType>(_GIFTDISCHARGE_QNAME, GIFTDISCHARGEType.class, null, value);
    }

}
