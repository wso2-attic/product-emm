
package cdm.api.windows.xcep.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RARequirements complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RARequirements">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rASignatures" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="rAEKUs" type="{http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy}OIDReferenceCollection"/>
 *         &lt;element name="rAPolicies" type="{http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy}OIDReferenceCollection"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RARequirements", propOrder = {
    "raSignatures",
    "raekUs",
    "raPolicies"
})
public class RARequirements {

    @XmlElement(name = "rASignatures")
    @XmlSchemaType(name = "unsignedInt")
    protected long raSignatures;
    @XmlElement(name = "rAEKUs", required = true, nillable = true)
    protected OIDReferenceCollection raekUs;
    @XmlElement(name = "rAPolicies", required = true, nillable = true)
    protected OIDReferenceCollection raPolicies;

    /**
     * Gets the value of the raSignatures property.
     * 
     */
    public long getRASignatures() {
        return raSignatures;
    }

    /**
     * Sets the value of the raSignatures property.
     * 
     */
    public void setRASignatures(long value) {
        this.raSignatures = value;
    }

    /**
     * Gets the value of the raekUs property.
     * 
     * @return
     *     possible object is
     *     {@link OIDReferenceCollection }
     *     
     */
    public OIDReferenceCollection getRAEKUs() {
        return raekUs;
    }

    /**
     * Sets the value of the raekUs property.
     * 
     * @param value
     *     allowed object is
     *     {@link OIDReferenceCollection }
     *     
     */
    public void setRAEKUs(OIDReferenceCollection value) {
        this.raekUs = value;
    }

    /**
     * Gets the value of the raPolicies property.
     * 
     * @return
     *     possible object is
     *     {@link OIDReferenceCollection }
     *     
     */
    public OIDReferenceCollection getRAPolicies() {
        return raPolicies;
    }

    /**
     * Sets the value of the raPolicies property.
     * 
     * @param value
     *     allowed object is
     *     {@link OIDReferenceCollection }
     *     
     */
    public void setRAPolicies(OIDReferenceCollection value) {
        this.raPolicies = value;
    }

}
