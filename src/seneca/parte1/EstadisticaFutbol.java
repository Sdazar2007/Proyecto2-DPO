package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class EstadisticaFutbol extends EstadisticaJugador {
    private int goles;
    private int asistencias;
    private int amarillas;
    private int rojas;

    public EstadisticaFutbol() {}

    public EstadisticaFutbol(int goles, int asistencias, int amarillas, int rojas) {
        this.goles = goles;
        this.asistencias = asistencias;
        this.amarillas = amarillas;
        this.rojas = rojas;
    }

    public int getGoles() { return goles; }
    public void setGoles(int goles) { this.goles = goles; }

    public int getAsistencias() { return asistencias; }
    public void setAsistencias(int asistencias) { this.asistencias = asistencias; }

    public int getAmarillas() { return amarillas; }
    public void setAmarillas(int amarillas) { this.amarillas = amarillas; }

    public int getRojas() { return rojas; }
    public void setRojas(int rojas) { this.rojas = rojas; }

    public static Builder newBuilder() { return new Builder(); }

    public static class Builder {
        private final EstadisticaFutbol instance = new EstadisticaFutbol();
        public Builder goles(int value) { instance.setGoles(value); return this; }
        public Builder asistencias(int value) { instance.setAsistencias(value); return this; }
        public Builder amarillas(int value) { instance.setAmarillas(value); return this; }
        public Builder rojas(int value) { instance.setRojas(value); return this; }
        public EstadisticaFutbol build() { return instance; }
    }
}
