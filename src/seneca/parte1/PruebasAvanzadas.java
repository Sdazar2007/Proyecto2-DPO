package seneca.parte1;
import java.util.*;
import java.nio.file.*;
/** Pruebas adicionales de integridad y casos limite. */
public class PruebasAvanzadas {
 static int n=0;
 static void check(boolean b,String m){n++;if(!b)throw new AssertionError(m);}
 static void reject(Runnable r,String m){n++;try{r.run();throw new AssertionError("Acepto caso invalido: "+m);}catch(IllegalArgumentException ok){}}
 static Socio s(int age){Socio x=new Socio();x.setEdad(age);return x;}
 static Equipo e(String name,Deporte d,Categoria c,int max){return new Equipo(name,max,d,c,new ArrayList<>());}
 static Deporte d(String n){Deporte x=new Deporte();x.setNombre(n);return x;}
 static Categoria c(String n){Categoria x=new Categoria();x.setNombre(n);return x;}
 static List<Socio> fill(ServicioDeportivo sys,Equipo eq,int k,int age){List<Socio> out=new ArrayList<>();for(int i=0;i<k;i++){Socio x=s(age);sys.inscribir(x,eq,"Titular",i+1);out.add(x);}return out;}
 public static void main(String[] args)throws Exception{
  ServicioDeportivo sys=new ServicioDeportivo();Deporte fut=d("Futbol"),vol=d("Voleibol"),bas=d("Baloncesto");Categoria may=c("Mayores"),sub15=c("Sub-15");
  Equipo a=e("A",fut,may,12),b=e("B",fut,may,12),v=e("V",vol,may,8),q=e("Q",bas,may,8),young=e("J",fut,sub15,9);
  for(Equipo eq:Arrays.asList(a,b,v,q,young))sys.registrarEquipo(eq);
  reject(()->sys.registrarEquipo(e("A",fut,may,9)),"duplicado por valor");
  List<Socio> aa=fill(sys,a,9,20),bb=fill(sys,b,9,20),vv=fill(sys,v,6,20),qq=fill(sys,q,5,20);
  reject(()->sys.inscribir(s(17),young,"X",1),"edad sub15");
  check(sys.inscribir(s(14),young,"X",1)!=null,"edad sub15 valida");
  reject(()->sys.inscribir(aa.get(0),a,"X",10),"inscripcion duplicada");
  reject(()->sys.inscribir(s(20),a,"X",1),"camiseta duplicada");
  check(sys.jugadoresHabilitados(a).size()==9,"habilitados");
  Escenario es=new Escenario();es.setNombre("Principal");
  reject(()->sys.programarInterno(a,v,new Date(),es),"deporte distinto");
  reject(()->sys.programarInterno(a,young,new Date(),es),"categoria distinta");
  PartidoInterno p=sys.programarInterno(a,b,new Date(),es);
  reject(()->sys.registrarResultado(p,-1,0),"marcador negativo");
  sys.registrarResultado(p,1,1);check(sys.getPartidos().size()==1,"partido registrado");
  EstadisticaFutbol st=new EstadisticaFutbol();st.setPartido(p);st.setSocio(aa.get(0));st.setGoles(1);st.setAmarillas(2);sys.registrarEstadistica(st);
  check(!sys.habilitado(aa.get(0)),"sancion por amarillas");
  reject(()->sys.registrarEstadistica(st),"estadistica duplicada");
  EstadisticaBaloncesto wrong=new EstadisticaBaloncesto();wrong.setPartido(p);wrong.setSocio(aa.get(1));reject(()->sys.registrarEstadistica(wrong),"tipo de estadistica");
  PartidoInterno p2=sys.programarInterno(a,b,new Date(),es);sys.registrarResultado(p2,0,1);check(sys.habilitado(aa.get(0)),"cumple suspension en siguiente partido");
  Competencia cerros=new Competencia();cerros.setNombre("Cerros");cerros.setFormato("LIGA");sys.configurarCompetencia(cerros,fut,may);
  sys.inscribirCompetencia(a,cerros,aa);sys.inscribirCompetencia(b,cerros,bb);
  reject(()->sys.inscribirCompetencia(a,cerros,aa),"doble inscripcion torneo");
  InstitucionRival rival=new InstitucionRival();rival.setNombre("Rival");
  PartidoCompetencia pc=sys.programarCompetencia(cerros,a,rival,new Date(),es,aa);
  sys.configurarFase(pc,"LIGA",null);sys.registrarResultado(pc,2,0);
  check(sys.puntos(cerros,a)==3,"puntos futbol");check(sys.tabla(cerros).get(0)==a,"orden tabla");
  check(sys.resumenEquipo(cerros,a)[1]==1,"victorias");
  EstadisticaFutbol sf=new EstadisticaFutbol();sf.setSocio(aa.get(0));sf.setPartido(pc);sf.setGoles(2);sys.registrarEstadistica(sf);
  check(sys.estadisticaAcumulada(aa.get(0),cerros,"goles")==2,"goles torneo");
  check(sys.estadisticaAcumulada(aa.get(0),null,"goles")==3,"goles totales");
  Competencia ascun=new Competencia();ascun.setNombre("ASCUN");ascun.setFormato("GRUPOS+ELIMINACION");sys.configurarCompetencia(ascun,fut,may);sys.inscribirCompetencia(b,ascun,bb);
  PartidoCompetencia knockout=sys.programarCompetencia(ascun,b,rival,new Date(),es,bb);
  sys.configurarFase(knockout,"ELIMINACION",null);sys.registrarResultado(knockout,0,2);
  check(sys.eliminado(ascun,b),"eliminado por derrota");
  PartidoCompetencia ko2=sys.programarCompetencia(ascun,b,rival,new Date(),es,bb);
  reject(()->sys.configurarFase(ko2,"ELIMINACION",null),"eliminado no avanza");
  Competencia ligaV=new Competencia();sys.configurarCompetencia(ligaV,vol,may);sys.inscribirCompetencia(v,ligaV,vv);
  PartidoCompetencia pv=sys.programarCompetencia(ligaV,v,rival,new Date(),es,vv);
  reject(()->sys.registrarResultado(pv,2,1),"sets incompletos");sys.registrarResultado(pv,3,2);
  check(sys.puntos(ligaV,v)==2,"puntos voleibol");
  Competencia ligaB=new Competencia();sys.configurarCompetencia(ligaB,bas,may);sys.inscribirCompetencia(q,ligaB,qq);
  PartidoCompetencia pb=sys.programarCompetencia(ligaB,q,rival,new Date(),es,qq);
  reject(()->sys.registrarResultado(pb,50,50),"empate baloncesto");sys.registrarResultado(pb,80,70);
  Path tmp=Files.createTempFile("seneca-pruebas-avanzadas-",".bin");sys.guardar(tmp);
  ServicioDeportivo loaded=ServicioDeportivo.cargar(tmp);Files.deleteIfExists(tmp);
  check(loaded.getEquipos().size()==5,"persistencia equipos");check(loaded.getPartidos().size()==7,"persistencia partidos");
  Equipo loadedA=loaded.getEquipos().stream().filter(x->x.getNombre().equals("A")).findFirst().orElseThrow();
  Competencia loadedCerros=((PartidoCompetencia)loaded.getPartidos().get(2)).getCompetencia();
  check(loaded.puntos(loadedCerros,loadedA)==3,"persistencia puntos y referencias");
  System.out.println("OK: "+n+" verificaciones avanzadas superadas.");
 }
}
