package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Instalacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String tipo;
    private int capacidad;
    private Deporte deporte;
    private boolean techada;
    private boolean activa;

    public Instalacion() {}

    public Instalacion(String nombre, String tipo, int capacidad, Deporte deporte, boolean techada, boolean activa) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.deporte = deporte;
        this.techada = techada;
        this.activa = activa;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }

    public String getTipo() { return tipo; }
    public void setTipo(String value) { this.tipo = value; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int value) { this.capacidad = value; }

    public Deporte getDeporte() { return deporte; }
    public void setDeporte(Deporte value) { this.deporte = value; }

    public boolean getTechada() { return techada; }
    public void setTechada(boolean value) { this.techada = value; }
    public boolean isTechada() { return techada; }

    public boolean getActiva() { return activa; }
    public void setActiva(boolean value) { this.activa = value; }
    public boolean isActiva() { return activa; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Instalacion x = new Instalacion();
        public Builder nombre(String value) { x.setNombre(value); return this; }
        public Builder tipo(String value) { x.setTipo(value); return this; }
        public Builder capacidad(int value) { x.setCapacidad(value); return this; }
        public Builder deporte(Deporte value) { x.setDeporte(value); return this; }
        public Builder techada(boolean value) { x.setTechada(value); return this; }
        public Builder activa(boolean value) { x.setActiva(value); return this; }
        public Instalacion build() { return x; }
    }
}
