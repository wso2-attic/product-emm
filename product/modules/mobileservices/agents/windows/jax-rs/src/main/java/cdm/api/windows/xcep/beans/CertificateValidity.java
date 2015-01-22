
package cdm.api.windows.xcep.beans;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CertificateValidity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CertificateValidity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="validityPeriodSeconds" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
 *         &lt;element name="renewalPeriodSeconds" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateValidity", propOrder = {
    "validityPeriodSeconds",
    "renewalPeriodSeconds"
})
public class CertificateValidity {

    @XmlElement(required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger validityPeriodSeconds;
    @XmlElement(required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger renewalPeriodSeconds;

    /**
     * Gets the value of the validityPeriodSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getValidityPeriodSeconds() {
        return validityPeriodSeconds;
    }

    /**
     * Sets the value of the validityPeriodSeconds property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setValidityPeriodSeconds(BigInteger value) {
        this.validityPeriodSeconds = value;
    }

    /**
     * Gets the value of the renewalPeriodSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRenewalPeriodSeconds() {
        return renewalPeriodSeconds;
    }

    /**
     * Sets the value of the renewalPeriodSeconds property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRenewalPeriodSeconds(BigInteger value) {
        this.renewalPeriodSeconds = value;
    }

}
