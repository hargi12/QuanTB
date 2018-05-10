package org.msh.quantb.services.excel;

import java.util.Date;

/**
 * It is DTO for the Medicine Stock import from Excel
 * @author Alexey Kurasov
 *
 */
public class ImportExcelDTO {
	private String medicine;
	private Date expDate;
	private Integer quantity;
	private Date arrive;
	private Integer orderQuantity;
	private Date orderExpDate;
	
	
	public ImportExcelDTO(String medicine, Date expDate, Integer quantity,
			Date _arrive, Integer _ordQuant, Date _ordExp) {
		super();
		this.medicine = medicine;
		this.expDate = expDate;
		this.quantity = quantity;
		this.arrive = _arrive;
		this.orderQuantity = _ordQuant;
		this.orderExpDate = _ordExp;
	}
	public String getMedicine() {
		return medicine;
	}
	public void setMedicine(String medicine) {
		this.medicine = medicine;
	}
	public Date getExpDate() {
		return expDate;
	}
	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public Date getArrive() {
		return arrive;
	}
	public void setArrive(Date arrive) {
		this.arrive = arrive;
	}
	
	public Integer getOrderQuantity() {
		return orderQuantity;
	}
	public void setOrderQuantity(Integer orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
	public Date getOrderExpDate() {
		return orderExpDate;
	}
	public void setOrderExpDate(Date orderExpDate) {
		this.orderExpDate = orderExpDate;
	}
	@Override
	public String toString() {
		return "ImportExcelDTO [medicine=" + medicine + ", expDate=" + expDate + ", quantity=" + quantity + ", arrive="
				+ arrive + ", orderQuantity=" + orderQuantity + ", orderExpDate=" + orderExpDate + "]";
	}
	/**
	 * DTO may contain stock or order and never both
	 * @return
	 */
	public boolean hasStock() {
		return getQuantity() != null;
	}
	/**
	 * DTO may contain stock or order and never both
	 * @return
	 */
	public boolean hasOrder() {
		return getOrderQuantity() != null;
	}

	
	
}
