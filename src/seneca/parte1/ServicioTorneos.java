package seneca.parte1;

import java.io.Serializable;
import java.time.*;
import java.util.*;
public final class ServicioTorneos implements Serializable {
    private static final long serialVersionUID=1L;
    private final List<Torneo> torneos=new ArrayList<>();
    void crearTorneo(Torneo t){Torneo.exigir(t!=null&&torneos.stream().noneMatch(x->x.getId().equals(t.getId())),"Torneo nulo o id repetido");torneos.add(t);}
    public Torneo buscarTorneo(String id){return torneos.stream().filter(t->t.getId().equals(id)).findFirst().orElseThrow(()->new IllegalArgumentException("Torneo inexistente"));}
    public List<Torneo> consultarTorneos(){return List.copyOf(torneos);}
    public Torneo consultarHistorial(String id){return buscarTorneo(id);}
    public List<BonoDescuento> consultarBonos(Usuario s){return torneos.stream().filter(t->t instanceof TorneoRecreativo).map(t->(TorneoRecreativo)t).flatMap(t->t.getBonos().stream()).filter(b->b.getPropietario()==s).toList();}
    BonoDescuento buscarBono(Usuario s,String codigo){return consultarBonos(s).stream().filter(b->b.getCodigo().equals(codigo)&&b.estaDisponible()).findFirst().orElseThrow(()->new IllegalArgumentException("Bono inexistente, ajeno o utilizado"));}
}
