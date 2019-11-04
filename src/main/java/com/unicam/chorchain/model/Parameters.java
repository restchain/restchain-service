package com.unicam.chorchain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Setter
@Getter
public class Parameters {
	/*private List<Integer> uintValue;
	private List<Boolean> boolValue;
	private List<String> stringValue;
	private List<String> addressValue;
	private String privateKey;
	
	public List<Integer> getUintValue() {
		return uintValue;
	}
	public void setUintValue(List<Integer> uintValue) {
		this.uintValue = uintValue;
	}
	public List<Boolean> getBoolValue() {
		return boolValue;
	}
	public void setBoolValue(List<Boolean> boolValue) {
		this.boolValue = boolValue;
	}
	public List<String> getStringValue() {
		return stringValue;
	}
	public void setStringValue(List<String> stringValue) {
		this.stringValue = stringValue;
	}
	public List<String> getAddressValue() {
		return addressValue;
	}
	public void setAddressValue(List<String> addressValue) {
		this.addressValue = addressValue;
	}
	
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	
	public Parameters(List<Integer> uintValue, List<Boolean> boolValue, List<String> stringValue,
			List<String> addressValue, String privateKey) {
		super();
		this.uintValue = uintValue;
		this.boolValue = boolValue;
		this.stringValue = stringValue;
		this.addressValue = addressValue;
		this.privateKey = privateKey;
	}*/

    private Map<String, String> paramsAndValue = new HashMap<String, String>();
    private String privateKey;
}
