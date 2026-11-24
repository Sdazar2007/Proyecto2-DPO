package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class InscripcionEquipo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String posicion;
    private int numeroCamiseta;
    private Socio socio;
    private Equipo equipo;

    public InscripcionEquipo() {}

    public InscripcionEquipo(String posicion, int numeroCamiseta, Socio socio, Equipo equipo) {
        this.posicion = posicion;
        this.numeroCamiseta = numeroCamiseta;
        this.socio = socio;
        this.equipo = equipo;
    }

    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }

    public int getNumeroCamiseta() { return numeroCamiseta; }
    public void setNumeroCamiseta(int numeroCamiseta) { this.numeroCamiseta = numeroCamiseta; }

    public Socio getSocio() { return socio; }
    public void setSocio(Socio socio) { this.socio = socio; }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final InscripcionEquipo instance = new InscripcionEquipo();
        public Builder posicion(String value) { instance.setPosicion(value); return this; }
        public Builder numeroCamiseta(int value) { instance.setNumeroCamiseta(value); return this; }
        public Builder socio(Socio value) { instance.setSocio(value); return this; }
        public Builder equipo(Equipo value) { instance.setEquipo(value); return this; }
        public InscripcionEquipo build() { return instance; }
    }
}
