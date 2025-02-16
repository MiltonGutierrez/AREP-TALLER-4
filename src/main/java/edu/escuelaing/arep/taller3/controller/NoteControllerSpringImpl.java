package edu.escuelaing.arep.taller3.controller;

import java.util.ArrayList;
import java.util.Map;


import edu.escuelaing.arep.taller3.model.Note;
import edu.escuelaing.arep.taller3.server.annotations.GetMapping;
import edu.escuelaing.arep.taller3.server.annotations.PostMapping;
import edu.escuelaing.arep.taller3.server.annotations.RequestBody;
import edu.escuelaing.arep.taller3.server.annotations.RestController;
import edu.escuelaing.arep.taller3.services.NoteServices;
import edu.escuelaing.arep.taller3.services.exception.NoteServicesException;

@RestController
public class NoteControllerSpringImpl implements NoteControllerSpring{

    private static NoteServices noteServices;

    public NoteControllerSpringImpl(NoteServices noteServices){
        this.noteServices = noteServices;
    }

    @Override
    @GetMapping("/app/note")
    public String getNotes() {
        return noteServices.getNotesAsJSON();
    }

    @Override
    @PostMapping("/app/note")
    public void createNote(@RequestBody Map<String, String> noteValues) throws NoteServicesException {
        noteServices.addNote(noteValues);
    }
    
    
}
