package branch.dpi.atlas.service.cm.handler.cms.customer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _CUSTOMER_QNAME = new QName("", "CUSTOMER");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: customer
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CUSTOMERType }
     * 
     */
    public CUSTOMERType createCUSTOMERType() {
        return new CUSTOMERType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CUSTOMERType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CUSTOMER")
    public JAXBElement<CUSTOMERType> createCUSTOMER(CUSTOMERType value) {
        return new JAXBElement<CUSTOMERType>(_CUSTOMER_QNAME, CUSTOMERType.class, null, value);
    }

}
