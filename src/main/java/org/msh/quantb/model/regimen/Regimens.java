//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.04.23 at 04:53:00 PM EEST 
//


package org.msh.quantb.model.regimen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.msh.quantb.model.gen.Regimen;
import org.msh.quantb.model.gen.SimpleStamp;


/**
 * <p>Java class for Regimens complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Regimens">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="regimen" type="{http://www.msh.org/quantb/model/gen}Regimen" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="stamp" type="{http://www.msh.org/quantb/model/gen}SimpleStamp"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Regimens", propOrder = {
    "regimen",
    "stamp"
})
public class Regimens {

    protected List<Regimen> regimen;
    @XmlElement(required = true)
    protected SimpleStamp stamp;

    /**
     * Gets the value of the regimen property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the regimen property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegimen().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Regimen }
     * 
     * 
     */
    public List<Regimen> getRegimen() {
        if (regimen == null) {
            regimen = new ArrayList<Regimen>();
        }
        return this.regimen;
    }

    /**
     * Gets the value of the stamp property.
     * 
     * @return
     *     possible object is
     *     {@link SimpleStamp }
     *     
     */
    public SimpleStamp getStamp() {
        return stamp;
    }

    /**
     * Sets the value of the stamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link SimpleStamp }
     *     
     */
    public void setStamp(SimpleStamp value) {
        this.stamp = value;
    }

}
