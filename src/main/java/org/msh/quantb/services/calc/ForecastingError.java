package org.msh.quantb.services.calc;
/**
 * This class represents forecasting calculation error or warning message and error place to navigate before message 
 * will be display to user.
 * @author alexey
 *
 */
public class ForecastingError {
	private String place;
	private String message;
	//possible places
	public static final String ENROLLED_CASES = "ec";
	public static final String NEW_CASES = "nc";
	public static final String MEDICINES = "m";
	public static final String MAIN = "main";
	/**
	 * Only valid constructor
	 * @param _place
	 * @param _message
	 */
	public ForecastingError(String _place, String _message){
		setPlace(_place);
		setMessage(_message);
	}
	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}
	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ForecastingError [place=" + place + ", message=" + message
				+ "]";
	}

	

}
