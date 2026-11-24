package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Fisioterapeuta extends Empleado {
    private static final long serialVersionUID = 1L;

    public Fisioterapeuta() {}

    public static Builder nuevoBuilderFisioterapeuta() { return new Builder(); }
    public static class Builder {
        private final Fisioterapeuta x = new Fisioterapeuta();
        public Fisioterapeuta build() { return x; }
    }
}
