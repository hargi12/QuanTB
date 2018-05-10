package org.msh.quantb.view.panel;

public enum OrderScenarioEnum {
	OPTIMIST,
	PESSIMIST;
	
	public String value() {
        return name();
    }
	public static OrderScenarioEnum fromValue(String value){
		return valueOf(value);
	}
}
