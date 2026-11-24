package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class InscripcionCompetencia implements Serializable {
    private static final long serialVersionUID = 1L;
    private Equipo equipo;
    private Competencia competencia;

    public InscripcionCompetencia() {}

    public InscripcionCompetencia(Equipo equipo, Competencia competencia) {
        this.equipo = equipo;
        this.competencia = competencia;
    }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public Competencia getCompetencia() { return competencia; }
    public void setCompetencia(Competencia competencia) { this.competencia = competencia; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final InscripcionCompetencia instance = new InscripcionCompetencia();
        public Builder equipo(Equipo value) { instance.setEquipo(value); return this; }
        public Builder competencia(Competencia value) { instance.setCompetencia(value); return this; }
        public InscripcionCompetencia build() { return instance; }
    }
}
