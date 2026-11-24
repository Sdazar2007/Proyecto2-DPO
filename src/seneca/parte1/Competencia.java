package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class Competencia implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String entidadOrganizadora;
    private int anioTemporada;
    private String formato;

    public Competencia() {}

    public Competencia(String nombre, String entidadOrganizadora, int anioTemporada, String formato) {
        this.nombre = nombre;
        this.entidadOrganizadora = entidadOrganizadora;
        this.anioTemporada = anioTemporada;
        this.formato = formato;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEntidadOrganizadora() { return entidadOrganizadora; }
    public void setEntidadOrganizadora(String entidadOrganizadora) { this.entidadOrganizadora = entidadOrganizadora; }

    public int getAnioTemporada() { return anioTemporada; }
    public void setAnioTemporada(int anioTemporada) { this.anioTemporada = anioTemporada; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Competencia instance = new Competencia();
        public Builder nombre(String value) { instance.setNombre(value); return this; }
        public Builder entidadOrganizadora(String value) { instance.setEntidadOrganizadora(value); return this; }
        public Builder anioTemporada(int value) { instance.setAnioTemporada(value); return this; }
        public Builder formato(String value) { instance.setFormato(value); return this; }
        public Competencia build() { return instance; }
    }
}
