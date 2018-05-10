//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.04.23 at 04:53:00 PM EEST 
//


package org.msh.quantb.model.locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for localeSaved complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="localeSaved">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lang" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="doNotShowHelp" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="pathToFiles" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "localeSaved", propOrder = {
    "country",
    "lang",
    "doNotShowHelp",
    "pathToFiles"
})
public class LocaleSaved {

    @XmlElement(required = true)
    protected String country;
    @XmlElement(required = true)
    protected String lang;
    protected boolean doNotShowHelp;
    @XmlElement(required = true)
    protected String pathToFiles;

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the lang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(String value) {
        this.lang = value;
    }

    /**
     * Gets the value of the doNotShowHelp property.
     * 
     */
    public boolean isDoNotShowHelp() {
        return doNotShowHelp;
    }

    /**
     * Sets the value of the doNotShowHelp property.
     * 
     */
    public void setDoNotShowHelp(boolean value) {
        this.doNotShowHelp = value;
    }

    /**
     * Gets the value of the pathToFiles property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPathToFiles() {
        return pathToFiles;
    }

    /**
     * Sets the value of the pathToFiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPathToFiles(String value) {
        this.pathToFiles = value;
    }

}
