package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class PartidoInterno extends Partido {
    private Equipo equipoLocal;
    private Equipo equipoVisitante;

    public PartidoInterno() {}

    public PartidoInterno(Equipo equipoLocal, Equipo equipoVisitante) {
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
    }

    public Equipo getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(Equipo equipoLocal) { this.equipoLocal = equipoLocal; }

    public Equipo getEquipoVisitante() { return equipoVisitante; }
    public void setEquipoVisitante(Equipo equipoVisitante) { this.equipoVisitante = equipoVisitante; }

    public static Builder newBuilder() { return new Builder(); }

    public static class Builder {
        private final PartidoInterno instance = new PartidoInterno();
        public Builder equipoLocal(Equipo value) { instance.setEquipoLocal(value); return this; }
        public Builder equipoVisitante(Equipo value) { instance.setEquipoVisitante(value); return this; }
        public PartidoInterno build() { return instance; }
    }
}
