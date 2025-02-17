package edu.escuelaing.arep.taller4;

import static edu.escuelaing.arep.taller4.controller.NoteControllerImpl.get;
import static edu.escuelaing.arep.taller4.controller.NoteControllerImpl.post;
import static edu.escuelaing.arep.taller4.server.HttpServer.staticfiles;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import edu.escuelaing.arep.taller4.server.HttpServer;
import edu.escuelaing.arep.taller4.server.MicroSpring;
import edu.escuelaing.arep.taller4.services.NoteServicesImpl;

public class App {

    public static final NoteServicesImpl noteServices = new NoteServicesImpl();

    public static void main(String[] args){
        staticfiles("target/classes/webroot");

        get("/note", (req, res) -> {
            return noteServices.getNotesAsJSON();
        });

        get("/pi", (req, resp) -> {
            return String.valueOf(Math.PI);
        });

        post("/note", (req, res) -> {
            String title = req.getQueryParams().get("title");
            String group = req.getQueryParams().get("group");
            String content = req.getQueryParams().get("content");
            try {
                noteServices.addNote(title, group, content);
                return "{ \"title\": " + "\"" + title + "\", " + "\"group\": " + "\"" + group + "\", "
                        + "\"content\": " + "\"" + content + "\" " + "}";
            } catch (Exception e) {
                return "{ \"error\": " + "\"" + e.getMessage() + "\"}";
            }
        });

        MicroSpring.start();

        Thread server = new Thread(HttpServer::runServer);
        server.start();

        Scanner scanner = new Scanner(System.in);

        String line = " ";
        while (!line.equals("")) {
            System.out.println("Press Enter to stop the server");
            line = scanner.nextLine();
        }
        HttpServer.stopServer();    
        
        scanner.close();
        

    }
}
