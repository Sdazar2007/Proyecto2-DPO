package seneca.consola;

import seneca.parte1.*;
import java.util.*;
public final class ConsolaSocio extends ConsolaBase {
    public static void main(String[] args){new ConsolaSocio().ejecutar();}
    protected boolean admite(Usuario u){return u instanceof Socio||u instanceof Estudiante||u instanceof Externo;}
    public void mostrarMenu(){while(true){System.out.println("0 Salir | 1 Torneos | 2 Inscribirse torneo | 3 Retirarse torneo | 4 Bonos | 5 Compra | 6 Equipo | 7 Academia | 8 Reserva | 9 Cancelar reserva | 10 Ranking | 11 Mensualidad | 12 Asistencia | 13 Retirarse equipo | 14 Consultar partidos");int op=entero("Opcion: ",0,14);if(op==0)return;seguro(()->{switch(op){case 1->consultarTorneos();case 2->inscribirTorneo();case 3->desinscribirTorneo();case 4->consultarBonos();case 5->comprar();default->accionesSocio(op);}});}}
    private void accionesSocio(int op){if(!(usuarioActual instanceof Socio s))throw new IllegalArgumentException("Funcion reservada a socios");switch(op){
        case 6->club.inscribirEquipo(s,equipo(),texto("Posicion: "),entero("Camiseta: ",1,999));
        case 7->club.inscribirAcademia(s,deporte(),texto("Nivel: "));
        case 8->{Instalacion i=instalacion();String bloque=texto("Fecha y hora ISO: ");String modalidad=entero("1 Sencillos, 2 Dobles: ",1,2)==1?"sencillos":"dobles";List<Socio> socios=new ArrayList<>();socios.add(s);int cantidad=modalidad.equals("sencillos")?2:4;while(socios.size()<cantidad)socios.add(socio());club.reservar(s,i,bloque,modalidad,socios);}
        case 9->{Reserva r=elegir(club.getAcademias().getReservas(),x->x.getBloqueHorario());club.getAcademias().cancelarReserva(r,s);}
        case 10->{Deporte d=deporte();System.out.println(club.getAcademias().ranking(s,d));System.out.println(club.getAcademias().evolucionRanking(s,d));}
        case 11->System.out.println(club.pagarMensualidad(s,decimal("Valor: ")).getTotal());
        case 12->club.getAcademias().asistencia(s).forEach(x->System.out.println(x.getAsistencia()+" "+x.getNotaDesempeno()));
        case 13->club.getDeportivo().retirar(s,equipo());
        case 14->{club.getDeportivo().getPartidos().forEach(x->System.out.println(x.getFecha()+" "+x.getMarcador()));club.getAcademias().getPartidos().forEach(x->System.out.println(x.getFecha()+" finalizado="+x.isFinalizado()));}
    }}
}
