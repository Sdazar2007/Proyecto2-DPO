package seneca.parte1;

import java.nio.file.*;
import java.util.*;

/** Pruebas de contrato, errores y regresiones para Entrega 2 / Parte 1. */
public final class PruebasAuditoriaIntegral {
    private static int n = 0;
    private static void check(boolean expected, String detail) {
        n++;
        if (!expected) throw new AssertionError("FALLO: " + detail);
    }
    private static void reject(Runnable action, String detail) {
        n++;
        try { action.run(); }
        catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("ACEPTO CASO INVALIDO: " + detail);
    }
    private static Socio socio(int edad) { Socio s=new Socio(); s.setEdad(edad); return s; }
    private static Equipo equipo(String n, Deporte d, Categoria c, int cupo) { return new Equipo(n,cupo,d,c,new ArrayList<>()); }
    private static List<Socio> poblar(ServicioDeportivo ss, Equipo e, int cuantos, int edad) {
        List<Socio> l=new ArrayList<>();
        for(int i=0;i<cuantos;i++){Socio s=socio(edad);ss.inscribir(s,e,"titular",i+1);l.add(s);}
        return l;
    }
    private static EstadisticaFutbol sf(Socio s,Partido p,int goles,int amarillas,int rojas) {
        EstadisticaFutbol f=new EstadisticaFutbol();f.setSocio(s);f.setPartido(p);f.setGoles(goles);f.setAmarillas(amarillas);f.setRojas(rojas);return f;
    }
    private static EstadisticaBaloncesto sb(Socio s,Partido p,int puntos) {
        EstadisticaBaloncesto f=new EstadisticaBaloncesto();f.setSocio(s);f.setPartido(p);f.setPuntos(puntos);return f;
    }
    private static EstadisticaVoleibol sv(Socio s,Partido p,int aces) {
        EstadisticaVoleibol f=new EstadisticaVoleibol();f.setSocio(s);f.setPartido(p);f.setAces(aces);return f;
    }
    private static Escenario esc() {return new Escenario("Cancha prueba");}
    private static Date fecha(){return new Date(1800000000000L);}
    private static InstitucionRival rival(){return new InstitucionRival("Universidad Rival");}
    private static void probarInscripciones() {
        ServicioDeportivo sys=new ServicioDeportivo();
        Deporte fut=new Deporte("FUTBOL"),bal=new Deporte("Baloncesto");
        Categoria may=new Categoria("Mayores"),s13=new Categoria("Sub-13"),s15=new Categoria("Sub-15"),s17=new Categoria("Sub-17");
        Equipo a=equipo("A",fut,may,7), b=equipo("B",new Deporte("futbol"),may,8);
        Equipo basket=equipo("Basket",bal,may,7);
        Equipo u13=equipo("U13",fut,s13,8),u15=equipo("U15",fut,s15,8),u17=equipo("U17",fut,s17,8);
        for(Equipo e:List.of(a,b,basket,u13,u15,u17))sys.registrarEquipo(e);
        reject(()->sys.registrarEquipo(a),"duplicar la misma referencia de equipo");
        reject(()->sys.registrarEquipo(equipo("a",new Deporte("FuTbOl"),new Categoria("mayores"),7)),"duplicar nombre+deporte+categoria");
        reject(()->sys.registrarEquipo(new Equipo()),"equipo nulo de campos");
        reject(()->sys.registrarEquipo(equipo("x",fut,may,0)),"cupo 0");
        reject(()->sys.inscribir(socio(20),a,"",1),"posicion vacia");
        reject(()->sys.inscribir(socio(20),a,"titular",0),"camiseta 0");
        reject(()->sys.inscribir(socio(16),a,"titular",1),"edad <17 en Mayores");
        reject(()->sys.inscribir(socio(13),u13,"titular",1),"13 fuera de sub13");
        check(sys.inscribir(socio(12),u13,"titular",1)!=null,"12 pertenece a sub13");
        reject(()->sys.inscribir(socio(12),u15,"titular",1),"12 fuera de sub15");
        check(sys.inscribir(socio(13),u15,"titular",1)!=null,"13 pertenece sub15");
        check(sys.inscribir(socio(14),u15,"titular",2)!=null,"14 pertenece sub15");
        reject(()->sys.inscribir(socio(15),u15,"titular",3),"15 fuera sub15");
        check(sys.inscribir(socio(15),u17,"titular",1)!=null,"15 pertenece sub17");
        check(sys.inscribir(socio(16),u17,"titular",2)!=null,"16 pertenece sub17");
        reject(()->sys.inscribir(socio(17),u17,"titular",3),"17 fuera sub17");
        Socio jugador=socio(22);
        check(sys.inscribir(jugador,a,"defensa",3)!=null,"Mayores 22");
        reject(()->sys.inscribir(jugador,a,"defensa",4),"mismo jugador mismo equipo");
        reject(()->sys.inscribir(jugador,b,"defensa",5),"mismo jugador otro equipo mismo deporte por nombre");
        reject(()->sys.inscribir(socio(22),a,"defensa",3),"numero camiseta duplicado");
        check(sys.inscribir(jugador,basket,"pivot",3)!=null,"mismo jugador dos deportes permitido");
        check(jugador.getInscripciones().size()==2,"inversas dos equipos");
        check(a.getInscripciones().size()==1,"equipo contiene jugador");
        sys.retirar(jugador,a);
        check(a.getInscripciones().isEmpty()&&jugador.getInscripciones().size()==1,"retiro bidireccional");
        reject(()->sys.retirar(jugador,a),"doble retiro");
        check(sys.inscribir(jugador,b,"defensa",1)!=null,"cambio de equipo mismo deporte despues de retiro");
        poblar(sys,a,7,19);
        reject(()->sys.inscribir(socio(19),a,"x",8),"cupo lleno");
        check(sys.jugadoresHabilitados(a).size()==7,"habilitados 7");
    }
    private static void probarPartidos() {
        ServicioDeportivo ss=new ServicioDeportivo();
        Deporte fut=new Deporte("Futbol"),bal=new Deporte("Baloncesto"),vol=new Deporte("Voleibol");
        Categoria may=new Categoria("Mayores"),sub=new Categoria("Sub-17");
        Equipo a=equipo("A",fut,may,12),b=equipo("B",fut,may,12),c=equipo("C",bal,may,9),d=equipo("D",vol,may,9),j=equipo("J",fut,sub,10);
        for(Equipo e:List.of(a,b,c,d,j))ss.registrarEquipo(e);
        List<Socio> aa=poblar(ss,a,8,22),bb=poblar(ss,b,8,22),cc=poblar(ss,c,5,22),dd=poblar(ss,d,6,22);
        reject(()->ss.programarInterno(a,c,fecha(),esc()),"deportes distintos");
        reject(()->ss.programarInterno(a,j,fecha(),esc()),"categorias distintas");
        reject(()->ss.programarInterno(a,a,fecha(),esc()),"mismo equipo");
        reject(()->ss.programarInterno(a,b,null,esc()),"sin fecha");
        reject(()->ss.programarInterno(a,b,fecha(),null),"sin escenario");
        reject(()->ss.programarInterno(a,b,fecha(),esc(),aa.subList(0,6),bb),"7 futbol minimo");
        List<Socio> repetidos=new ArrayList<>(aa);repetidos.set(7,aa.get(0));
        reject(()->ss.programarInterno(a,b,fecha(),esc(),repetidos,bb),"alineacion repetida");
        List<Socio> ajenos=new ArrayList<>(aa);ajenos.set(7,cc.get(0));
        reject(()->ss.programarInterno(a,b,fecha(),esc(),ajenos,bb),"alineacion ajena");
        PartidoInterno p=ss.programarInterno(a,b,fecha(),esc(),aa.subList(0,7),bb.subList(0,7));
        reject(()->ss.registrarResultado(p,-1,0),"marcador negativo");
        ss.registrarResultado(p,1,1);
        check("1-1".equals(p.getMarcador()),"futbol empate");
        reject(()->ss.registrarResultado(p,0,0),"resultado duplicado");
        check(ss.getPartidos().size()==1,"un solo partido admitido");
        reject(()->ss.registrarEstadistica(sf(aa.get(7),p,1,0,0)),"suplente no alineado");
        reject(()->ss.registrarEstadistica(sf(cc.get(0),p,1,0,0)),"ajeno al partido");
        reject(()->ss.registrarEstadistica(sb(aa.get(0),p,5)),"estadistica deporte incorrecta");
        reject(()->ss.registrarEstadistica(sf(aa.get(0),p,-1,0,0)),"goles negativos");
        reject(()->ss.registrarEstadistica(sf(aa.get(0),p,1,-1,0)),"amarillas negativas");
        ss.registrarEstadistica(sf(aa.get(0),p,2,1,0));
        check(ss.goles(aa.get(0))==2,"goles acumulados");
        reject(()->ss.registrarEstadistica(sf(aa.get(0),p,3,0,0)),"estadistica duplicada");
        // Estadisticas baloncesto y voleibol se aceptan solo con sus respectivos partidos.
        Equipo c2=equipo("C2",bal,may,9),d2=equipo("D2",vol,may,9);ss.registrarEquipo(c2);ss.registrarEquipo(d2);
        poblar(ss,c2,5,22);poblar(ss,d2,6,22);
        PartidoInterno pb=ss.programarInterno(c,c2,fecha(),esc());
        reject(()->ss.registrarResultado(pb,100,100),"empate baloncesto");
        ss.registrarResultado(pb,75,72);
        ss.registrarEstadistica(sb(cc.get(0),pb,25));
        check(ss.estadisticaAcumulada(cc.get(0),null,"puntos")==25,"puntos basket");
        PartidoInterno pv=ss.programarInterno(d,d2,fecha(),esc());
        reject(()->ss.registrarResultado(pv,2,1),"sets incompletos");
        reject(()->ss.registrarResultado(pv,3,3),"empate volley");
        reject(()->ss.registrarResultado(pv,4,0),"sets cuatro volley");
        ss.registrarResultado(pv,3,2);ss.registrarEstadistica(sv(dd.get(0),pv,4));
        check(ss.estadisticaAcumulada(dd.get(0),null,"aces")==4,"aces voleibol");
        // Sanciones: debe quedar excluido del siguiente encuentro.
        ss.registrarEstadistica(sf(aa.get(1),p,0,2,0));
        check(!ss.habilitado(aa.get(1)),"suspendido por dos amarillas");
        reject(()->ss.programarInterno(a,b,fecha(),esc(),aa.subList(0,7),bb.subList(0,7)),"alinear suspendido");
        PartidoInterno p2=ss.programarInterno(a,b,fecha(),esc());
        ss.registrarResultado(p2,3,1);
        check(ss.habilitado(aa.get(1)),"cumple suspension en partido siguiente");
        check(ss.getPartidos().size()==4,"cantidad partidos");
    }
    private static void probarCompetencia() {
        ServicioDeportivo ss=new ServicioDeportivo();
        Deporte fut=new Deporte("Futbol"),bal=new Deporte("Baloncesto");
        Categoria may=new Categoria("Mayores"),sub=new Categoria("Sub-15");
        Equipo a=equipo("A",fut,may,12),b=equipo("B",fut,may,12),bas=equipo("Bas",bal,may,8),j=equipo("J",fut,sub,12);
        for(Equipo e:List.of(a,b,bas,j))ss.registrarEquipo(e);
        List<Socio> aa=poblar(ss,a,9,21),bb=poblar(ss,b,9,20),basplayers=poblar(ss,bas,5,22);
        Competencia torneo=new Competencia("ASCUN","ASCUN",2026,"GRUPOS+ELIMINACION");
        ss.configurarCompetencia(torneo,fut,may);
        reject(()->ss.configurarCompetencia(torneo,bal,may),"reconfiguracion incompatible de deporte");
        reject(()->ss.configurarCompetencia(torneo,fut,sub),"reconfiguracion incompatible de categoria");
        reject(()->ss.inscribirCompetencia(bas,torneo,basplayers),"deporte incompatible");
        reject(()->ss.inscribirCompetencia(j,torneo,List.of()),"categoria incompatible");
        reject(()->ss.inscribirCompetencia(a,torneo,aa.subList(0,6)),"nomina futbol <7");
        List<Socio> repetidos=new ArrayList<>(aa);repetidos.set(8,aa.get(0));
        reject(()->ss.inscribirCompetencia(a,torneo,repetidos),"nomina duplicados");
        List<Socio> fuera=new ArrayList<>(aa);fuera.set(8,bb.get(0));
        reject(()->ss.inscribirCompetencia(a,torneo,fuera),"nomina integrante equipo ajeno");
        ss.inscribirCompetencia(a,torneo,aa);ss.inscribirCompetencia(b,torneo,bb);
        reject(()->ss.inscribirCompetencia(a,torneo,aa),"doble inscripcion");
        reject(()->ss.programarCompetencia(torneo,a,rival(),fecha(),esc(),aa.subList(0,6)),"alineacion minima");
        reject(()->ss.programarCompetencia(torneo,a,rival(),fecha(),esc(),fuera),"alineacion fuera nomina");
        PartidoCompetencia p=ss.programarCompetencia(torneo,a,rival(),fecha(),esc(),aa);
        ss.configurarFase(p,"GRUPOS","A");
        ss.registrarResultado(p,2,0);
        check(ss.puntos(torneo,a)==3,"puntos por partido de grupo");
        check(ss.tablaCompleta(torneo,"GRUPOS","A").size()==2,"tabla grupo incluye rival");
        ss.registrarEstadistica(sf(aa.get(0),p,2,2,0));
        check(!ss.habilitadoEnCompetencia(aa.get(0),torneo),"suspension oficial amarillas");
        check(ss.partidosSuspendidoEnTorneo(aa.get(0),torneo)==1,"pendiente partido oficial");
        // El encuentro interno NO levanta la suspension oficial.
        PartidoInterno interno=ss.programarInterno(a,b,fecha(),esc());
        ss.registrarResultado(interno,1,0);
        check(ss.habilitado(aa.get(0)),"sancion general cumplida en interno");
        check(!ss.habilitadoEnCompetencia(aa.get(0),torneo),"no se lava sancion oficial en interno");
        reject(()->ss.programarCompetencia(torneo,a,rival(),fecha(),esc(),aa),"alinear sancionado en competencia");
        List<Socio> sinSancionado=new ArrayList<>(aa);sinSancionado.remove(0);
        PartidoCompetencia oficial=ss.programarCompetencia(torneo,a,rival(),fecha(),esc(),sinSancionado);
        ss.configurarFase(oficial,"GRUPOS","A");ss.registrarResultado(oficial,0,1);
        check(ss.habilitadoEnCompetencia(aa.get(0),torneo),"cumple sancion en partido oficial");
        check(ss.partidosSuspendidoEnTorneo(aa.get(0),torneo)==0,"se reinicia saldo sancion");
        PartidoCompetencia knockout=ss.programarCompetencia(torneo,a,rival(),fecha(),esc(),aa);
        ss.configurarFase(knockout,"ELIMINACION",null);ss.registrarResultado(knockout,3,0);
        check(ss.puntos(torneo,a)==3,"eliminatoria no suma puntos de grupo");
        check(ss.resumenEquipo(torneo,a)[0]==2,"eliminatoria no suma PJ de grupo");
        check(ss.tablaCompleta(torneo,"ELIMINACION",null).size()==3,"tabla eliminatoria informativa");
        check(ss.estadisticaAcumulada(aa.get(0),torneo,"goles")==2,"estadistica torneo");
        check(ss.amarillasEnTorneo(torneo,aa.get(0))==2,"amarillas de torneo");
        // Una roja tambien debe bloquear el siguiente oficial.
        ss.registrarEstadistica(sf(aa.get(1),knockout,0,0,1));
        check(ss.partidosSuspendidoEnTorneo(aa.get(1),torneo)==1,"roja sanciona partido oficial");
        Path tmp=null;
        try{
            tmp=Files.createTempFile("seneca-integral-",".dat");ss.guardar(tmp);
            ServicioDeportivo loaded=ServicioDeportivo.cargar(tmp);
            check(loaded.getEquipos().size()==4,"persistencia equipos");
            check(loaded.getPartidos().size()==4,"persistencia partidos");
            Equipo a2=loaded.getEquipos().stream().filter(e->e.getNombre().equals("A")).findFirst().orElseThrow();
            Competencia c2=((PartidoCompetencia)loaded.getPartidos().get(0)).getCompetencia();
            check(loaded.puntos(c2,a2)==3,"persistencia puntos");
            check(loaded.getEstadisticas().size()==2,"persistencia estadisticas");
            check(loaded.partidosSuspendidoEnTorneo(loaded.getEstadisticas().get(1).getSocio(),c2)==1,"persistencia sanciones oficiales");
            check(loaded.tablaCompleta(c2,"GRUPOS","A").size()==2,"persistencia fase y grupo");
        }catch(Exception ex){throw new AssertionError("Persistencia integral",ex);}
        finally{try{if(tmp!=null)Files.deleteIfExists(tmp);}catch(Exception ex){throw new AssertionError(ex);}}
    }
    private static void probarTablasYEliminatorias() {
        ServicioDeportivo ss=new ServicioDeportivo();
        Competencia c=new Competencia("Cerros","Cerros",2026,"Liga");
        ss.configurarCompetencia(c,new Deporte("Futbol"),new Categoria("Mayores"));
        reject(()->ss.registrarEncuentroExterno(c,"A","a",1,0,"LIGA",null),"mismo nombre sin case");
        reject(()->ss.registrarEncuentroExterno(c,"A","B",-1,0,"LIGA",null),"goles negativos");
        reject(()->ss.registrarEncuentroExterno(c,"A","B",1,0,"GRUPOS",null),"grupo requerido");
        ss.registrarEncuentroExterno(c,"A","B",3,0,"LIGA",null);
        ss.registrarEncuentroExterno(c,"B","C",2,1,"LIGA",null);
        ss.registrarEncuentroExterno(c,"C","A",0,0,"LIGA",null);
        List<ServicioDeportivo.FilaTabla> tabla=ss.tablaConDesempateDirecto(c,"LIGA",null);
        check(tabla.size()==3,"tres clubes de liga");
        check(tabla.get(0).equipo.equals("A")&&tabla.get(0).puntos==4,"A 4 puntos");
        check(tabla.get(1).equipo.equals("B")&&tabla.get(1).puntos==3,"B 3 puntos");
        check(tabla.get(2).equipo.equals("C")&&tabla.get(2).puntos==1,"C 1 punto");
        check(tabla.get(0).ganados==1&&tabla.get(0).empatados==1,"estadisticas liga completas");
        check(tabla.get(0).diferencia()==3,"diferencia goles correcta");
        Competencia asc=new Competencia("ASCUN","ASCUN",2026,"Grupos+Eliminacion");
        ss.configurarCompetencia(asc,new Deporte("Futbol"),new Categoria("Mayores"));
        String[][] jornadas={{"A","B","G1"},{"C","D","G1"},{"E","F","G2"},{"G","H","G2"}};
        for(String[] j:jornadas)ss.registrarEncuentroExterno(asc,j[0],j[1],2,0,"GRUPOS",j[2]);
        check(ss.tablaCompleta(asc,"GRUPOS","G1").size()==4,"grupo 1 separado");
        check(ss.tablaCompleta(asc,"GRUPOS","G2").size()==4,"grupo 2 separado");
        reject(()->ss.clasificarPorGrupos(asc,5),"clasificados > participantes");
        List<String> clas=ss.clasificarPorGrupos(asc,2);
        check(clas.size()==4,"cuatro clasificados");
        Map<String,String> cruces=ss.generarCruces(asc);
        check(cruces.size()==2,"semifinal creada");
        reject(()->ss.avanzarRonda(asc),"no se avanza sin marcadores");
        for(Map.Entry<String,String> m:cruces.entrySet())ss.registrarEncuentroExterno(asc,m.getKey(),m.getValue(),1,0,"ELIMINACION",null);
        List<String> ganadores=ss.avanzarRonda(asc);
        check(ganadores.size()==2,"dos avanzan");
        check(ganadores.containsAll(cruces.keySet()),"ganadores exactos");
        check(ss.generarCruces(asc).size()==1,"final 2 equipos");
        var finalistas=ss.generarCruces(asc).entrySet().iterator().next();
        ss.registrarEncuentroExterno(asc,finalistas.getKey(),finalistas.getValue(),2,0,"ELIMINACION",null);
        check(ss.avanzarRonda(asc).equals(List.of(finalistas.getKey())),"campeon unico");
        check(ss.getEncuentrosExternos(asc).size()==7,"cuatro grupos+dos semis+final");
    }
    private static void probarAleatorias() {
        final Random rnd=new Random(20261101);
        for(int iter=0;iter<35;iter++){
            ServicioDeportivo ss=new ServicioDeportivo();
            int cupo=7+rnd.nextInt(10);
            Equipo e=equipo("Aleatorio"+iter,new Deporte("Futbol"),new Categoria("Mayores"),cupo);
            ss.registrarEquipo(e);
            int[] camisetas=new int[cupo];
            for(int i=0;i<cupo;i++){
                int edad=17+rnd.nextInt(50),cam=i+1;
                Socio s=socio(edad);ss.inscribir(s,e,"Pos",cam);camisetas[i]=cam;
                check(s.getInscripciones().get(0)==e.getInscripciones().get(i),"referencia bidireccional aleatoria "+iter+"/"+i);
            }
            check(e.getInscripciones().size()==cupo,"cupo aleatorio llenado " + iter);
            final int camisetaExistente=camisetas[rnd.nextInt(cupo)];
            reject(()->ss.inscribir(socio(20),e,"Pos",camisetaExistente),"cupo/numero aleatorio " + iter);
        }
    }
    public static void main(String[] args) {
        probarInscripciones();probarPartidos();probarCompetencia();probarTablasYEliminatorias();probarAleatorias();
        System.out.println("OK: " + n + " verificaciones de auditoria integral superadas.");
    }
}
