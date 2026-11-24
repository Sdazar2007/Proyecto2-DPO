package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class EstadisticaBaloncesto extends EstadisticaJugador {
    private int puntos;
    private int rebotes;

    public EstadisticaBaloncesto() {}

    public EstadisticaBaloncesto(int puntos, int rebotes) {
        this.puntos = puntos;
        this.rebotes = rebotes;
    }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    public int getRebotes() { return rebotes; }
    public void setRebotes(int rebotes) { this.rebotes = rebotes; }

    public static Builder newBuilder() { return new Builder(); }

    public static class Builder {
        private final EstadisticaBaloncesto instance = new EstadisticaBaloncesto();
        public Builder puntos(int value) { instance.setPuntos(value); return this; }
        public Builder rebotes(int value) { instance.setRebotes(value); return this; }
        public EstadisticaBaloncesto build() { return instance; }
    }
}
