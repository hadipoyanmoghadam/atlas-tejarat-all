package branch.dpi.atlas.service.cm.handler.cms.cancellation;


import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _CANCELLATION_QNAME = new QName("", "CANCELLATION");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: cancellation
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CANCELLATIONType }
     *
     */
    public CANCELLATIONType createCANCELLATIONType() {
        return new CANCELLATIONType();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link CANCELLATIONType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "CANCELLATION")
    public JAXBElement<CANCELLATIONType> createCANCELLATION(CANCELLATIONType value) {
        return new JAXBElement<CANCELLATIONType>(_CANCELLATION_QNAME, CANCELLATIONType.class, null, value);
    }

}
