package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class InstitucionRival implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;

    public InstitucionRival() {}

    public InstitucionRival(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final InstitucionRival instance = new InstitucionRival();
        public Builder nombre(String value) { instance.setNombre(value); return this; }
        public InstitucionRival build() { return instance; }
    }
}
