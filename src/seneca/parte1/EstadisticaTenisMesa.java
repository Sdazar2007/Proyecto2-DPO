package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class EstadisticaTenisMesa implements Serializable {
    private static final long serialVersionUID = 1L;
    private int setsGanados;
    private int puntosTotales;
    private Socio socio;
    private PartidoPractica partido;

    public EstadisticaTenisMesa() {}

    public EstadisticaTenisMesa(int setsGanados, int puntosTotales, Socio socio, PartidoPractica partido) {
        this.setsGanados = setsGanados;
        this.puntosTotales = puntosTotales;
        this.socio = socio;
        this.partido = partido;
    }

    public int getSetsGanados() { return setsGanados; }
    public void setSetsGanados(int value) { this.setsGanados = value; }

    public int getPuntosTotales() { return puntosTotales; }
    public void setPuntosTotales(int value) { this.puntosTotales = value; }

    public Socio getSocio() { return socio; }
    public void setSocio(Socio value) { this.socio = value; }

    public PartidoPractica getPartido() { return partido; }
    public void setPartido(PartidoPractica value) { this.partido = value; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final EstadisticaTenisMesa x = new EstadisticaTenisMesa();
        public Builder setsGanados(int value) { x.setSetsGanados(value); return this; }
        public Builder puntosTotales(int value) { x.setPuntosTotales(value); return this; }
        public Builder socio(Socio value) { x.setSocio(value); return this; }
        public Builder partido(PartidoPractica value) { x.setPartido(value); return this; }
        public EstadisticaTenisMesa build() { return x; }
    }
}
