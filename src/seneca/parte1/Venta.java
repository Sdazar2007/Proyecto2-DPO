package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Venta implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date fecha;
    private double subtotal;
    private double descuento;
    private double impuesto;
    private double total;
    private double propina;
    private String rubro;
    private Usuario comprador;
    private List<ItemVenta> items = new ArrayList<>();
    private int puntosRedimidos;

    public Venta() {}

    public Venta(Date fecha, double subtotal, double descuento, double impuesto, double total, double propina, String rubro, Usuario comprador, List<ItemVenta> items, int puntosRedimidos) {
        this.fecha = fecha;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.impuesto = impuesto;
        this.total = total;
        this.propina = propina;
        this.rubro = rubro;
        this.comprador = comprador;
        this.items = items;
        this.puntosRedimidos = puntosRedimidos;
    }

    public Date getFecha() { return fecha; }
    public void setFecha(Date value) { this.fecha = value; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double value) { this.subtotal = value; }

    public double getDescuento() { return descuento; }
    public void setDescuento(double value) { this.descuento = value; }

    public double getImpuesto() { return impuesto; }
    public void setImpuesto(double value) { this.impuesto = value; }

    public double getTotal() { return total; }
    public void setTotal(double value) { this.total = value; }

    public double getPropina() { return propina; }
    public void setPropina(double value) { this.propina = value; }

    public String getRubro() { return rubro; }
    public void setRubro(String value) { this.rubro = value; }

    public Usuario getComprador() { return comprador; }
    public void setComprador(Usuario value) { this.comprador = value; }

    public List<ItemVenta> getItems() { return items; }
    public void setItems(List<ItemVenta> value) { this.items = value; }

    public int getPuntosRedimidos() { return puntosRedimidos; }
    public void setPuntosRedimidos(int value) { this.puntosRedimidos = value; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Venta x = new Venta();
        public Builder fecha(Date value) { x.setFecha(value); return this; }
        public Builder subtotal(double value) { x.setSubtotal(value); return this; }
        public Builder descuento(double value) { x.setDescuento(value); return this; }
        public Builder impuesto(double value) { x.setImpuesto(value); return this; }
        public Builder total(double value) { x.setTotal(value); return this; }
        public Builder propina(double value) { x.setPropina(value); return this; }
        public Builder rubro(String value) { x.setRubro(value); return this; }
        public Builder comprador(Usuario value) { x.setComprador(value); return this; }
        public Builder items(List<ItemVenta> value) { x.setItems(value); return this; }
        public Builder puntosRedimidos(int value) { x.setPuntosRedimidos(value); return this; }
        public Venta build() { return x; }
    }
}
