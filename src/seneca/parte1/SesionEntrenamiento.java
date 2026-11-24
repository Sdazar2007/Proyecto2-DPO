package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class SesionEntrenamiento implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date fecha;
    private String hora;
    private int duracion;
    private Entrenador entrenador;
    private Instalacion instalacion;
    private List<Socio> convocados = new ArrayList<>();
    private List<Evaluacion> evaluaciones = new ArrayList<>();

    public SesionEntrenamiento() {}

    public SesionEntrenamiento(Date fecha, String hora, int duracion, Entrenador entrenador, Instalacion instalacion, List<Socio> convocados, List<Evaluacion> evaluaciones) {
        this.fecha = fecha;
        this.hora = hora;
        this.duracion = duracion;
        this.entrenador = entrenador;
        this.instalacion = instalacion;
        this.convocados = convocados;
        this.evaluaciones = evaluaciones;
    }

    public Date getFecha() { return fecha; }
    public void setFecha(Date value) { this.fecha = value; }

    public String getHora() { return hora; }
    public void setHora(String value) { this.hora = value; }

    public int getDuracion() { return duracion; }
    public void setDuracion(int value) { this.duracion = value; }

    public Entrenador getEntrenador() { return entrenador; }
    public void setEntrenador(Entrenador value) { this.entrenador = value; }

    public Instalacion getInstalacion() { return instalacion; }
    public void setInstalacion(Instalacion value) { this.instalacion = value; }

    public List<Socio> getConvocados() { return convocados; }
    public void setConvocados(List<Socio> value) { this.convocados = value; }

    public List<Evaluacion> getEvaluaciones() { return evaluaciones; }
    public void setEvaluaciones(List<Evaluacion> value) { this.evaluaciones = value; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final SesionEntrenamiento x = new SesionEntrenamiento();
        public Builder fecha(Date value) { x.setFecha(value); return this; }
        public Builder hora(String value) { x.setHora(value); return this; }
        public Builder duracion(int value) { x.setDuracion(value); return this; }
        public Builder entrenador(Entrenador value) { x.setEntrenador(value); return this; }
        public Builder instalacion(Instalacion value) { x.setInstalacion(value); return this; }
        public Builder convocados(List<Socio> value) { x.setConvocados(value); return this; }
        public Builder evaluaciones(List<Evaluacion> value) { x.setEvaluaciones(value); return this; }
        public SesionEntrenamiento build() { return x; }
    }
}
