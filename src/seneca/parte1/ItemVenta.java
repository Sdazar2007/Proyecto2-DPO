package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class ItemVenta implements Serializable {
    private static final long serialVersionUID = 1L;
    private int cantidad;
    private Producto producto;
    private double precioUnitario;

    public ItemVenta() {}

    public ItemVenta(int cantidad, Producto producto, double precioUnitario) {
        this.cantidad = cantidad;
        this.producto = producto;
        this.precioUnitario = precioUnitario;
    }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int value) { this.cantidad = value; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto value) { this.producto = value; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double value) { this.precioUnitario = value; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final ItemVenta x = new ItemVenta();
        public Builder cantidad(int value) { x.setCantidad(value); return this; }
        public Builder producto(Producto value) { x.setProducto(value); return this; }
        public Builder precioUnitario(double value) { x.setPrecioUnitario(value); return this; }
        public ItemVenta build() { return x; }
    }
}
