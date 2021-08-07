package sneps.exceptions;

import sneps.snebr.Context;
import sneps.snebr.Controller;

public class DuplicateContextNameException extends Exception {
    Context duplicatedContext;

    public DuplicateContextNameException(String contextName)  {
        super("A context with this name exist");
        this.duplicatedContext = Controller.getContextByName(contextName);
    }
}
