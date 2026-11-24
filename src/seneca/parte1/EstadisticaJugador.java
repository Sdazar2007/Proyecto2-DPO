package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class EstadisticaJugador implements Serializable {
    private static final long serialVersionUID = 1L;
    private Socio socio;
    private Partido partido;

    public EstadisticaJugador() {}

    public EstadisticaJugador(Socio socio, Partido partido) {
        this.socio = socio;
        this.partido = partido;
    }

    public Socio getSocio() { return socio; }
    public void setSocio(Socio socio) { this.socio = socio; }

    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final EstadisticaJugador instance = new EstadisticaJugador();
        public Builder socio(Socio value) { instance.setSocio(value); return this; }
        public Builder partido(Partido value) { instance.setPartido(value); return this; }
        public EstadisticaJugador build() { return instance; }
    }
}
