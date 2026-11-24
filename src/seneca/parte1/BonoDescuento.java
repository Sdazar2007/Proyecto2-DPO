package seneca.parte1;

import java.io.Serializable;
import java.util.UUID;
public final class BonoDescuento implements Serializable {
    private static final long serialVersionUID=1L;
    private final String codigo=UUID.randomUUID().toString();
    private final Usuario propietario;
    private final double porcentaje;
    private boolean utilizado;
    BonoDescuento(Usuario propietario,double porcentaje){this.propietario=propietario;this.porcentaje=porcentaje;}
    public String getCodigo(){return codigo;}
    public Usuario getPropietario(){return propietario;}
    public double getPorcentaje(){return porcentaje;}
    public boolean estaDisponible(){return !utilizado;}
    void redimir(){if(utilizado)throw new IllegalArgumentException("Bono utilizado");utilizado=true;}
}
