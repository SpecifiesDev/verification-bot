package me.specifies.core.Proxy;

import java.util.HashMap;
import java.util.Map;

public class JSONFactory {
	

	private Map<String, String> values = new HashMap<>();
	
	/**
	 * Function to place a single value in the json object.
	 * @param key
	 * @param value
	 */
	public void put(String key, String value) {
		this.values.put(key, value);
	}
	
	/**
	 * Function to place multiple values in the json object. Works by simply matching every pair in a key / value pair. Will throw an error if the inserted array is odd due to this logic.
	 * @param arr
	 */
	public void putMultiple(String[] arr) {
		
		if(arr.length % 2 == 0) {
			for(int i = 0; i < arr.length; i+=2) {
				this.values.put(arr[i], arr[i+1]);
			}
		} else System.out.println("The range for a multiple insertion array must be divisble by two.");
		
		
	}
	
	/**
	 * Function to remove a single value in the json object.
	 * @param key
	 */
	public void remove(String key) {
		values.remove(key);
	}
	
	/**
	 * Function to flush to json factory.
	 */
	public void flush() {
		this.values = new HashMap<>();
	}
	
	/**
	 * Function to stringify the built factory to a json string that can be sent off for further manipulation.
	 * @return returnString
	 */
	public String stringify() {
		
		String returnString = "{";
		int position = 0;
		
		for(Map.Entry<String, String> entry : this.values.entrySet()) {
			
			String key = entry.getKey();
			String value = entry.getValue();
			
			if(position == this.values.size() - 1) {
				returnString += "\"" + key + "\": \"" + value + "\" }";
			} else {
				returnString += "\"" + key + "\": \"" + value + "\",";
			}
			
			position++;
		}
		
		return returnString;
		
	}
	

}
