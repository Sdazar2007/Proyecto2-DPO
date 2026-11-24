package seneca.parte1;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/** Parte 2: disciplinas individuales, reservas, ranking y sesiones. */
public final class ServicioAcademias implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<InscripcionIndividual> inscripciones = new ArrayList<>();
    private final List<Instalacion> instalaciones = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();
    private final List<PartidoPractica> partidos = new ArrayList<>();
    private final List<Object> estadisticas = new ArrayList<>();
    private final List<SesionEntrenamiento> sesiones = new ArrayList<>();
    private final Map<InscripcionIndividual,List<Integer>> historialRanking = new IdentityHashMap<>();
    private final Map<String,Integer> cuposSimultaneos = new HashMap<>();
    private final Set<Socio> sociosEntrenadores = Collections.newSetFromMap(new IdentityHashMap<>());

    private static void require(boolean ok, String mensaje) { if (!ok) throw new IllegalArgumentException(mensaje); }
    private static String norm(String s) { return s == null ? "" : s.trim().toLowerCase(Locale.ROOT).replace("á","a").replace("é","e"); }
    public static String disciplina(Deporte d) {
        require(d != null, "Deporte obligatorio");
        String n = norm(d.getNombre());
        if (n.equals("tenis") || n.equals("tennis")) return "TENIS";
        if (n.equals("padel")) return "PADEL";
        if (n.equals("tenis de mesa") || n.equals("tenis mesa") || n.equals("ping pong")) return "MESA";
        throw new IllegalArgumentException("Disciplina individual no reconocida: " + d.getNombre());
    }
    public static String nivel(String nivel) {
        String n = norm(nivel);
        if (n.equals("principiante")) return "PRINCIPIANTE";
        if (n.equals("intermedio")) return "INTERMEDIO";
        if (n.equals("avanzado")) return "AVANZADO";
        throw new IllegalArgumentException("Nivel invalido");
    }
    private static boolean mismoIndividual(Instalacion i,String d){
        try{return disciplina(i.getDeporte()).equals(d);}catch(IllegalArgumentException ex){return false;}
    }
    private static int peso(String n) { return switch (n) { case "PRINCIPIANTE" -> 0; case "INTERMEDIO" -> 1; default -> 2; }; }
    private static LocalDateTime momento(String s) {
        try { return LocalDateTime.parse(s); }
        catch (DateTimeParseException | NullPointerException e) { throw new IllegalArgumentException("Bloque debe ser ISO 8601: AAAA-MM-DDTHH:MM",e); }
    }
    private static boolean solapa(LocalDateTime inicio, int minutos, LocalDateTime otro, int otrosMinutos) {
        return inicio.isBefore(otro.plusMinutes(otrosMinutos)) && otro.isBefore(inicio.plusMinutes(minutos));
    }
    private static void sociosDistintos(List<Socio> jugadores){
        require(jugadores != null && jugadores.stream().noneMatch(Objects::isNull), "Participantes invalidos");
        Set<Socio> vistos = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Socio s:jugadores) require(vistos.add(s), "Participante repetido");
    }
    public InscripcionIndividual inscribir(Socio socio,Deporte deporte,String nombreNivel){
        require(socio!=null && socio.isActivo(),"Socio no activo");
        String d=disciplina(deporte), n=nivel(nombreNivel);
        require(buscar(socio,deporte)==null,"Una inscripcion por disciplina");
        InscripcionIndividual i=new InscripcionIndividual(n,1000,socio,deporte);
        inscripciones.add(i);historialRanking.put(i,new ArrayList<>(List.of(1000)));return i;
    }
    public InscripcionIndividual buscar(Socio socio,Deporte deporte){
        String d=disciplina(deporte);
        for(InscripcionIndividual i:inscripciones) if(i.getSocio()==socio && disciplina(i.getDeporte()).equals(d)) return i;
        return null;
    }
    public void declararSocioEntrenador(Socio socio, boolean esEntrenador){
        require(socio!=null,"Socio requerido"); if(esEntrenador)sociosEntrenadores.add(socio); else sociosEntrenadores.remove(socio);
    }
    public void registrarInstalacion(Instalacion instalacion){
        require(instalacion!=null && instalacion.getNombre()!=null && !instalacion.getNombre().isBlank() && instalacion.getCapacidad()>=2,"Instalacion invalida");
        require(instalacion.getDeporte()!=null && instalacion.getDeporte().getNombre()!=null && !instalacion.getDeporte().getNombre().isBlank(),"Deporte de instalacion obligatorio");
        require(!instalaciones.contains(instalacion),"Instalacion duplicada");
        require(instalaciones.stream().noneMatch(x->x.getNombre().equalsIgnoreCase(instalacion.getNombre())),"Nombre de instalacion duplicado");
        instalacion.setActiva(true);
        instalaciones.add(instalacion);
    }
    private int capacidadPermitida(String d,String modalidad){
        String m=norm(modalidad);
        require(m.equals("sencillos") || m.equals("dobles"),"Modalidad invalida");
        if(d.equals("PADEL")){require(m.equals("dobles"),"Padel solo admite dobles");return 4;}
        return m.equals("sencillos")?2:4;
    }
    public void configurarLimiteSimultaneo(Deporte deporte,int maximo){
        String d=disciplina(deporte);require(maximo>=1,"Limite simultaneo invalido");
        cuposSimultaneos.put(d,maximo);
    }
    public int limiteSimultaneo(Deporte deporte){
        String d=disciplina(deporte);
        long fisicas=instalaciones.stream().filter(i->i.isActiva()&&mismoIndividual(i,d)).count();
        return (int)Math.min(fisicas,cuposSimultaneos.getOrDefault(d,Integer.MAX_VALUE));
    }
    /** Cada reserva representa un bloque de una hora; las instalaciones son inventario finito. */
    public Reserva reservar(Socio responsable, Instalacion instalacion,String bloque,String modalidad,List<Socio> jugadores){
        require(instalaciones.contains(instalacion)&&instalacion.isActiva(),"Instalacion no disponible");
        require(responsable!=null && responsable.isActivo(),"Socio no habilitado");
        String d=disciplina(instalacion.getDeporte());
        int cantidad=capacidadPermitida(d,modalidad);
        sociosDistintos(jugadores);
        require(jugadores.size()==cantidad && cantidad<=instalacion.getCapacidad(),"Cantidad incompatible con la instalacion");
        require(jugadores.contains(responsable),"El responsable debe participar");
        for(Socio jugador:jugadores) require(jugador.isActivo() && buscar(jugador,instalacion.getDeporte())!=null,"Jugador no inscrito en disciplina");
        LocalDateTime inicio=momento(bloque);
        for(Reserva existente:reservas) if(!existente.isCancelada() && solapa(inicio,60,momento(existente.getBloqueHorario()),60)){
            for(Socio jugador:jugadores)require(!existente.getParticipantes().contains(jugador),"Socio ya reservado en ese horario");
        }
        for(Reserva r:reservas) if(!r.isCancelada() && r.getInstalacion()==instalacion && solapa(inicio,60,momento(r.getBloqueHorario()),60))
            throw new IllegalArgumentException("Instalacion ocupada en ese bloque");
        // Rechaza cupos cuando todas las instalaciones de la disciplina estan reservadas.
        long ocupadas=reservas.stream().filter(r->!r.isCancelada() && disciplina(r.getInstalacion().getDeporte()).equals(d)
              && solapa(inicio,60,momento(r.getBloqueHorario()),60)).map(Reserva::getInstalacion).distinct().count();
        long total=instalaciones.stream().filter(x->x.isActiva() && mismoIndividual(x,d)).count();
        require(ocupadas<Math.min(total,cuposSimultaneos.getOrDefault(d,Integer.MAX_VALUE)),"Capacidad simultanea de instalaciones agotada");
        Reserva reserva=new Reserva(bloque,cantidad,modalidad,responsable,instalacion,new ArrayList<>(jugadores),false);
        reservas.add(reserva);return reserva;
    }
    public void cancelarReserva(Reserva r,Socio responsable){
        require(reservas.contains(r)&&r.getResponsable()==responsable && !r.isCancelada(),"No se puede cancelar reserva");
        require(partidos.stream().noneMatch(p->p.getInstalacion()==r.getInstalacion()&&p.getFecha().equals(fecha(r.getBloqueHorario()))),"Reserva con partido asociado");
        r.setCancelada(true);
    }
    public static Date fecha(String iso){return Date.from(momento(iso).atZone(ZoneId.systemDefault()).toInstant());}
    public PartidoPractica programarPractica(Reserva reserva){
        require(reservas.contains(reserva)&&!reserva.isCancelada(),"Reserva inexistente");
        require(partidos.stream().noneMatch(p->p.getInstalacion()==reserva.getInstalacion() && p.getFecha().equals(fecha(reserva.getBloqueHorario()))),"Partido ya programado");
        List<Socio> j=reserva.getParticipantes();
        for(Socio socio:j)require(socio.isActivo(),"Socio dado de baja no puede disputar el partido");
        for(int i=0;i<j.size();i++)for(int k=i+1;k<j.size();k++){
            InscripcionIndividual a=buscar(j.get(i),reserva.getInstalacion().getDeporte()), b=buscar(j.get(k),reserva.getInstalacion().getDeporte());
            require(Math.abs(peso(a.getNivel())-peso(b.getNivel()))<=1 || sociosEntrenadores.contains(j.get(i)) || sociosEntrenadores.contains(j.get(k)),
                  "Jugadores de nivel no compatible");
        }
        PartidoPractica p=new PartidoPractica(fecha(reserva.getBloqueHorario()),reserva.getInstalacion().getDeporte(),reserva.getInstalacion(),
            new ArrayList<>(j),new ArrayList<>(),false);
        partidos.add(p);return p;
    }
    private static int ajusteElo(int rating, double resultado, double esperado){return (int)Math.round(32*(resultado-esperado));}
    public void finalizarPartido(PartidoPractica partido,List<Socio> ganadores,Map<Socio,int[]> datos){
        require(partidos.contains(partido)&&!partido.isFinalizado(),"Partido invalido o terminado");
        for(Socio s:partido.getParticipantes())require(s.isActivo(),"Participante inactivo");
        sociosDistintos(ganadores);require(!ganadores.isEmpty() && ganadores.size()*2==partido.getParticipantes().size(),"Debe haber ganadores y perdedores");
        require(partido.getParticipantes().containsAll(ganadores),"Ganadores ajenos al partido");
        require(datos!=null && datos.size()==partido.getParticipantes().size(),"Faltan estadisticas de participantes");
        String d=disciplina(partido.getDeporte());
        for(Socio s:partido.getParticipantes()){
            int[] v=datos.get(s);
            require(v!=null && v.length==(d.equals("MESA")?2:4),"Estadistica incompleta");
            for(int n:v)require(n>=0,"Estadistica negativa");
            if(d.equals("MESA"))require(v[0]<=5,"Sets de tenis mesa invalidos");
            else require(v[0]<=5,"Sets de tenis/padel invalidos");
        }
        int setsGanadores=ganadores.stream().mapToInt(s->datos.get(s)[0]).sum();
        int setsPerdedores=partido.getParticipantes().stream().filter(s->!ganadores.contains(s)).mapToInt(s->datos.get(s)[0]).sum();
        require(setsGanadores>setsPerdedores,"El ganador debe tener mas sets");
        double mediaA=ganadores.stream().mapToInt(s->buscar(s,partido.getDeporte()).getRankingELO()).average().orElseThrow();
        double mediaB=partido.getParticipantes().stream().filter(s->!ganadores.contains(s)).mapToInt(s->buscar(s,partido.getDeporte()).getRankingELO()).average().orElseThrow();
        double esperado=1/(1+Math.pow(10,(mediaB-mediaA)/400));
        int dGan=ajusteElo((int)mediaA,1,esperado),dPer=ajusteElo((int)mediaB,0,1-esperado);
        for(Socio s:partido.getParticipantes()){
            int[] v=datos.get(s);
            if(d.equals("MESA"))estadisticas.add(new EstadisticaTenisMesa(v[0],v[1],s,partido));
            else estadisticas.add(new EstadisticaTenisPadel(v[0],v[1],v[2],v[3],s,partido));
            InscripcionIndividual i=buscar(s,partido.getDeporte());i.setRankingELO(Math.max(0,i.getRankingELO()+(ganadores.contains(s)?dGan:dPer)));
            historialRanking.computeIfAbsent(i,k->new ArrayList<>()).add(i.getRankingELO());
        }
        partido.setGanadores(new ArrayList<>(ganadores));partido.setFinalizado(true);
    }
    public SesionEntrenamiento programarSesion(Entrenador entrenador,Instalacion instalacion,String bloque,int duracion,List<Socio> convocados){
        require(entrenador!=null && entrenador.isActivo(),"Entrenador invalido");
        require(instalaciones.contains(instalacion)&&instalacion.isActiva(),"Instalacion no registrada");
        require(duracion>0&&duracion<=360,"Duracion invalida");
        sociosDistintos(convocados);require(!convocados.isEmpty(),"Sin convocados");
        for(Socio s:convocados)require(s.isActivo(),"Convocado inactivo");
        LocalDateTime inicio=momento(bloque);
        require(sesiones.stream().noneMatch(s->(s.getEntrenador()==entrenador || s.getInstalacion()==instalacion)
          && solapa(inicio,duracion,momento(s.getHora()),s.getDuracion())),"Conflicto de horario entrenador/instalacion");
        require(reservas.stream().noneMatch(r->!r.isCancelada()&&r.getInstalacion()==instalacion&&solapa(inicio,duracion,momento(r.getBloqueHorario()),60)),"Instalacion reservada");
        String dia=bloque.substring(0,10);
        SesionEntrenamiento s=new SesionEntrenamiento(fecha(bloque),bloque,duracion,entrenador,instalacion,new ArrayList<>(convocados),new ArrayList<>());
        sesiones.add(s);return s;
    }
    public Evaluacion evaluar(SesionEntrenamiento sesion,Socio socio,String asistencia,Double nota,String comentario){
        require(sesiones.contains(sesion)&&sesion.getConvocados().contains(socio),"Socio no convocado");
        String a=norm(asistencia).toUpperCase(Locale.ROOT);
        require(Set.of("ASISTIO","FALTO","TARDE").contains(a),"Asistencia invalida");
        require(nota==null || (nota>=1.0 && nota<=5.0 && Double.isFinite(nota)),"Nota fuera de rango");
        require(sesion.getEvaluaciones().stream().noneMatch(e->e.getSocio()==socio),"Evaluacion duplicada");
        Evaluacion e=new Evaluacion(a,nota==null?0:nota,comentario==null?"":comentario,socio,sesion);
        sesion.getEvaluaciones().add(e);return e;
    }
    public List<InscripcionIndividual> getInscripciones(){return List.copyOf(inscripciones);}
    public List<Instalacion> getInstalaciones(){return List.copyOf(instalaciones);}
    public List<Reserva> getReservas(){return List.copyOf(reservas);}
    public List<PartidoPractica> getPartidos(){return List.copyOf(partidos);}
    public List<Object> getEstadisticas(){return List.copyOf(estadisticas);}
    public List<SesionEntrenamiento> getSesiones(){return List.copyOf(sesiones);}
    public List<Evaluacion> asistencia(Socio s){return sesiones.stream().flatMap(x->x.getEvaluaciones().stream()).filter(e->e.getSocio()==s).toList();}
    public int ranking(Socio s,Deporte d){InscripcionIndividual i=buscar(s,d);require(i!=null,"Socio no inscrito");return i.getRankingELO();}
    public List<Integer> evolucionRanking(Socio s,Deporte d){InscripcionIndividual i=buscar(s,d);require(i!=null,"Socio no inscrito");return List.copyOf(historialRanking.getOrDefault(i,List.of(i.getRankingELO())));}
    public boolean dentroInstalacionRestringida(Socio s, LocalDateTime hora){
        return reservas.stream().anyMatch(r->!r.isCancelada() && r.getParticipantes().contains(s) && restringida(r.getInstalacion())
         && solapa(hora,1,momento(r.getBloqueHorario()),60));
    }
    public boolean restringida(Instalacion i){
        try {String d=disciplina(i.getDeporte());return d.equals("PADEL") || (d.equals("MESA") && i.isTechada());}
        catch(IllegalArgumentException noIndividual){return false;}
    }
    public boolean reservaRestringida(Socio s,String bloque){
        LocalDateTime hora=momento(bloque);
        return dentroInstalacionRestringida(s,hora);
    }
    public void exigirSinConflictoEntrenamientos(Instalacion instalacion,String bloque){
        LocalDateTime h=momento(bloque);
        require(sesiones.stream().noneMatch(s->s.getInstalacion()==instalacion && solapa(h,60,momento(s.getHora()),s.getDuracion())),"Instalacion ocupada por entrenamiento");
    }
}
