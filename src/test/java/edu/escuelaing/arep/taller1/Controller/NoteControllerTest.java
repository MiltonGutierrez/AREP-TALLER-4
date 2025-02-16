package edu.escuelaing.arep.taller1.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import edu.escuelaing.arep.taller3.controller.NoteControllerImpl;
import edu.escuelaing.arep.taller3.http.HttpRequest;
import edu.escuelaing.arep.taller3.http.HttpResponse;
import edu.escuelaing.arep.taller3.services.NoteServices;
import edu.escuelaing.arep.taller3.services.NoteServicesImpl;
import edu.escuelaing.arep.taller3.services.exception.NoteServicesException;

import static edu.escuelaing.arep.taller3.controller.NoteControllerImpl.get;
import static edu.escuelaing.arep.taller3.controller.NoteControllerImpl.post;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;


class NoteControllerTest {

    private NoteControllerImpl noteController;
    private NoteServices noteServices;

    @BeforeEach
    public void setUp() {
        noteServices = new NoteServicesImpl();
        noteController = new NoteControllerImpl();

        get("/note", (req, res) -> {
            return "[" + noteServices.getNotes().stream()
                    .map(note -> String.format(
                            "{\"title\":\"%s\", \"group\":\"%s\", \"content\":\"%s\", \"date\":\"%s\"}",
                            note.getTitle(),
                            note.getGroup().name(),
                            note.getContent(),
                            note.getDate().toString()))
                    .collect(Collectors.joining(","))
                    + "]";
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
    }

    @Test
    void testGetNotesResponseShouldReturnEmptyArray() {
        String responseByController = noteController.getServices("/app/note").apply(null, null);
        String responseThatShouldReturn = "[" + "]";
        assertEquals(responseByController, responseThatShouldReturn);
    }

    @Test
    void testGetNotesResponseShouldReturnArrayWithCreatedNotes() throws NoteServicesException {
        noteServices.addNote("TEST", "personal", "Test text");
        noteServices.addNote("TEST2", "work", "Test text 2");
        noteServices.addNote("TEST3", "personal", "Test text 3");

        String responseByController = noteController.getServices("/app/note").apply(null, null);

        StringBuilder responseThatShouldReturn = new StringBuilder();
        responseThatShouldReturn.append("[" +
                "{\"title\":\"TEST\", \"group\":\"PERSONAL\", \"content\":\"Test text\", \"date\":\""
                + java.time.LocalDate.now() + "\"}," +
                "{\"title\":\"TEST2\", \"group\":\"WORK\", \"content\":\"Test text 2\", \"date\":\""
                + java.time.LocalDate.now() + "\"}," +
                "{\"title\":\"TEST3\", \"group\":\"PERSONAL\", \"content\":\"Test text 3\", \"date\":\""
                + java.time.LocalDate.now() + "\"}" +
                "]");
        assertEquals(responseByController, responseThatShouldReturn.toString());
    }


    @Test
    void testPostNoteResponseShouldHandleErrors() {
        String path = "/app/note";
        String method = "POST";
        HttpRequest req = new HttpRequest(path,"title=&group=personal&content=hola", method);
        String expectedError = "Some parameters are empty";
        String responseByController = noteController.postServices("/app/note").apply(req, new HttpResponse());
        String responseThatShouldReturn = "{ \"error\": " + "\"" + expectedError + "\"}";
        assertEquals(responseByController, responseThatShouldReturn);

        req = new HttpRequest(path,"title=hola&group=hi&content=hola", method);
        expectedError = "Invalid group";
        responseByController = noteController.postServices("/app/note").apply(req, new HttpResponse());
        responseThatShouldReturn = "{ \"error\": " + "\"" + expectedError + "\"}";
        assertEquals(responseByController, responseThatShouldReturn);

        req = new HttpRequest(path,"title=&group=personal&content=", method);
        expectedError = "Some parameters are empty";
        responseByController = noteController.postServices("/app/note").apply(req, new HttpResponse());
        responseThatShouldReturn = "{ \"error\": " + "\"" + expectedError + "\"}";
        assertEquals(responseByController, responseThatShouldReturn);

        req = new HttpRequest(path,"title=&group=&content=", method);
        expectedError = "Some parameters are empty";
        responseByController = noteController.postServices("/app/note").apply(req, new HttpResponse());
        responseThatShouldReturn = "{ \"error\": " + "\"" + expectedError + "\"}";
        assertEquals(responseByController, responseThatShouldReturn);

    }

    @Test
    void testPostNoteResponseShouldReturnNote() {
        String path = "/app/note";
        HttpRequest req = new HttpRequest(path,"title=hola&group=personal&content=hola", "POST");
        String responseByController = noteController.postServices("/app/note").apply(req, new HttpResponse());
        String responseThatShouldReturn = "{ \"title\": " + "\"hola\", " + "\"group\": " + "\"personal\", "
                + "\"content\": " + "\"hola\" " + "}";
                
        assertEquals(responseByController, responseThatShouldReturn);
    }

}
