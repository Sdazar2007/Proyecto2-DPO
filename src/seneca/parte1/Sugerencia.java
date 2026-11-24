package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Sugerencia implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String estado;
    private Empleado proponente;
    private String tipo;
    private String motivo;

    public Sugerencia() {}

    public Sugerencia(String nombre, String estado, Empleado proponente, String tipo, String motivo) {
        this.nombre = nombre;
        this.estado = estado;
        this.proponente = proponente;
        this.tipo = tipo;
        this.motivo = motivo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }

    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }

    public Empleado getProponente() { return proponente; }
    public void setProponente(Empleado value) { this.proponente = value; }

    public String getTipo() { return tipo; }
    public void setTipo(String value) { this.tipo = value; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String value) { this.motivo = value; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Sugerencia x = new Sugerencia();
        public Builder nombre(String value) { x.setNombre(value); return this; }
        public Builder estado(String value) { x.setEstado(value); return this; }
        public Builder proponente(Empleado value) { x.setProponente(value); return this; }
        public Builder tipo(String value) { x.setTipo(value); return this; }
        public Builder motivo(String value) { x.setMotivo(value); return this; }
        public Sugerencia build() { return x; }
    }
}
