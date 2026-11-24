package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class Equipo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private boolean activo = true;
    private int cupoMaximo;
    private Deporte deporte;
    private Categoria categoria;
    private List<InscripcionEquipo> inscripciones = new ArrayList<>();

    public Equipo() {}

    public Equipo(String nombre, int cupoMaximo, Deporte deporte, Categoria categoria, List<InscripcionEquipo> inscripciones) {
        this.nombre = nombre;
        this.cupoMaximo = cupoMaximo;
        this.deporte = deporte;
        this.categoria = categoria;
        this.inscripciones = inscripciones;
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo=activo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = cupoMaximo; }

    public Deporte getDeporte() { return deporte; }
    public void setDeporte(Deporte deporte) { this.deporte = deporte; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public List<InscripcionEquipo> getInscripciones() { return inscripciones; }
    public void setInscripciones(List<InscripcionEquipo> inscripciones) { this.inscripciones = inscripciones; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Equipo instance = new Equipo();
        public Builder nombre(String value) { instance.setNombre(value); return this; }
        public Builder cupoMaximo(int value) { instance.setCupoMaximo(value); return this; }
        public Builder deporte(Deporte value) { instance.setDeporte(value); return this; }
        public Builder categoria(Categoria value) { instance.setCategoria(value); return this; }
        public Builder inscripciones(List<InscripcionEquipo> value) { instance.setInscripciones(value); return this; }
        public Equipo build() { return instance; }
    }
}
