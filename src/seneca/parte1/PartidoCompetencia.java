package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class PartidoCompetencia extends Partido {
    private Competencia competencia;
    private Equipo equipo;
    private InstitucionRival rival;

    public PartidoCompetencia() {}

    public PartidoCompetencia(Competencia competencia, Equipo equipo, InstitucionRival rival) {
        this.competencia = competencia;
        this.equipo = equipo;
        this.rival = rival;
    }

    public Competencia getCompetencia() { return competencia; }
    public void setCompetencia(Competencia competencia) { this.competencia = competencia; }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public InstitucionRival getRival() { return rival; }
    public void setRival(InstitucionRival rival) { this.rival = rival; }

    public static Builder newBuilder() { return new Builder(); }

    public static class Builder {
        private final PartidoCompetencia instance = new PartidoCompetencia();
        public Builder competencia(Competencia value) { instance.setCompetencia(value); return this; }
        public Builder equipo(Equipo value) { instance.setEquipo(value); return this; }
        public Builder rival(InstitucionRival value) { instance.setRival(value); return this; }
        public PartidoCompetencia build() { return instance; }
    }
}
