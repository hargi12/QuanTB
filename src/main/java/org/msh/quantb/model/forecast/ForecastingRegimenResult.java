//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.04.23 at 04:53:00 PM EEST 
//


package org.msh.quantb.model.forecast;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ForecastingRegimenResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ForecastingRegimenResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="month" type="{http://www.msh.org/quantb/model/forecast}Month"/>
 *         &lt;element name="fromDay" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="toDay" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="intensive" type="{http://www.msh.org/quantb/model/forecast}PhaseResult"/>
 *         &lt;element name="continious" type="{http://www.msh.org/quantb/model/forecast}PhaseResult"/>
 *         &lt;element name="addPhases" type="{http://www.msh.org/quantb/model/forecast}PhaseResult" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="weekList" type="{http://www.msh.org/quantb/model/forecast}WeekQuantity" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="cons" type="{http://www.msh.org/quantb/model/forecast}MedicineCons" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ForecastingRegimenResult", propOrder = {
    "month",
    "fromDay",
    "toDay",
    "intensive",
    "continious",
    "addPhases",
    "weekList",
    "cons"
})
public class ForecastingRegimenResult {

    @XmlElement(required = true)
    protected Month month;
    protected int fromDay;
    protected int toDay;
    @XmlElement(required = true)
    protected PhaseResult intensive;
    @XmlElement(required = true)
    protected PhaseResult continious;
    protected List<PhaseResult> addPhases;
    protected List<WeekQuantity> weekList;
    protected List<MedicineCons> cons;

    /**
     * Gets the value of the month property.
     * 
     * @return
     *     possible object is
     *     {@link Month }
     *     
     */
    public Month getMonth() {
        return month;
    }

    /**
     * Sets the value of the month property.
     * 
     * @param value
     *     allowed object is
     *     {@link Month }
     *     
     */
    public void setMonth(Month value) {
        this.month = value;
    }

    /**
     * Gets the value of the fromDay property.
     * 
     */
    public int getFromDay() {
        return fromDay;
    }

    /**
     * Sets the value of the fromDay property.
     * 
     */
    public void setFromDay(int value) {
        this.fromDay = value;
    }

    /**
     * Gets the value of the toDay property.
     * 
     */
    public int getToDay() {
        return toDay;
    }

    /**
     * Sets the value of the toDay property.
     * 
     */
    public void setToDay(int value) {
        this.toDay = value;
    }

    /**
     * Gets the value of the intensive property.
     * 
     * @return
     *     possible object is
     *     {@link PhaseResult }
     *     
     */
    public PhaseResult getIntensive() {
        return intensive;
    }

    /**
     * Sets the value of the intensive property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhaseResult }
     *     
     */
    public void setIntensive(PhaseResult value) {
        this.intensive = value;
    }

    /**
     * Gets the value of the continious property.
     * 
     * @return
     *     possible object is
     *     {@link PhaseResult }
     *     
     */
    public PhaseResult getContinious() {
        return continious;
    }

    /**
     * Sets the value of the continious property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhaseResult }
     *     
     */
    public void setContinious(PhaseResult value) {
        this.continious = value;
    }

    /**
     * Gets the value of the addPhases property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addPhases property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddPhases().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PhaseResult }
     * 
     * 
     */
    public List<PhaseResult> getAddPhases() {
        if (addPhases == null) {
            addPhases = new ArrayList<PhaseResult>();
        }
        return this.addPhases;
    }

    /**
     * Gets the value of the weekList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the weekList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWeekList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WeekQuantity }
     * 
     * 
     */
    public List<WeekQuantity> getWeekList() {
        if (weekList == null) {
            weekList = new ArrayList<WeekQuantity>();
        }
        return this.weekList;
    }

    /**
     * Gets the value of the cons property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cons property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCons().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MedicineCons }
     * 
     * 
     */
    public List<MedicineCons> getCons() {
        if (cons == null) {
            cons = new ArrayList<MedicineCons>();
        }
        return this.cons;
    }

}
