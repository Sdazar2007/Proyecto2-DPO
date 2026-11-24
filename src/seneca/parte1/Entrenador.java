package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Entrenador extends Empleado {
    private static final long serialVersionUID = 1L;

    public Entrenador() {}

    public static Builder nuevoBuilderEntrenador() { return new Builder(); }
    public static class Builder {
        private final Entrenador x = new Entrenador();
        public Entrenador build() { return x; }
    }
}
