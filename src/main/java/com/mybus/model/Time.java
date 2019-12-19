/**
 * 
 */
package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author schanda on 02/04/16.
 *
 */
@Getter
@Setter
public class Time {
	private int hour;
	private int minute;
	private TimeUnit meridian;
	private enum TimeUnit {
	    AM,
	    PM;
	    @Override
	    public String toString() {
	        return name();
	    }
	}
}
