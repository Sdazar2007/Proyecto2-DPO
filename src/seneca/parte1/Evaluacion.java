package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Evaluacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private String asistencia;
    private double notaDesempeno;
    private String comentario;
    private Socio socio;
    private SesionEntrenamiento sesion;

    public Evaluacion() {}

    public Evaluacion(String asistencia, double notaDesempeno, String comentario, Socio socio, SesionEntrenamiento sesion) {
        this.asistencia = asistencia;
        this.notaDesempeno = notaDesempeno;
        this.comentario = comentario;
        this.socio = socio;
        this.sesion = sesion;
    }

    public String getAsistencia() { return asistencia; }
    public void setAsistencia(String value) { this.asistencia = value; }

    public double getNotaDesempeno() { return notaDesempeno; }
    public void setNotaDesempeno(double value) { this.notaDesempeno = value; }

    public String getComentario() { return comentario; }
    public void setComentario(String value) { this.comentario = value; }

    public Socio getSocio() { return socio; }
    public void setSocio(Socio value) { this.socio = value; }

    public SesionEntrenamiento getSesion() { return sesion; }
    public void setSesion(SesionEntrenamiento value) { this.sesion = value; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Evaluacion x = new Evaluacion();
        public Builder asistencia(String value) { x.setAsistencia(value); return this; }
        public Builder notaDesempeno(double value) { x.setNotaDesempeno(value); return this; }
        public Builder comentario(String value) { x.setComentario(value); return this; }
        public Builder socio(Socio value) { x.setSocio(value); return this; }
        public Builder sesion(SesionEntrenamiento value) { x.setSesion(value); return this; }
        public Evaluacion build() { return x; }
    }
}
