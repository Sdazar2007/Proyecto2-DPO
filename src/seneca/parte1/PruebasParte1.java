package seneca.parte1;

import java.nio.file.*;
import java.util.*;
public class PruebasParte1 {
    private static int pruebas=0;
    private static void ok(boolean condicion,String mensaje){pruebas++;if(!condicion)throw new AssertionError(mensaje);}
    private static void falla(Runnable r,String mensaje){pruebas++;try{r.run();throw new AssertionError("No rechazo: "+mensaje);}catch(IllegalArgumentException esperado){}}
    private static Socio socio(int edad,String login){Socio s=new Socio();s.setEdad(edad);s.setLogin(login);return s;}
    private static Equipo equipo(String nombre,Deporte d,Categoria c,int cupo){return new Equipo(nombre,cupo,d,c,new ArrayList<>());}
    public static void main(String[] args)throws Exception{
        ServicioDeportivo sistema=new ServicioDeportivo();
        Deporte futbol=new Deporte();futbol.setNombre("Futbol");
        Deporte basket=new Deporte();basket.setNombre("Baloncesto");
        Categoria mayores=new Categoria();mayores.setNombre("Mayores");
        Equipo a=equipo("A",futbol,mayores,8),b=equipo("B",futbol,mayores,8),c=equipo("C",basket,mayores,5);
        sistema.registrarEquipo(a);sistema.registrarEquipo(b);sistema.registrarEquipo(c);
        List<Socio> as=new ArrayList<>(),bs=new ArrayList<>();
        for(int i=0;i<7;i++){
            Socio sa=socio(20,"a"+i),sb=socio(20,"b"+i);as.add(sa);bs.add(sb);
            sistema.inscribir(sa,a,"Jugador",i+1);sistema.inscribir(sb,b,"Jugador",i+1);
        }
        ok(a.getInscripciones().size()==7,"Inscripciones");
        falla(()->sistema.inscribir(as.get(0),b,"Jugador",8),"Mismo deporte");
        falla(()->sistema.inscribir(socio(20,"extra"),a,"Jugador",1),"Camiseta duplicada");
        falla(()->sistema.inscribir(socio(12,"nino"),a,"Jugador",8),"Edad");
        sistema.inscribir(socio(20,"extra"),a,"Jugador",8);
        falla(()->sistema.inscribir(socio(20,"full"),a,"Jugador",9),"Cupo");
        Escenario esc=new Escenario();esc.setNombre("Cancha 1");
        PartidoInterno pi=sistema.programarInterno(a,b,new Date(),esc);
        sistema.registrarResultado(pi,2,1);ok("2-1".equals(pi.getMarcador()),"Marcador");
        falla(()->sistema.registrarResultado(pi,3,1),"Resultado duplicado");
        EstadisticaFutbol f=new EstadisticaFutbol();f.setSocio(as.get(0));f.setPartido(pi);f.setGoles(2);f.setAmarillas(2);
        sistema.registrarEstadistica(f);ok(sistema.goles(as.get(0))==2,"Goles");
        ok(!sistema.habilitado(as.get(0)),"Suspension");
        falla(()->sistema.registrarEstadistica(f),"Estadistica duplicada");
        Competencia comp=new Competencia();comp.setNombre("Cerros");comp.setFormato("Liga");
        sistema.configurarCompetencia(comp,futbol,mayores);
        falla(()->sistema.inscribirCompetencia(b,comp,bs.subList(0,5)),"Nomina insuficiente");
        sistema.inscribirCompetencia(b,comp,bs);
        InstitucionRival rival=new InstitucionRival();rival.setNombre("Universidad X");
        PartidoCompetencia pc=sistema.programarCompetencia(comp,b,rival,new Date(),esc,bs);
        sistema.registrarResultado(pc,3,0);ok(sistema.puntos(comp,b)==3,"Puntos liga futbol");
        List<Socio> ajenos=new ArrayList<>(bs);ajenos.set(0,as.get(0));
        falla(()->sistema.programarCompetencia(comp,b,rival,new Date(),esc,ajenos),"Nomina ajena");
        Path ruta=Files.createTempFile("seneca-parte1-",".dat");
        sistema.guardar(ruta);ServicioDeportivo recuperado=ServicioDeportivo.cargar(ruta);
        ok(recuperado.getEquipos().size()==3&&recuperado.getPartidos().size()==2,"Persistencia");
        Files.deleteIfExists(ruta);
        System.out.println("OK: "+pruebas+" verificaciones superadas.");
    }
}
