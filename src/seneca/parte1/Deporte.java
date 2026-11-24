package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class Deporte implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;

    public Deporte() {}

    public Deporte(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Deporte instance = new Deporte();
        public Builder nombre(String value) { instance.setNombre(value); return this; }
        public Deporte build() { return instance; }
    }
}
