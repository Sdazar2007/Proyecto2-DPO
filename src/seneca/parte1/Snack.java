package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Snack extends Producto {
    private static final long serialVersionUID = 1L;
    private String alergenos;

    public Snack() {}

    public Snack(String alergenos) {
        this.alergenos = alergenos;
    }

    public String getAlergenos() { return alergenos; }
    public void setAlergenos(String value) { this.alergenos = value; }

    public static Builder nuevoBuilder() { return new Builder(); }
    public static class Builder {
        private final Snack x = new Snack();
        public Builder alergenos(String value) { x.setAlergenos(value); return this; }
        public Snack build() { return x; }
    }
}
