package edu.escuelaing.arep.taller3.controller;

import edu.escuelaing.arep.taller3.server.annotations.GetMapping;
import edu.escuelaing.arep.taller3.server.annotations.RequestParam;
import edu.escuelaing.arep.taller3.server.annotations.RestController;
import edu.escuelaing.arep.taller3.services.GreetingServices;

@RestController
public class GretingController {

    private static GreetingServices greetingServices = new GreetingServices();

    @GetMapping("/spring/hello")
    public static String greeting(@RequestParam(value = "name", defaultValue = "world")String name) {
        return greetingServices.greet(name);
    }
}
