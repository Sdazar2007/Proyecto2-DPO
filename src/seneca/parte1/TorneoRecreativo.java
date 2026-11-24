package seneca.parte1;

import java.time.LocalDate;
import java.util.*;
public final class TorneoRecreativo extends Torneo {
    private static final long serialVersionUID=1L;
    private final double porcentajeDescuento;
    private final List<BonoDescuento> bonos=new ArrayList<>();
    public TorneoRecreativo(String id,String nombre,LocalDate fecha,int cupo,Deporte deporte,Categoria categoria,ModalidadTorneo modalidad,double porcentaje){super(id,nombre,fecha,cupo,deporte,categoria,modalidad);exigir(Double.isFinite(porcentaje)&&porcentaje>0&&porcentaje<=1,"Descuento invalido");porcentajeDescuento=porcentaje;}
    public double getPorcentajeDescuento(){return porcentajeDescuento;}
    double tarifa(ParticipanteTorneo p){return 0;}
    void otorgar(ParticipanteTorneo ganador){for(Usuario u:ganador.getUsuarios())bonos.add(new BonoDescuento(u,porcentajeDescuento));}
    public List<BonoDescuento> getBonos(){return List.copyOf(bonos);}
}
