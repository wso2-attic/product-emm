
package cdm.api.windows.xcep.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CAURICollection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CAURICollection">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cAURI" type="{http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy}CAURI" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CAURICollection", propOrder = {
    "cauri"
})
public class CAURICollection {

    @XmlElement(name = "cAURI", required = true)
    protected List<CAURI> cauri;

    /**
     * Gets the value of the cauri property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cauri property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCAURI().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CAURI }
     * 
     * 
     */
    public List<CAURI> getCAURI() {
        if (cauri == null) {
            cauri = new ArrayList<CAURI>();
        }
        return this.cauri;
    }

}
