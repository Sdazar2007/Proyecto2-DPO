package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private double precio;
    private boolean activo;

    public Producto() {}

    public Producto(String nombre, double precio, boolean activo) {
        this.nombre = nombre;
        this.precio = precio;
        this.activo = activo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }

    public double getPrecio() { return precio; }
    public void setPrecio(double value) { this.precio = value; }

    public boolean getActivo() { return activo; }
    public void setActivo(boolean value) { this.activo = value; }
    public boolean isActivo() { return activo; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Producto x = new Producto();
        public Builder nombre(String value) { x.setNombre(value); return this; }
        public Builder precio(double value) { x.setPrecio(value); return this; }
        public Builder activo(boolean value) { x.setActivo(value); return this; }
        public Producto build() { return x; }
    }
}
