package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class EstadisticaTenisPadel implements Serializable {
    private static final long serialVersionUID = 1L;
    private int setsGanados;
    private int gamesGanados;
    private int aces;
    private int erroresNoForzados;
    private Socio socio;
    private PartidoPractica partido;

    public EstadisticaTenisPadel() {}

    public EstadisticaTenisPadel(int setsGanados, int gamesGanados, int aces, int erroresNoForzados, Socio socio, PartidoPractica partido) {
        this.setsGanados = setsGanados;
        this.gamesGanados = gamesGanados;
        this.aces = aces;
        this.erroresNoForzados = erroresNoForzados;
        this.socio = socio;
        this.partido = partido;
    }

    public int getSetsGanados() { return setsGanados; }
    public void setSetsGanados(int value) { this.setsGanados = value; }

    public int getGamesGanados() { return gamesGanados; }
    public void setGamesGanados(int value) { this.gamesGanados = value; }

    public int getAces() { return aces; }
    public void setAces(int value) { this.aces = value; }

    public int getErroresNoForzados() { return erroresNoForzados; }
    public void setErroresNoForzados(int value) { this.erroresNoForzados = value; }

    public Socio getSocio() { return socio; }
    public void setSocio(Socio value) { this.socio = value; }

    public PartidoPractica getPartido() { return partido; }
    public void setPartido(PartidoPractica value) { this.partido = value; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final EstadisticaTenisPadel x = new EstadisticaTenisPadel();
        public Builder setsGanados(int value) { x.setSetsGanados(value); return this; }
        public Builder gamesGanados(int value) { x.setGamesGanados(value); return this; }
        public Builder aces(int value) { x.setAces(value); return this; }
        public Builder erroresNoForzados(int value) { x.setErroresNoForzados(value); return this; }
        public Builder socio(Socio value) { x.setSocio(value); return this; }
        public Builder partido(PartidoPractica value) { x.setPartido(value); return this; }
        public EstadisticaTenisPadel build() { return x; }
    }
}
