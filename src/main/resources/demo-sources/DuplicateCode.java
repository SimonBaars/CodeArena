package com.simonbaars.codearena.javaparser.demo;

/**
 * Another demo class with code duplication.
 */
public class DuplicateCode {
    
    /**
     * Another duplicate of the sum calculation.
     */
    public void addNumbers() {
        int total = 0;
        for (int i = 1; i <= 100; i++) {
            total += i;
            if (total > 1000) {
                System.out.println("Exceeded threshold");
            }
        }
        System.out.println("Final sum: " + total);
    }
    
    /**
     * Complex validation method.
     */
    public boolean validate(Object obj, String type, int level, boolean strict) {
        if (obj == null) {
            return false;
        }
        
        if (type == null || type.isEmpty()) {
            return false;
        }
        
        if (level < 0) {
            if (strict) {
                throw new IllegalArgumentException("Invalid level");
            } else {
                return false;
            }
        }
        
        if (level > 10) {
            if (strict) {
                if (type.equals("premium")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return level <= 20;
            }
        }
        
        if (type.equals("basic")) {
            if (level > 5) {
                return false;
            }
        }
        
        return true;
    }
}
