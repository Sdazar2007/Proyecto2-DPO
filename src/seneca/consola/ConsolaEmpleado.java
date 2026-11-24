package seneca.consola;

import seneca.parte1.*;
import java.util.*;
public final class ConsolaEmpleado extends ConsolaBase {
    public static void main(String[] args){new ConsolaEmpleado().ejecutar();}
    protected boolean admite(Usuario u){return u instanceof Empleado;}
    public void mostrarMenu(){while(true){System.out.println("0 Salir | 1 Torneos | 2 Inscribirse | 3 Retirarse | 4 Turnos | 5 Sugerencia | 6 Compra | 7 Entrenamiento | 8 Evaluar | 9 Bonos");int op=entero("Opcion: ",0,9);if(op==0)return;seguro(()->{switch(op){case 1->consultarTorneos();case 2->inscribirTorneo();case 3->desinscribirTorneo();case 4->club.getAdministrativo().getTurnos().stream().filter(t->t.getEmpleado()==usuarioActual&&t.isActivo()).forEach(t->System.out.println(t.getDia()+" "+t.getHoraInicio()+"-"+t.getHoraFin()));case 5->club.sugerir((Empleado)usuarioActual,texto("Nombre: "),texto("Tipo: "),texto("Motivo: "));case 6->comprar();case 7->entrenamiento();case 8->evaluar();case 9->consultarBonos();}});}}
    private Entrenador entrenador(){if(!(usuarioActual instanceof Entrenador e))throw new IllegalArgumentException("Solo entrenadores");return e;}
    private void entrenamiento(){Entrenador e=entrenador();Instalacion i=instalacion();String bloque=texto("Fecha y hora ISO: ");int minutos=entero("Duracion minutos: ",1,1440);List<Socio> convocados=new ArrayList<>();int n=entero("Convocados: ",1,100);for(int j=0;j<n;j++)convocados.add(socio());if(si("Entrenamiento de equipo"))club.programarEntrenamientoEquipo(e,equipo(),i,bloque,minutos,convocados);else club.getAcademias().programarSesion(e,i,bloque,minutos,convocados);}
    private void evaluar(){Entrenador e=entrenador();SesionEntrenamiento s=elegir(club.getAcademias().getSesiones().stream().filter(x->x.getEntrenador()==e).toList(),x->x.getFecha()+" "+x.getHora());club.getAcademias().evaluar(s,socio(),texto("Asistencia: "),decimal("Nota: "),texto("Comentario: "));}
}
