
package cdm.api.windows.xcep.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrivateKeyAttributes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PrivateKeyAttributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="minimalKeyLength" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="keySpec" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="keyUsageProperty" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="permissions" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="algorithmOIDReference" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cryptoProviders" type="{http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy}CryptoProviders"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrivateKeyAttributes", propOrder = {
    "minimalKeyLength",
    "keySpec",
    "keyUsageProperty",
    "permissions",
    "algorithmOIDReference",
    "cryptoProviders"
})
public class PrivateKeyAttributes {

    @XmlSchemaType(name = "unsignedInt")
    protected long minimalKeyLength;
    @XmlElement(required = true, type = Long.class, nillable = true)
    @XmlSchemaType(name = "unsignedInt")
    protected Long keySpec;
    @XmlElement(required = true, type = Long.class, nillable = true)
    @XmlSchemaType(name = "unsignedInt")
    protected Long keyUsageProperty;
    @XmlElement(required = true, nillable = true)
    protected String permissions;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer algorithmOIDReference;
    @XmlElement(required = true, nillable = true)
    protected CryptoProviders cryptoProviders;

    /**
     * Gets the value of the minimalKeyLength property.
     * 
     */
    public long getMinimalKeyLength() {
        return minimalKeyLength;
    }

    /**
     * Sets the value of the minimalKeyLength property.
     * 
     */
    public void setMinimalKeyLength(long value) {
        this.minimalKeyLength = value;
    }

    /**
     * Gets the value of the keySpec property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getKeySpec() {
        return keySpec;
    }

    /**
     * Sets the value of the keySpec property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setKeySpec(Long value) {
        this.keySpec = value;
    }

    /**
     * Gets the value of the keyUsageProperty property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getKeyUsageProperty() {
        return keyUsageProperty;
    }

    /**
     * Sets the value of the keyUsageProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setKeyUsageProperty(Long value) {
        this.keyUsageProperty = value;
    }

    /**
     * Gets the value of the permissions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPermissions() {
        return permissions;
    }

    /**
     * Sets the value of the permissions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPermissions(String value) {
        this.permissions = value;
    }

    /**
     * Gets the value of the algorithmOIDReference property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAlgorithmOIDReference() {
        return algorithmOIDReference;
    }

    /**
     * Sets the value of the algorithmOIDReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAlgorithmOIDReference(Integer value) {
        this.algorithmOIDReference = value;
    }

    /**
     * Gets the value of the cryptoProviders property.
     * 
     * @return
     *     possible object is
     *     {@link CryptoProviders }
     *     
     */
    public CryptoProviders getCryptoProviders() {
        return cryptoProviders;
    }

    /**
     * Sets the value of the cryptoProviders property.
     * 
     * @param value
     *     allowed object is
     *     {@link CryptoProviders }
     *     
     */
    public void setCryptoProviders(CryptoProviders value) {
        this.cryptoProviders = value;
    }

}
