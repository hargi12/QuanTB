//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.04.23 at 04:53:00 PM EEST 
//


package org.msh.quantb.model.forecast;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PricePack complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PricePack">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="adjust" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="adjustAccel" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="pack" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="packAccel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="packPrice" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="packPriceAccel" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PricePack", propOrder = {
    "adjust",
    "adjustAccel",
    "pack",
    "packAccel",
    "packPrice",
    "packPriceAccel"
})
public class PricePack {

    @XmlElement(required = true)
    protected BigDecimal adjust;
    @XmlElement(required = true)
    protected BigDecimal adjustAccel;
    protected int pack;
    protected int packAccel;
    @XmlElement(required = true)
    protected BigDecimal packPrice;
    @XmlElement(required = true)
    protected BigDecimal packPriceAccel;

    /**
     * Gets the value of the adjust property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAdjust() {
        return adjust;
    }

    /**
     * Sets the value of the adjust property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAdjust(BigDecimal value) {
        this.adjust = value;
    }

    /**
     * Gets the value of the adjustAccel property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAdjustAccel() {
        return adjustAccel;
    }

    /**
     * Sets the value of the adjustAccel property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAdjustAccel(BigDecimal value) {
        this.adjustAccel = value;
    }

    /**
     * Gets the value of the pack property.
     * 
     */
    public int getPack() {
        return pack;
    }

    /**
     * Sets the value of the pack property.
     * 
     */
    public void setPack(int value) {
        this.pack = value;
    }

    /**
     * Gets the value of the packAccel property.
     * 
     */
    public int getPackAccel() {
        return packAccel;
    }

    /**
     * Sets the value of the packAccel property.
     * 
     */
    public void setPackAccel(int value) {
        this.packAccel = value;
    }

    /**
     * Gets the value of the packPrice property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPackPrice() {
        return packPrice;
    }

    /**
     * Sets the value of the packPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPackPrice(BigDecimal value) {
        this.packPrice = value;
    }

    /**
     * Gets the value of the packPriceAccel property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPackPriceAccel() {
        return packPriceAccel;
    }

    /**
     * Sets the value of the packPriceAccel property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPackPriceAccel(BigDecimal value) {
        this.packPriceAccel = value;
    }

}
