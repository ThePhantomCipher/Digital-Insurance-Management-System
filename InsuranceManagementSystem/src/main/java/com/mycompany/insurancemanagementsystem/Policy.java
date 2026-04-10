/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.insurancemanagementsystem;

/** 
 * 
 * @author Kamogelo 
 */ 
public class Policy { 
    private String idNum, name, surname, address, policyType; 
    private int age; 
    private double coverageAmount, premiumAmount; 
     
    public Policy(String idNum, String name, String surname, String address, int age,  
                    String policyType, double coverageAmount, double premiumAmount){ 
        this.idNum = idNum; 
        this.name = name; 
        this.surname = surname; 
        this.address = address; 
        this.age = age; 
        this.policyType = policyType; 
        this.coverageAmount = coverageAmount; 
        this.premiumAmount = premiumAmount; 
    } 
     
    public String getIdNum(){ return idNum; }; 
    public String getName(){ return name; }; 
    public String getSurname(){ return surname; }; 
    public String getAddress(){ return address; }; 
    public int getAge(){ return age; }; 
    public String getPolicyType(){ return policyType; }; 
    public double getCoverageAmount(){ return coverageAmount; }; 
    public double getPremiumAmount(){ return premiumAmount; }; 
}
