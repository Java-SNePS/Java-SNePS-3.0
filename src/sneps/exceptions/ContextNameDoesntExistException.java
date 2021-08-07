package sneps.exceptions;

public class ContextNameDoesntExistException extends Throwable {

    String contextName;
    public ContextNameDoesntExistException(String contextName) {
        super("A context with this name doesn't exist");
        this.contextName = contextName;
    }

    public ContextNameDoesntExistException() {

    }
}
