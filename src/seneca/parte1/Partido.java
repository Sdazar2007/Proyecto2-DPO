package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class Partido implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date fecha;
    private String marcador;
    private Escenario escenario;

    public Partido() {}

    public Partido(Date fecha, String marcador, Escenario escenario) {
        this.fecha = fecha;
        this.marcador = marcador;
        this.escenario = escenario;
    }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getMarcador() { return marcador; }
    public void setMarcador(String marcador) { this.marcador = marcador; }

    public Escenario getEscenario() { return escenario; }
    public void setEscenario(Escenario escenario) { this.escenario = escenario; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Partido instance = new Partido();
        public Builder fecha(Date value) { instance.setFecha(value); return this; }
        public Builder marcador(String value) { instance.setMarcador(value); return this; }
        public Builder escenario(Escenario value) { instance.setEscenario(value); return this; }
        public Partido build() { return instance; }
    }
}
