package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Empleado extends Usuario {
    private static final long serialVersionUID = 1L;
    private String codigoDescuento;

    public Empleado() {}

    public Empleado(String codigoDescuento) {
        this.codigoDescuento = codigoDescuento;
    }

    public String getCodigoDescuento() { return codigoDescuento; }
    public void setCodigoDescuento(String value) { this.codigoDescuento = value; }

    public static Builder nuevoBuilder() { return new Builder(); }
    public static class Builder {
        private final Empleado x = new Empleado();
        public Builder codigoDescuento(String value) { x.setCodigoDescuento(value); return this; }
        public Empleado build() { return x; }
    }
}
