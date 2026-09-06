package com.simonbaars.codearena.javaparser.demo;

/**
 * Demo class with intentional code smells for testing smell detection.
 * This class contains duplication, complexity, and size issues.
 */
public class DemoSmells {
    
    /**
     * Method with high cyclomatic complexity (too many branches).
     */
    public String processInput(int value, boolean flag, String type) {
        String result = "";
        
        if (value < 0) {
            if (flag) {
                if (type.equals("A")) {
                    result = "Negative A";
                } else if (type.equals("B")) {
                    result = "Negative B";
                } else if (type.equals("C")) {
                    result = "Negative C";
                } else {
                    result = "Negative Other";
                }
            } else {
                result = "Negative No Flag";
            }
        } else if (value == 0) {
            if (flag) {
                result = "Zero with flag";
            } else {
                result = "Zero without flag";
            }
        } else if (value < 10) {
            result = "Low value";
        } else if (value < 100) {
            result = "Medium value";
        } else {
            result = "High value";
        }
        
        return result;
    }
    
    /**
     * Method with too many parameters (interface size).
     */
    public void complexMethod(int a, int b, int c, int d, int e, int f, int g, int h) {
        System.out.println("Too many parameters: " + a + b + c + d + e + f + g + h);
    }
    
    /**
     * Large method with many lines (unit volume).
     */
    public void largeMethod() {
        int sum = 0;
        for (int i = 0; i < 100; i++) {
            sum += i;
            System.out.println("Processing: " + i);
            if (i % 2 == 0) {
                System.out.println("Even number");
            } else {
                System.out.println("Odd number");
            }
            if (i % 5 == 0) {
                System.out.println("Multiple of 5");
            }
            if (i % 10 == 0) {
                System.out.println("Multiple of 10");
            }
            for (int j = 0; j < 10; j++) {
                System.out.println("Inner loop: " + j);
                if (j == i % 10) {
                    break;
                }
            }
        }
        System.out.println("Total: " + sum);
        System.out.println("Done processing");
        System.out.println("Final value: " + sum);
        System.out.println("Exiting method");
        System.out.println("Cleanup");
        System.out.println("More cleanup");
        System.out.println("Even more cleanup");
        System.out.println("Still cleaning up");
        System.out.println("Almost done");
        System.out.println("Really almost done");
        System.out.println("Just a bit more");
        System.out.println("One more line");
        System.out.println("Final line");
    }
    
    /**
     * Duplicated method 1.
     */
    public void calculateSum() {
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
     * Duplicated method 2 (nearly identical to calculateSum).
     */
    public void computeTotal() {
        int total = 0;
        for (int i = 1; i <= 100; i++) {
            total += i;
            if (total > 1000) {
                System.out.println("Exceeded threshold");
            }
        }
        System.out.println("Final sum: " + total);
    }
}
