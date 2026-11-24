package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class Socio extends Usuario {
    private int edad;
    private int puntos;
    private List<InscripcionEquipo> inscripciones = new ArrayList<>();

    public Socio() {}

    public Socio(int edad, List<InscripcionEquipo> inscripciones) {
        this.edad = edad;
        this.inscripciones = inscripciones;
    }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { if(puntos < 0) throw new IllegalArgumentException("Puntos negativos"); this.puntos = puntos; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public List<InscripcionEquipo> getInscripciones() { return inscripciones; }
    public void setInscripciones(List<InscripcionEquipo> inscripciones) { this.inscripciones = inscripciones; }

    public static Builder newBuilder() { return new Builder(); }

    public static class Builder {
        private final Socio instance = new Socio();
        public Builder puntos(int value) { instance.setPuntos(value); return this; }
        public Builder edad(int value) { instance.setEdad(value); return this; }
        public Builder inscripciones(List<InscripcionEquipo> value) { instance.setInscripciones(value); return this; }
        public Socio build() { return instance; }
    }
}
