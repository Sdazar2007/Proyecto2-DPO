package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Reabastecimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date fecha;
    private int cantidad;
    private ArticuloTienda articulo;
    private String ubicacion;

    public Reabastecimiento() {}

    public Reabastecimiento(Date fecha, int cantidad, ArticuloTienda articulo, String ubicacion) {
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.articulo = articulo;
        this.ubicacion = ubicacion;
    }

    public Date getFecha() { return fecha; }
    public void setFecha(Date value) { this.fecha = value; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int value) { this.cantidad = value; }

    public ArticuloTienda getArticulo() { return articulo; }
    public void setArticulo(ArticuloTienda value) { this.articulo = value; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String value) { this.ubicacion = value; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Reabastecimiento x = new Reabastecimiento();
        public Builder fecha(Date value) { x.setFecha(value); return this; }
        public Builder cantidad(int value) { x.setCantidad(value); return this; }
        public Builder articulo(ArticuloTienda value) { x.setArticulo(value); return this; }
        public Builder ubicacion(String value) { x.setUbicacion(value); return this; }
        public Reabastecimiento build() { return x; }
    }
}
