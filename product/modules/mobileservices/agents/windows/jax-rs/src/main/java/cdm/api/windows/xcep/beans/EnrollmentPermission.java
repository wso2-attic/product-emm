
package cdm.api.windows.xcep.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EnrollmentPermission complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EnrollmentPermission">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="enroll" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="autoEnroll" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnrollmentPermission", propOrder = {
    "enroll",
    "autoEnroll"
})
public class EnrollmentPermission {

    protected boolean enroll;
    protected boolean autoEnroll;

    /**
     * Gets the value of the enroll property.
     * 
     */
    public boolean isEnroll() {
        return enroll;
    }

    /**
     * Sets the value of the enroll property.
     * 
     */
    public void setEnroll(boolean value) {
        this.enroll = value;
    }

    /**
     * Gets the value of the autoEnroll property.
     * 
     */
    public boolean isAutoEnroll() {
        return autoEnroll;
    }

    /**
     * Sets the value of the autoEnroll property.
     * 
     */
    public void setAutoEnroll(boolean value) {
        this.autoEnroll = value;
    }

}
