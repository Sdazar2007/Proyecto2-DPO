package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class EstadisticaVoleibol extends EstadisticaJugador {
    private int aces;
    private int bloqueos;

    public EstadisticaVoleibol() {}

    public EstadisticaVoleibol(int aces, int bloqueos) {
        this.aces = aces;
        this.bloqueos = bloqueos;
    }

    public int getAces() { return aces; }
    public void setAces(int aces) { this.aces = aces; }

    public int getBloqueos() { return bloqueos; }
    public void setBloqueos(int bloqueos) { this.bloqueos = bloqueos; }

    public static Builder newBuilder() { return new Builder(); }

    public static class Builder {
        private final EstadisticaVoleibol instance = new EstadisticaVoleibol();
        public Builder aces(int value) { instance.setAces(value); return this; }
        public Builder bloqueos(int value) { instance.setBloqueos(value); return this; }
        public EstadisticaVoleibol build() { return instance; }
    }
}
