package seneca.consola;

import seneca.parte1.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.function.Function;
public abstract class ConsolaBase {
    protected ClubSeneca club;
    protected final Scanner scanner=new Scanner(System.in);
    protected final EntradaConsola entrada=new EntradaConsola(scanner,System.out);
    protected Usuario usuarioActual;
    private final Path ruta=Path.of("datos","club.dat");
    protected abstract boolean admite(Usuario u);
    protected abstract void mostrarMenu();
    protected boolean puedeInicializar(){return false;}
    protected void ejecutar(){
        try{
            if(Files.exists(ruta))club=ClubSeneca.cargar(ruta);
            else if(puedeInicializar()){club=new ClubSeneca();System.out.println("Crear administrador inicial");club.crearAdministradorInicial(texto("Login: "),texto("Clave: "));club.guardar(ruta);}
            else {System.out.println("Primero inicialice desde ConsolaAdmin.");return;}
            if(autenticar()){try{mostrarMenu();}finally{club.guardar(ruta);}}
        }catch(NoSuchElementException e){System.out.println("Entrada finalizada.");}catch(Exception e){System.out.println("Error: "+e.getMessage());}
    }
    protected boolean autenticar(){for(int i=0;i<3;i++){Usuario u=club.autenticar(texto("Login: "),texto("Clave: "));if(u!=null&&admite(u)){usuarioActual=u;return true;}System.out.println("Credenciales o rol incorrectos.");}return false;}
    protected String texto(String s){return entrada.leerTexto(s);}
    protected int entero(String s,int min,int max){return entrada.leerEntero(s,min,max);}
    protected double decimal(String s){return entrada.leerDecimal(s);}
    protected boolean si(String s){return entero(s+" (1 si, 0 no): ",0,1)==1;}
    protected void seguro(Runnable accion){try{accion.run();}catch(IllegalArgumentException|IllegalStateException|java.time.DateTimeException|ArithmeticException e){System.out.println("Error: "+e.getMessage());}}
    protected <T> T elegir(List<T> lista,Function<T,String> etiqueta){if(lista.isEmpty())throw new IllegalArgumentException("No hay elementos disponibles");for(int i=0;i<lista.size();i++)System.out.println((i+1)+". "+etiqueta.apply(lista.get(i)));return lista.get(entero("Seleccione: ",1,lista.size())-1);}
    protected Usuario usuario(){return elegir(club.getUsuarios().stream().filter(Usuario::isActivo).toList(),Usuario::getLogin);}
    protected Socio socio(){return elegir(club.getUsuarios().stream().filter(u->u instanceof Socio&&u.isActivo()).map(u->(Socio)u).toList(),Usuario::getLogin);}
    protected Equipo equipo(){return elegir(club.getDeportivo().getEquipos().stream().filter(Equipo::isActivo).toList(),Equipo::getNombre);}
    protected Instalacion instalacion(){return elegir(club.getAcademias().getInstalaciones().stream().filter(Instalacion::isActiva).toList(),Instalacion::getNombre);}
    protected Torneo torneo(){return elegir(club.getTorneos().consultarTorneos(),Torneo::toString);}
    protected Deporte deporte(){return new Deporte(texto("Deporte: "));}
    protected void consultarTorneos(){Torneo t=torneo();System.out.println(t+" "+t.getDeporte().getNombre()+" "+t.getCategoria().getNombre()+" "+t.getFecha()+" "+t.getModalidad());System.out.println("Cupos preferenciales: "+t.calcularCuposPreferenciales()+"; regulares libres: "+t.cuposRegularesDisponibles());for(InscripcionTorneo i:t.getInscripciones())System.out.println(i.getParticipante()+" activa="+i.isActiva()+" preferencial="+i.isPreferencial()+" tarifa="+i.getValorInscripcion()+" posicion final="+i.getPosicionFinal());for(EncuentroTorneo e:t.getEncuentros())System.out.println(e);int pos=1;for(InscripcionTorneo i:t.consultarPosiciones())System.out.println((pos++)+". "+i.getParticipante()+" victorias="+t.victorias(i.getParticipante()));}
    protected void inscribirTorneo(){Torneo t=torneo();ParticipanteTorneo p=switch(t.getModalidad()){case INDIVIDUAL->ParticipanteTorneo.jugadores(List.of(usuarioActual));case DOBLES->ParticipanteTorneo.jugadores(List.of(usuarioActual,usuario()));case EQUIPOS->ParticipanteTorneo.equipo(equipo());};InscripcionTorneo i=club.inscribirTorneo(usuarioActual,t.getId(),p);System.out.println("Inscrito: "+i.getValorInscripcion()+" preferencial="+i.isPreferencial());}
    protected void desinscribirTorneo(){Torneo t=torneo();InscripcionTorneo i=elegir(t.getInscripciones().stream().filter(x->x.isActiva()&&x.getParticipante().contiene(usuarioActual)).toList(),x->x.getParticipante().getId());club.desinscribirTorneo(usuarioActual,t.getId(),i.getParticipante().getId());System.out.println("Cupo liberado");}
    protected void consultarBonos(){for(BonoDescuento b:club.getTorneos().consultarBonos(usuarioActual))System.out.println(b.getCodigo()+" descuento="+b.getPorcentaje()*100+"% disponible="+b.estaDisponible());}
    protected void comprar(){String rubro=entero("1 Tienda, 2 Cafeteria: ",1,2)==1?"TIENDA":"CAFETERIA";Map<Producto,Integer> carrito=new LinkedHashMap<>();do{Producto p=elegir(club.getComercial().getCatalogo().stream().filter(x->x.isActivo()&&(rubro.equals("TIENDA")?x instanceof ArticuloTienda:x instanceof Bebida||x instanceof Snack)).toList(),x->x.getNombre()+" $"+x.getPrecio());carrito.merge(p,entero("Cantidad: ",1,10000),Math::addExact);}while(si("Agregar otro producto"));List<String> alertas=club.consultarAlergenos(carrito);boolean confirma=alertas.isEmpty();if(!confirma){System.out.println(alertas);confirma=si("Confirma aviso de alergenos");}double propina=rubro.equals("TIENDA")?0:decimal("Propina (fraccion 0 a 1): ");Venta venta;if(si("Usar bono de torneo")){BonoDescuento b=elegir(club.getTorneos().consultarBonos(usuarioActual).stream().filter(BonoDescuento::estaDisponible).toList(),BonoDescuento::getCodigo);venta=club.comprarConBono(usuarioActual,b.getCodigo(),rubro,carrito,propina,confirma);}else{String codigo=si("Usar codigo empleado")?texto("Codigo: "):null;double promo=0;int puntos=usuarioActual instanceof Socio?entero("Puntos a redimir: ",0,((Socio)usuarioActual).getPuntos()):0;venta=club.comprarConCodigo(usuarioActual,rubro,carrito,codigo,promo,propina,puntos,LocalDateTime.now(),confirma);}System.out.println("Total: "+venta.getTotal()+" descuento: "+venta.getDescuento());}
}
