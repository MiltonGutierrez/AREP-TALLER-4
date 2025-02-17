package edu.escuelaing.arep.taller4.services;

import java.util.List;
import java.util.Map;

import edu.escuelaing.arep.taller4.model.Note;
import edu.escuelaing.arep.taller4.services.exception.NoteServicesException;


public interface NoteServices {
    List<Note> getNotes();
    void addNote(String title, String group, String content) throws NoteServicesException;
    void addNote(Map<String, String> noteValues) throws NoteServicesException;
    String getNotesAsJSON();
}
