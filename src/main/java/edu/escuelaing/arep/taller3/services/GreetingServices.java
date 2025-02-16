package edu.escuelaing.arep.taller3.services;

public class GreetingServices {
    public String greet(String name){
        return "{ \"greeting\" : \"Hello "+name+"!\" }";
    }
}
