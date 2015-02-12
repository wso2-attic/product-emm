
package cdm.api.windows.xcep.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CACollection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CACollection">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cA" type="{http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy}CA" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CACollection", propOrder = {
    "ca"
})
public class CACollection {

    @XmlElement(name = "cA", required = true)
    protected List<CA> ca;

    /**
     * Gets the value of the ca property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ca property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CA }
     * 
     * 
     */
    public List<CA> getCA() {
        if (ca == null) {
            ca = new ArrayList<CA>();
        }
        return this.ca;
    }

}
