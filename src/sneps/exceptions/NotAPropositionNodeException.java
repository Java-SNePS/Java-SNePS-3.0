package sneps.exceptions;

public class NotAPropositionNodeException extends Exception {

    public NotAPropositionNodeException() {
        super("This node isn't a proposition node");
    }
}
