package edu.escuelaing.arep.taller3.controller;

import java.util.ArrayList;
import java.util.Map;

import edu.escuelaing.arep.taller3.model.Note;
import edu.escuelaing.arep.taller3.server.annotations.RequestBody;
import edu.escuelaing.arep.taller3.services.exception.NoteServicesException;

public interface NoteControllerSpring {

    String getNotes();

    void createNote(@RequestBody Map<String, String> noteValues) throws NoteServicesException;
    
}
