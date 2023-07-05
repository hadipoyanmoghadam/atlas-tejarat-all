package branch.dpi.atlas.service.cm.handler.cms.accountInfoResp;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName _ACCOUNTINFORESPONSE_QNAME = new QName("", "ACCOUNTINFO_RESPONSE");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ACCOUNTINFORESPONSEType }
     * 
     */
    public ACCOUNTINFORESPONSEType createACCOUNTINFORESPONSEType() {
        return new ACCOUNTINFORESPONSEType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ACCOUNTINFORESPONSEType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ACCOUNTINFO_RESPONSE")
    public JAXBElement<ACCOUNTINFORESPONSEType> createACCOUNTINFORESPONSE(ACCOUNTINFORESPONSEType value) {
        return new JAXBElement<ACCOUNTINFORESPONSEType>(_ACCOUNTINFORESPONSE_QNAME, ACCOUNTINFORESPONSEType.class, null, value);
    }

}
