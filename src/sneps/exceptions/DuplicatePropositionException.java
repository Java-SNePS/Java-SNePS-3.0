package sneps.exceptions;

public class DuplicatePropositionException extends Exception {
    public DuplicatePropositionException() {
        super("This is a duplicate proposition!");
    }
}
