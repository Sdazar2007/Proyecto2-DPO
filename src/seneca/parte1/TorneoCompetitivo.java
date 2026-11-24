package seneca.parte1;

import java.time.LocalDate;
import java.math.*;
import java.util.*;
public final class TorneoCompetitivo extends Torneo {
    private static final long serialVersionUID=1L;
    private final double tarifaInscripcion;
    private final List<PremioEfectivo> premios=new ArrayList<>();
    public TorneoCompetitivo(String id,String nombre,LocalDate fecha,int cupo,Deporte deporte,Categoria categoria,ModalidadTorneo modalidad,double tarifa){super(id,nombre,fecha,cupo,deporte,categoria,modalidad);exigir(Double.isFinite(tarifa)&&tarifa>0&&tarifa<=1e12,"Tarifa invalida");tarifaInscripcion=BigDecimal.valueOf(tarifa).setScale(2,RoundingMode.HALF_UP).doubleValue();exigir(tarifaInscripcion>0,"Tarifa invalida");}
    public double getTarifaInscripcion(){return tarifaInscripcion;}
    double tarifa(ParticipanteTorneo p){return p.tieneEmpleado()?0:tarifaInscripcion;}
    public double calcularRecaudacion(){return activas().stream().map(i->BigDecimal.valueOf(i.getValorInscripcion())).reduce(BigDecimal.ZERO,BigDecimal::add).doubleValue();}
    public double calcularPremio(){return calcularRecaudacion();}
    void otorgar(ParticipanteTorneo ganador){if(!ganador.tieneEmpleado()&&calcularPremio()>0)premios.add(new PremioEfectivo(ganador,calcularPremio()));}
    public List<PremioEfectivo> getPremios(){return List.copyOf(premios);}
}
