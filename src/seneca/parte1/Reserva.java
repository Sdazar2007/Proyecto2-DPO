package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;
    private String bloqueHorario;
    private int numeroJugadores;
    private String modalidad;
    private Socio responsable;
    private Instalacion instalacion;
    private List<Socio> participantes = new ArrayList<>();
    private boolean cancelada;

    public Reserva() {}

    public Reserva(String bloqueHorario, int numeroJugadores, String modalidad, Socio responsable, Instalacion instalacion, List<Socio> participantes, boolean cancelada) {
        this.bloqueHorario = bloqueHorario;
        this.numeroJugadores = numeroJugadores;
        this.modalidad = modalidad;
        this.responsable = responsable;
        this.instalacion = instalacion;
        this.participantes = participantes;
        this.cancelada = cancelada;
    }

    public String getBloqueHorario() { return bloqueHorario; }
    public void setBloqueHorario(String value) { this.bloqueHorario = value; }

    public int getNumeroJugadores() { return numeroJugadores; }
    public void setNumeroJugadores(int value) { this.numeroJugadores = value; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String value) { this.modalidad = value; }

    public Socio getResponsable() { return responsable; }
    public void setResponsable(Socio value) { this.responsable = value; }

    public Instalacion getInstalacion() { return instalacion; }
    public void setInstalacion(Instalacion value) { this.instalacion = value; }

    public List<Socio> getParticipantes() { return participantes; }
    public void setParticipantes(List<Socio> value) { this.participantes = value; }

    public boolean getCancelada() { return cancelada; }
    public void setCancelada(boolean value) { this.cancelada = value; }
    public boolean isCancelada() { return cancelada; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Reserva x = new Reserva();
        public Builder bloqueHorario(String value) { x.setBloqueHorario(value); return this; }
        public Builder numeroJugadores(int value) { x.setNumeroJugadores(value); return this; }
        public Builder modalidad(String value) { x.setModalidad(value); return this; }
        public Builder responsable(Socio value) { x.setResponsable(value); return this; }
        public Builder instalacion(Instalacion value) { x.setInstalacion(value); return this; }
        public Builder participantes(List<Socio> value) { x.setParticipantes(value); return this; }
        public Builder cancelada(boolean value) { x.setCancelada(value); return this; }
        public Reserva build() { return x; }
    }
}
