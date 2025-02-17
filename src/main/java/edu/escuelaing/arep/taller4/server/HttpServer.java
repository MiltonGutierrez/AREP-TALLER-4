package edu.escuelaing.arep.taller4.server;

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

import edu.escuelaing.arep.taller4.controller.NoteControllerImpl;
import edu.escuelaing.arep.taller4.http.HttpRequest;
import edu.escuelaing.arep.taller4.http.HttpResponse;

import static edu.escuelaing.arep.taller4.server.MicroSpring.callMicroSpringService;

import java.io.*;

public class HttpServer {

    public static final int PORT = 8080;
    public static String WEB_ROOT;
    private static String INDEX_PAGE_URI = "/notes.html";
    private static boolean RUNNING = true;
    private static final NoteControllerImpl noteController = new NoteControllerImpl();
    private static final String HTTP_400_BAD_REQUEST = "HTTP/1.1 400 Bad Request";
    private static int MAX_THREADS = 10;


    public static void setIndexPageUri(String uri) {
        INDEX_PAGE_URI = uri;
    }

    public static void staticfiles(String path) {
        WEB_ROOT = path;
    }

    public static void runServer() {
        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started at port: " + PORT);
            while (RUNNING) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute( () -> {
                    try {
                        handleRequests(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }

    }

    private static void handleRequests(Socket clientSocket) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        BufferedOutputStream dataOut = new BufferedOutputStream(clientSocket.getOutputStream());

        String readline = in.readLine();
        if (readline == null){
            closeResources(clientSocket, in, out);
            return;
        }
        String[] parts = readline.split(" ");
        String httpVerb = parts[0];
        String resource = parts[1].equals("/") ? INDEX_PAGE_URI : parts[1];
        URI resourceUri = URI.create(resource);

        if (httpVerb.equals("GET") && !resource.startsWith("/app") && !resource.startsWith("/spring")) {
            System.out.println("GET request for: " + resource);
            handleGetRequests(resource, out, dataOut);
        } else if (resource.startsWith("/app")) {
            System.out.println("Request APP for: " + resource);
            handleAppRequests(httpVerb, resourceUri, out);
        } else if (resource.startsWith("/spring")) {
            System.out.println("Request SPRING for: " + resource);
            handleSpringRequests(httpVerb, resourceUri,out);
        } else {
            out.println(HTTP_400_BAD_REQUEST);
            out.println("Content-Type: text/html");
            out.println("\r\n");
            out.println("<html><body><h1>400 Bad Request</h1></body></html>");
            out.flush();
        }
         closeResources(clientSocket, in, out);
    }

    private static void handleSpringRequests(String method, URI resourceUri, PrintWriter out) {
        HttpRequest req = new HttpRequest(resourceUri.getPath(), resourceUri.getQuery(), method);
        out.print(callMicroSpringService(req));
        out.flush();
    }

    private static void handleAppRequests(String method, URI resourceUri, PrintWriter out) {
        HttpRequest req = new HttpRequest(resourceUri.getPath(), resourceUri.getQuery(), method);
        HttpResponse res = new HttpResponse();
        if (method.equals("GET")) {
            handleAppGetRequests(req, res, out);
        } else if (method.equals("POST")) {
            handleAppPostRequests(req, res, out);
        }
    }

    private static void handleAppGetRequests(HttpRequest req, HttpResponse res, PrintWriter out) {
        StringBuilder response = new StringBuilder();
        try {
            BiFunction<HttpRequest, HttpResponse, String> service = noteController.getServices(req.getPath());
            response.append("HTTP/1.1 200 OK\r\n");
            response.append("Content-Type: application/json\r\n");
            response.append("\r\n");
            response.append(service.apply(req, res));
        } catch (Exception e) {
            response = new StringBuilder();
            response.append("HTTP/1.1 404 Not Found\r\n");
            response.append("Content-Type: text/html\r\n");
            response.append("\r\n");
            response.append("<html><body><h1>404 Not Found</h1></body></html>");
        } finally {
            System.out.println(response.toString());
            out.print(response.toString());
            out.flush();
        }
    }

    private static void handleAppPostRequests(HttpRequest req, HttpResponse res, PrintWriter out) {
        StringBuilder response = new StringBuilder();
        try {
            BiFunction<HttpRequest, HttpResponse, String> service = noteController.postServices(req.getPath());
            String jsonResponse = service.apply(req, res);
            if (jsonResponse.startsWith("{ \"error\":")) {
                response.append(HTTP_400_BAD_REQUEST);
                response.append("Content-Type: application/json");
                response.append("\r\n");
                response.append(jsonResponse);
            } 
            else {
            response.append("HTTP/1.1 200 OK\r\n");
            response.append("Content-Type: application/json\r\n");
            response.append("\r\n");
            response.append(jsonResponse);
            }
        } catch (Exception e) {
            response.append(HTTP_400_BAD_REQUEST);
            response.append("Content-Type: text/html");
            response.append("\r\n");
            response.append("{ \"error\": " + "\"" + "Invalid POST request" + "\"}");
        } finally {
            System.out.println(response.toString());
            out.print(response.toString());
            out.flush();
        }
    }

    private static void handleGetRequests(String requestedResource, PrintWriter out, BufferedOutputStream dataOut)
            throws IOException {
        String contentType = getContentType(requestedResource);
        File resource = new File(WEB_ROOT, requestedResource);
        if (resource.exists() && !resource.isDirectory()) {
            int resourceLength = (int) resource.length();
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + contentType);
            out.println("Content-Length: " + resourceLength);
            out.println();
            out.flush();
            byte[] fileBytes = readBytesFromFile(resource, resourceLength);
            dataOut.write(fileBytes);
            dataOut.flush();
        } else {
            out.println("HTTP/1.1 404 Not Found");
            out.println("Content-Type: text/html");
            out.println("\r\n");
            out.println("<html><body><h1>404 Not Found</h1></body></html>");
            out.flush();
        }
    }

    private static String getContentType(String requestedResource) {
        if (requestedResource.endsWith(".html"))
            return "text/html";
        if (requestedResource.endsWith(".css"))
            return "text/css";
        if (requestedResource.endsWith(".js"))
            return "application/javascript";
        if (requestedResource.endsWith(".png"))
            return "image/png";
        if (requestedResource.endsWith(".jpg"))
            return "image/jpg";
        if (requestedResource.endsWith(".jpeg"))
            return "image/jpeg";
        return "text/plain";
    }

    private static byte[] readBytesFromFile(File file, int fileLength) throws IOException {
        byte[] fileBytes = new byte[fileLength];
        try (FileInputStream fileIn = new FileInputStream(file)) {
            fileIn.read(fileBytes);
        }
        return fileBytes;
    }

    private static void closeResources(Socket socket, BufferedReader in, PrintWriter out) {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
