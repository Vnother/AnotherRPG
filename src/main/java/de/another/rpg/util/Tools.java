package de.another.rpg.util;

public class Tools {

    public static String capitalizeFirstChar(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
}
