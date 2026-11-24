package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class ArticuloTienda extends Producto {
    private static final long serialVersionUID = 1L;
    private String categoria;
    private int cantidad;

    public ArticuloTienda() {}

    public ArticuloTienda(String categoria, int cantidad) {
        this.categoria = categoria;
        this.cantidad = cantidad;
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String value) { this.categoria = value; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int value) { this.cantidad = value; }

    public static Builder nuevoBuilder() { return new Builder(); }
    public static class Builder {
        private final ArticuloTienda x = new ArticuloTienda();
        public Builder categoria(String value) { x.setCategoria(value); return this; }
        public Builder cantidad(int value) { x.setCantidad(value); return this; }
        public ArticuloTienda build() { return x; }
    }
}
