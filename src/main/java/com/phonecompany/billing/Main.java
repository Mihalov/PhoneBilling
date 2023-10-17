package com.phonecompany.billing;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
    TelephoneBillCalculator calculator = new TelephoneBillCalculatorImpl();
    String csvData = "";
    BigDecimal phoneBill = calculator.calculate(csvData);
    System.out.println("Your telephone bill is : " + phoneBill + " Kc");
    }
}