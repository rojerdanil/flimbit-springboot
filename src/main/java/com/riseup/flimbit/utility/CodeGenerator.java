package com.riseup.flimbit.utility;

import lombok.experimental.UtilityClass;


public class CodeGenerator {
	
	public static String generateCode(String username) {
        String suffix = String.valueOf((int)(Math.random() * 90000) + 10000); // 5-digit number
        String prefix = username.length() >= 4 ? username.substring(0, 4).toUpperCase() : "FILM";
        return prefix + suffix;
    }
	
	

}
