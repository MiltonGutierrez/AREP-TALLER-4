package edu.escuelaing.arep.taller4.services.exception;

public class NoteServicesException extends Exception {

    public static final String INVALID_GROUP = "Invalid group";
    public static final String EMPTY_PARAMETERS = "Some parameters are empty";

    public NoteServicesException(String message) {
        super(message);
    }
    
}
