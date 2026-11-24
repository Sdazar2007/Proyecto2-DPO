package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class PartidoPractica implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date fecha;
    private Deporte deporte;
    private Instalacion instalacion;
    private List<Socio> participantes = new ArrayList<>();
    private List<Socio> ganadores = new ArrayList<>();
    private boolean finalizado;

    public PartidoPractica() {}

    public PartidoPractica(Date fecha, Deporte deporte, Instalacion instalacion, List<Socio> participantes, List<Socio> ganadores, boolean finalizado) {
        this.fecha = fecha;
        this.deporte = deporte;
        this.instalacion = instalacion;
        this.participantes = participantes;
        this.ganadores = ganadores;
        this.finalizado = finalizado;
    }

    public Date getFecha() { return fecha; }
    public void setFecha(Date value) { this.fecha = value; }

    public Deporte getDeporte() { return deporte; }
    public void setDeporte(Deporte value) { this.deporte = value; }

    public Instalacion getInstalacion() { return instalacion; }
    public void setInstalacion(Instalacion value) { this.instalacion = value; }

    public List<Socio> getParticipantes() { return participantes; }
    public void setParticipantes(List<Socio> value) { this.participantes = value; }

    public List<Socio> getGanadores() { return ganadores; }
    public void setGanadores(List<Socio> value) { this.ganadores = value; }

    public boolean getFinalizado() { return finalizado; }
    public void setFinalizado(boolean value) { this.finalizado = value; }
    public boolean isFinalizado() { return finalizado; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final PartidoPractica x = new PartidoPractica();
        public Builder fecha(Date value) { x.setFecha(value); return this; }
        public Builder deporte(Deporte value) { x.setDeporte(value); return this; }
        public Builder instalacion(Instalacion value) { x.setInstalacion(value); return this; }
        public Builder participantes(List<Socio> value) { x.setParticipantes(value); return this; }
        public Builder ganadores(List<Socio> value) { x.setGanadores(value); return this; }
        public Builder finalizado(boolean value) { x.setFinalizado(value); return this; }
        public PartidoPractica build() { return x; }
    }
}
