package edu.escuelaing.arep.taller4.controller;

import java.util.Map;

import edu.escuelaing.arep.taller4.server.annotations.GetMapping;
import edu.escuelaing.arep.taller4.server.annotations.PostMapping;
import edu.escuelaing.arep.taller4.server.annotations.RequestBody;
import edu.escuelaing.arep.taller4.server.annotations.RestController;
import edu.escuelaing.arep.taller4.services.NoteServices;
import edu.escuelaing.arep.taller4.services.NoteServicesImpl;
import edu.escuelaing.arep.taller4.services.exception.NoteServicesException;

@RestController
public class NoteControllerSpringImpl{

    private static NoteServices noteServices = new NoteServicesImpl();

    @GetMapping("/spring/note")
    public static String getNotes() {
        return noteServices.getNotesAsJSON();
    }

    @PostMapping("/spring/note")
    public static void createNote(@RequestBody Map<String, String> noteValues) throws NoteServicesException {
        noteServices.addNote(noteValues);
    }
    
    
}
