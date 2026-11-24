package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class NominaCompetencia implements Serializable {
    private static final long serialVersionUID = 1L;
    private InscripcionCompetencia inscripcion;
    private List<Socio> jugadores = new ArrayList<>();

    public NominaCompetencia() {}

    public NominaCompetencia(InscripcionCompetencia inscripcion, List<Socio> jugadores) {
        this.inscripcion = inscripcion;
        this.jugadores = jugadores;
    }

    public InscripcionCompetencia getInscripcion() { return inscripcion; }
    public void setInscripcion(InscripcionCompetencia inscripcion) { this.inscripcion = inscripcion; }

    public List<Socio> getJugadores() { return jugadores; }
    public void setJugadores(List<Socio> jugadores) { this.jugadores = jugadores; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final NominaCompetencia instance = new NominaCompetencia();
        public Builder inscripcion(InscripcionCompetencia value) { instance.setInscripcion(value); return this; }
        public Builder jugadores(List<Socio> value) { instance.setJugadores(value); return this; }
        public NominaCompetencia build() { return instance; }
    }
}
