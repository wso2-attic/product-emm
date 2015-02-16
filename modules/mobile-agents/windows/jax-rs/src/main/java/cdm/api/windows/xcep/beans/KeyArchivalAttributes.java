
package cdm.api.windows.xcep.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KeyArchivalAttributes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KeyArchivalAttributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="symmetricAlgorithmOIDReference" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="symmetricAlgorithmKeyLength" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KeyArchivalAttributes", propOrder = {
    "symmetricAlgorithmOIDReference",
    "symmetricAlgorithmKeyLength"
})
public class KeyArchivalAttributes {

    protected int symmetricAlgorithmOIDReference;
    @XmlSchemaType(name = "unsignedInt")
    protected long symmetricAlgorithmKeyLength;

    /**
     * Gets the value of the symmetricAlgorithmOIDReference property.
     * 
     */
    public int getSymmetricAlgorithmOIDReference() {
        return symmetricAlgorithmOIDReference;
    }

    /**
     * Sets the value of the symmetricAlgorithmOIDReference property.
     * 
     */
    public void setSymmetricAlgorithmOIDReference(int value) {
        this.symmetricAlgorithmOIDReference = value;
    }

    /**
     * Gets the value of the symmetricAlgorithmKeyLength property.
     * 
     */
    public long getSymmetricAlgorithmKeyLength() {
        return symmetricAlgorithmKeyLength;
    }

    /**
     * Sets the value of the symmetricAlgorithmKeyLength property.
     * 
     */
    public void setSymmetricAlgorithmKeyLength(long value) {
        this.symmetricAlgorithmKeyLength = value;
    }

}
