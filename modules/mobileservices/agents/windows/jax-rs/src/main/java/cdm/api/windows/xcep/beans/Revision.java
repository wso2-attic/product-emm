
package cdm.api.windows.xcep.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Revision complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Revision">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="majorRevision" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="minorRevision" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Revision", propOrder = {
    "majorRevision",
    "minorRevision"
})
public class Revision {

    @XmlSchemaType(name = "unsignedInt")
    protected long majorRevision;
    @XmlSchemaType(name = "unsignedInt")
    protected long minorRevision;

    /**
     * Gets the value of the majorRevision property.
     * 
     */
    public long getMajorRevision() {
        return majorRevision;
    }

    /**
     * Sets the value of the majorRevision property.
     * 
     */
    public void setMajorRevision(long value) {
        this.majorRevision = value;
    }

    /**
     * Gets the value of the minorRevision property.
     * 
     */
    public long getMinorRevision() {
        return minorRevision;
    }

    /**
     * Sets the value of the minorRevision property.
     * 
     */
    public void setMinorRevision(long value) {
        this.minorRevision = value;
    }

}
