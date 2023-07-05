package branch.dpi.atlas.service.cm.handler.cms.balance;


import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _BALANCE_QNAME = new QName("", "BALANCE");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: balance
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BALANCEType }
     *
     */
    public BALANCEType createBALANCEType() {
        return new BALANCEType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link branch.dpi.atlas.service.cm.handler.cms.balance.BALANCEType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "BALANCE")
    public JAXBElement<BALANCEType> createBALANCE(BALANCEType value) {
        return new JAXBElement<BALANCEType>(_BALANCE_QNAME, BALANCEType.class, null, value);
    }

}
