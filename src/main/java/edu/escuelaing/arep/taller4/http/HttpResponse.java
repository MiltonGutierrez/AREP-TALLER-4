package edu.escuelaing.arep.taller4.http;

public class HttpResponse {
    public static final String OK = "HTTP/1.1 200 OK\r\n";
    public static final String NOT_FOUND = "HTTP/1.1 404 Not Found\r\n";
    public static final String BAD_REQUEST = "HTTP/1.1 400 Bad Request\r\n";
    public static final String INTERNAL_SERVER_ERROR = "HTTP/1.1 500 Internal Server Error\r\n";
    public static final String CREATED = "HTTP/1.1 201 Created\r\n";
}
