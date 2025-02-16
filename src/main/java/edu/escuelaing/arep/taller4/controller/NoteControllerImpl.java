package edu.escuelaing.arep.taller4.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import edu.escuelaing.arep.taller4.http.HttpRequest;
import edu.escuelaing.arep.taller4.http.HttpResponse;

public class NoteControllerImpl {

    private static Map<String, BiFunction<HttpRequest, HttpResponse, String>> serviciosGet = new HashMap<>();
    private static Map<String, BiFunction<HttpRequest, HttpResponse, String>> serviciosPost = new HashMap<>();

    public static void get(String route, BiFunction<HttpRequest, HttpResponse, String> function) {
        serviciosGet.put("/app" + route, function);
    }


    public static void post(String route, BiFunction<HttpRequest, HttpResponse, String> function) {
        serviciosPost.put("/app" + route, function);
    }


    public BiFunction<HttpRequest, HttpResponse, String> getServices(String route) {
        return serviciosGet.get(route);
    }

    public BiFunction<HttpRequest, HttpResponse, String> postServices(String route) {
        return serviciosPost.get(route);
    }



}