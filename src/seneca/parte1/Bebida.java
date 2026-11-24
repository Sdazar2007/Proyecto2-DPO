package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Bebida extends Producto {
    private static final long serialVersionUID = 1L;
    private boolean esCaliente;

    public Bebida() {}

    public Bebida(boolean esCaliente) {
        this.esCaliente = esCaliente;
    }

    public boolean getEsCaliente() { return esCaliente; }
    public void setEsCaliente(boolean value) { this.esCaliente = value; }
    public boolean isEsCaliente() { return esCaliente; }

    public static Builder nuevoBuilder() { return new Builder(); }
    public static class Builder {
        private final Bebida x = new Bebida();
        public Builder esCaliente(boolean value) { x.setEsCaliente(value); return this; }
        public Bebida build() { return x; }
    }
}
