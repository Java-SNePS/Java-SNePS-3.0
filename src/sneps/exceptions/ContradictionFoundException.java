package sneps.exceptions;

import sneps.snebr.Context;
import sneps.snebr.Contradiction;
import sneps.snebr.Controller;

public class ContradictionFoundException extends Exception{

    private Contradiction contradiciton;

    public ContradictionFoundException(Contradiction con)  {
        super("A context with this name exist");
        this.contradiciton = con;
    }
}
