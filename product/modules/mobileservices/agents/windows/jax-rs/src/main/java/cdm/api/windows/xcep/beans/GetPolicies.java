
package cdm.api.windows.xcep.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="client" type="{http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy}Client"/>
 *         &lt;element name="requestFilter" type="{http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy}RequestFilter"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "client",
    "requestFilter"
})
@XmlRootElement(name = "GetPolicies")
public class GetPolicies {

    @XmlElement(required = true)
    protected Client client;
    @XmlElement(required = true, nillable = true)
    protected RequestFilter requestFilter;

    /**
     * Gets the value of the client property.
     * 
     * @return
     *     possible object is
     *     {@link Client }
     *     
     */
    public Client getClient() {
        return client;
    }

    /**
     * Sets the value of the client property.
     * 
     * @param value
     *     allowed object is
     *     {@link Client }
     *     
     */
    public void setClient(Client value) {
        this.client = value;
    }

    /**
     * Gets the value of the requestFilter property.
     * 
     * @return
     *     possible object is
     *     {@link RequestFilter }
     *     
     */
    public RequestFilter getRequestFilter() {
        return requestFilter;
    }

    /**
     * Sets the value of the requestFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestFilter }
     *     
     */
    public void setRequestFilter(RequestFilter value) {
        this.requestFilter = value;
    }

}
