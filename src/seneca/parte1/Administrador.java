package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Administrador extends Usuario {
    private static final long serialVersionUID = 1L;

    public Administrador() {}

    public static Builder nuevoBuilder() { return new Builder(); }
    public static class Builder {
        private final Administrador x = new Administrador();
        public Administrador build() { return x; }
    }
}
