package edu.escuelaing.arep.taller4.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.escuelaing.arep.taller4.http.HttpRequest;
import edu.escuelaing.arep.taller4.server.annotations.GetMapping;
import edu.escuelaing.arep.taller4.server.annotations.PostMapping;
import edu.escuelaing.arep.taller4.server.annotations.RequestBody;
import edu.escuelaing.arep.taller4.server.annotations.RequestParam;
import edu.escuelaing.arep.taller4.server.annotations.RestController;

public class MicroSpring {

    private static Map<String, Method> services = new HashMap<>();

    public static void start() {
        ClassFileScanner.listClasses();
        loadMethods();
        services.forEach((k, v) -> System.out.println(k + " " + v));
    }

    private static void loadMethods() {
        List<String> classPaths = ClassFileScanner.getClassPaths();
        for (String classPath : classPaths) {
            processClassPath(classPath);
        }
    }

    private static void processClassPath(String classPath) {
        boolean isRestController = true;
        while (isRestController) {
            try {
                Class<?> c = Class.forName(classPath);
                if (!c.isAnnotationPresent(RestController.class)) {
                    isRestController = false;
                }
                processMethods(c);
                isRestController = false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                isRestController = false;
            }
        }
    }

    private static void processMethods(Class<?> c) {
        for (Method m : c.getDeclaredMethods()) {
            String path = "";
            if (m.isAnnotationPresent(GetMapping.class)){
                path = m.getAnnotation(GetMapping.class).value();
                services.put("GET " + path, m);
            }
            else if(m.isAnnotationPresent(PostMapping.class)){
                path = m.getAnnotation(PostMapping.class).value();
                services.put("POST " + path, m);
            }   
        }
    }

    public static String callMicroSpringService(HttpRequest req) {
        StringBuilder response = new StringBuilder();
        try {
            return generateRequestResponse(response, services.get(req.getMethod() + " " + req.getPath()), req);
        } catch (Exception e) {
            return generateBadRequestResponse(response);
        }
    }

    private static String generateRequestResponse(StringBuilder response, Method service, HttpRequest req) throws IllegalAccessException, InvocationTargetException{
        Map<String, String> params = req.getQueryParams(); 
        System.out.println("SERVICE: " + service);
        Parameter[] parameters = service.getParameters();
        Object[] args = parameters.length == 0 ? null : getArgs(params, parameters, req);
        String result = service.invoke(null, args).toString();
        response.append("HTTP/1.1 200 OK\r\n");
        response.append("Content-Type: application/json\r\n");
        response.append("\r\n");
        response.append(result);
        return response.toString();
    }


    private static String generateBadRequestResponse(StringBuilder response) {
        response.append("HTTP/1.1 400 Bad Request\r\n");
        response.append("Content-Type: application/json\r\n");
        response.append("\r\n");
        response.append("{ \"error\": " + "\"" + "Invalid Request" + "\"}");
        return response.toString();
    }


    /**
     * 
     * @param params
     * @param parameters
     * @return arguments of the method. for the momentm when there's a RequestParam annotation it adds the value otherwise it ignores it
     */
    private static Object[] getArgs(Map<String, String> params, Parameter[] parameters, HttpRequest req ){
        Object[] args = new Object[parameters.length];
        System.out.println("ARGS: " + args.length);
        for(int i = 0; i < parameters.length; i++){
            Parameter parameter = parameters[i];
            if(parameter.isAnnotationPresent(RequestParam.class)){
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                if(requestParam != null){
                    args[i] = params.getOrDefault(requestParam.value(), requestParam.defaultValue());
                }
            }
            if(parameter.isAnnotationPresent(RequestBody.class)){
                RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
                if(requestBody != null){
                    args[i] = req.getQueryParams();
                }
            }
        }
        return args;

    }
}
