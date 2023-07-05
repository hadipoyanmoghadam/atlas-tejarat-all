package branch.dpi.atlas.service.cm.handler.cms.accountInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _ACCOUNTINFO_QNAME = new QName("", "ACCOUNTINFO");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ACCOUNTINFOType }
     * 
     */
    public ACCOUNTINFOType createACCOUNTINFOType() {
        return new ACCOUNTINFOType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ACCOUNTINFOType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ACCOUNTINFO")
    public JAXBElement<ACCOUNTINFOType> createACCOUNTINFO(ACCOUNTINFOType value) {
        return new JAXBElement<ACCOUNTINFOType>(_ACCOUNTINFO_QNAME, ACCOUNTINFOType.class, null, value);
    }

}
