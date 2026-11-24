package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class ReporteVentas implements Serializable {
    private static final long serialVersionUID = 1L;
    private String periodo;
    private double total;
    private String rubro;
    private Date desde;
    private Date hasta;

    public ReporteVentas() {}

    public ReporteVentas(String periodo, double total, String rubro, Date desde, Date hasta) {
        this.periodo = periodo;
        this.total = total;
        this.rubro = rubro;
        this.desde = desde;
        this.hasta = hasta;
    }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String value) { this.periodo = value; }

    public double getTotal() { return total; }
    public void setTotal(double value) { this.total = value; }

    public String getRubro() { return rubro; }
    public void setRubro(String value) { this.rubro = value; }

    public Date getDesde() { return desde; }
    public void setDesde(Date value) { this.desde = value; }

    public Date getHasta() { return hasta; }
    public void setHasta(Date value) { this.hasta = value; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final ReporteVentas x = new ReporteVentas();
        public Builder periodo(String value) { x.setPeriodo(value); return this; }
        public Builder total(double value) { x.setTotal(value); return this; }
        public Builder rubro(String value) { x.setRubro(value); return this; }
        public Builder desde(Date value) { x.setDesde(value); return this; }
        public Builder hasta(Date value) { x.setHasta(value); return this; }
        public ReporteVentas build() { return x; }
    }
}
