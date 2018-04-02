package sneps.exceptions;

import sneps.snebr.Context;
import sneps.snebr.Controller;

public class ContextNameDuplicateException extends Exception {
    Context duplicatedContext;

    public ContextNameDuplicateException(String contextName) {
        super("A context with this name exist");
        this.duplicatedContext = Controller.getContextByName(contextName);
    }
}
