
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
 *         &lt;element name="response" type="{http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy}Response"/>
 *         &lt;element name="cAs" type="{http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy}CACollection"/>
 *         &lt;element name="oIDs" type="{http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy}OIDCollection"/>
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
    "response",
    "cAs",
    "oiDs"
})
@XmlRootElement(name = "GetPoliciesResponse")
public class GetPoliciesResponse {

    @XmlElement(required = true, nillable = true)
    protected Response response;
    @XmlElement(required = true, nillable = true)
    protected CACollection cAs;
    @XmlElement(name = "oIDs", required = true, nillable = true)
    protected OIDCollection oiDs;

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link Response }
     *     
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link Response }
     *     
     */
    public void setResponse(Response value) {
        this.response = value;
    }

    /**
     * Gets the value of the cAs property.
     * 
     * @return
     *     possible object is
     *     {@link CACollection }
     *     
     */
    public CACollection getCAs() {
        return cAs;
    }

    /**
     * Sets the value of the cAs property.
     * 
     * @param value
     *     allowed object is
     *     {@link CACollection }
     *     
     */
    public void setCAs(CACollection value) {
        this.cAs = value;
    }

    /**
     * Gets the value of the oiDs property.
     * 
     * @return
     *     possible object is
     *     {@link OIDCollection }
     *     
     */
    public OIDCollection getOIDs() {
        return oiDs;
    }

    /**
     * Sets the value of the oiDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link OIDCollection }
     *     
     */
    public void setOIDs(OIDCollection value) {
        this.oiDs = value;
    }

}
