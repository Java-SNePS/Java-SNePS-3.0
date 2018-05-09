package sneps.exceptions;

public class ContextNameDoesntExist extends Throwable {

    String contextName;
    public ContextNameDoesntExist(String contextName) {
        super("A context with this name doesn't exist");
        this.contextName = contextName;
    }
}
