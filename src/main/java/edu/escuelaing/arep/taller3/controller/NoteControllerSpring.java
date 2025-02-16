package edu.escuelaing.arep.taller3.controller;


import java.util.Map;
import edu.escuelaing.arep.taller3.services.exception.NoteServicesException;

public interface NoteControllerSpring {

    String getNotes();

    void createNote(Map<String, String> noteValues) throws NoteServicesException;
    
}
