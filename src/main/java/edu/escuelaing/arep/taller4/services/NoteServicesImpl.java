package edu.escuelaing.arep.taller4.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import edu.escuelaing.arep.taller4.model.Note;
import edu.escuelaing.arep.taller4.model.NoteGroup;
import edu.escuelaing.arep.taller4.services.exception.NoteServicesException;

public class NoteServicesImpl implements NoteServices {

    private ArrayList<Note> notes = new ArrayList<>();

    @Override
    public ArrayList<Note> getNotes() {
        return notes;
    }

    @Override
    public void addNote(String title, String group, String content) throws NoteServicesException {
        if (title.isEmpty() || group.isEmpty() || content.isEmpty()) {
            throw new NoteServicesException(NoteServicesException.EMPTY_PARAMETERS);
        }
        try {
            NoteGroup noteGroup = NoteGroup.valueOf(group.toUpperCase());
            notes.add(new Note(title, noteGroup, content, LocalDate.now()));
        } catch (IllegalArgumentException e) {
            throw new NoteServicesException(NoteServicesException.INVALID_GROUP);
        }

    }

    @Override
    public String getNotesAsJSON() {
        return "[" + getNotes().stream()
                .map(note -> String.format(
                        "{\"title\":\"%s\", \"group\":\"%s\", \"content\":\"%s\", \"date\":\"%s\"}",
                        note.getTitle(),
                        note.getGroup().name(),
                        note.getContent(),
                        note.getDate().toString()))
                .collect(Collectors.joining(","))
                + "]";
    }

    @Override
    public void addNote(Map<String, String> noteValues) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addNote'");
    }

}
