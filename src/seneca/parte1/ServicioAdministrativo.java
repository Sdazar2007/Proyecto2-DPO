package seneca.parte1;

import java.io.Serializable;
import java.time.*;
import java.util.*;

/** Parte 3: turnos, peticiones, autorizaciones y requisitos de cobertura. */
public final class ServicioAdministrativo implements Serializable {
    private static final long serialVersionUID=1L;
    private final List<Turno> turnos=new ArrayList<>();
    private final List<Sugerencia> sugerencias=new ArrayList<>();
    private final EnumSet<DayOfWeek> diasAbiertos=EnumSet.allOf(DayOfWeek.class);
    private LocalTime apertura=LocalTime.of(8,0), cierre=LocalTime.of(18,0);
    private static void exigir(boolean ok,String m){if(!ok)throw new IllegalArgumentException(m);}
    private static void admin(Administrador a){exigir(a!=null && a.isActivo(),"Solo un administrador activo puede realizar esta accion");}
    private static DayOfWeek dia(String dia){
        exigir(dia!=null,"Dia obligatorio");
        String x=dia.trim().toUpperCase(Locale.ROOT);
        return switch(x){
            case "LUNES","MONDAY" -> DayOfWeek.MONDAY;
            case "MARTES","TUESDAY" -> DayOfWeek.TUESDAY;
            case "MIERCOLES","MIÉRCOLES","WEDNESDAY" -> DayOfWeek.WEDNESDAY;
            case "JUEVES","THURSDAY" -> DayOfWeek.THURSDAY;
            case "VIERNES","FRIDAY" -> DayOfWeek.FRIDAY;
            case "SABADO","SÁBADO","SATURDAY" -> DayOfWeek.SATURDAY;
            case "DOMINGO","SUNDAY" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Dia invalido");
        };
    }
    private static LocalTime hora(String s){try{return LocalTime.parse(s);}catch(RuntimeException ex){throw new IllegalArgumentException("Hora debe ser HH:MM",ex);}}
    private static void validarTurno(Turno t){
        exigir(t!=null&&t.getEmpleado()!=null&&t.getEmpleado().isActivo(),"Empleado no valido");
        exigir(t.getEmpleado() instanceof Entrenador || t.getEmpleado() instanceof Fisioterapeuta,"Rol no admite turnos deportivos");
        dia(t.getDia());exigir(hora(t.getHoraInicio()).isBefore(hora(t.getHoraFin())),"Intervalo de turno invalido");
    }
    private boolean solapaEmpleado(Turno t,Turno omitir){
        for(Turno x:turnos) if(x!=omitir&&x.isActivo() && x.getEmpleado()==t.getEmpleado() && dia(x.getDia())==dia(t.getDia()) &&
         hora(x.getHoraInicio()).isBefore(hora(t.getHoraFin()))&&hora(t.getHoraInicio()).isBefore(hora(x.getHoraFin())))return true;
        return false;
    }
    public boolean enTurno(Empleado e,LocalDate fecha){return turnos.stream().anyMatch(t->t.isActivo()&&t.getEmpleado()==e&&dia(t.getDia())==fecha.getDayOfWeek());}
    public Turno crearTurno(Administrador a,Empleado e,String dia,String inicio,String fin){
        admin(a);Turno t=new Turno(dia,inicio,fin,e,true);validarTurno(t);
        exigir(!solapaEmpleado(t,null),"Turnos superpuestos del mismo empleado");turnos.add(t);return t;
    }
    public void configurarHorarioAtencion(Administrador a,Set<DayOfWeek> dias,LocalTime apertura,LocalTime cierre){
        admin(a);exigir(dias!=null&&!dias.isEmpty()&&apertura!=null&&cierre!=null&&apertura.isBefore(cierre),"Horario invalido");
        EnumSet<DayOfWeek> antes=EnumSet.copyOf(diasAbiertos);LocalTime anteriorApertura=this.apertura,c=this.cierre;
        diasAbiertos.clear();diasAbiertos.addAll(dias);this.apertura=apertura;this.cierre=cierre;
        if(!turnos.isEmpty()&&!coberturaCompleta()){
            diasAbiertos.clear();diasAbiertos.addAll(antes);this.apertura=anteriorApertura;this.cierre=c;
            throw new IllegalArgumentException("Nuevo horario deja turnos sin cobertura");
        }
    }
    /** Cobertura minuto a minuto, deduplicada por identidad para evitar contar 2 turnos de 1 empleado. */
    public boolean coberturaCompleta(){
        for(DayOfWeek d:diasAbiertos){
            for(LocalTime h=apertura;h.isBefore(cierre);h=h.plusMinutes(1)){
                Set<Empleado> presentes=Collections.newSetFromMap(new IdentityHashMap<>());
                for(Turno t:turnos)if(t.isActivo()&&t.getEmpleado().isActivo()&&dia(t.getDia())==d
                     &&!hora(t.getHoraInicio()).isAfter(h)&&hora(t.getHoraFin()).isAfter(h))presentes.add(t.getEmpleado());
                long entrenadores=presentes.stream().filter(e->e instanceof Entrenador).count();
                long fisioterapeutas=presentes.stream().filter(e->e instanceof Fisioterapeuta).count();
                if(entrenadores<2 || fisioterapeutas<1)return false;
            }
        }
        return true;
    }
    public void cambiarTurno(Administrador a,Turno original,String nuevoDia,String inicio,String fin){
        admin(a);exigir(turnos.contains(original)&&original.isActivo(),"Turno no registrado");
        Turno propuesta=new Turno(nuevoDia,inicio,fin,original.getEmpleado(),true);validarTurno(propuesta);
        exigir(!solapaEmpleado(propuesta,original),"Empleado ocupado en el nuevo horario");
        String od=original.getDia(),oi=original.getHoraInicio(),of=original.getHoraFin();
        original.setDia(nuevoDia);original.setHoraInicio(inicio);original.setHoraFin(fin);
        if(!coberturaCompleta()){
            original.setDia(od);original.setHoraInicio(oi);original.setHoraFin(of);
            throw new IllegalArgumentException("Cambio de turno incumple minimo 2 entrenadores + 1 fisioterapeuta");
        }
    }
    public void darBajaTurno(Administrador a,Turno t){
        admin(a);exigir(turnos.contains(t)&&t.isActivo(),"Turno no existente");
        t.setActivo(false);
        if(!coberturaCompleta()){t.setActivo(true);throw new IllegalArgumentException("Dar de baja rompe cobertura");}
    }
    public Sugerencia sugerir(Empleado e,String nombre,String tipo,String motivo){
        exigir(e!=null&&e.isActivo()&&nombre!=null&&!nombre.isBlank(),"Sugerencia invalida");
        exigir(tipo!=null&&(tipo.equalsIgnoreCase("ARTICULO")||tipo.equalsIgnoreCase("SNACK")),"Tipo de sugerencia invalido");
        Sugerencia s=new Sugerencia(nombre,"PENDIENTE",e,tipo.toUpperCase(Locale.ROOT),motivo);
        sugerencias.add(s);return s;
    }
    public void decidirSugerencia(Administrador admin,Sugerencia s,boolean aprobada,Producto producto,ServicioComercial comercial){
        admin(admin);exigir(sugerencias.contains(s)&&"PENDIENTE".equals(s.getEstado()),"Sugerencia ya resuelta");
        if(aprobada){
            exigir(producto!=null && producto.getNombre().equalsIgnoreCase(s.getNombre()),"Producto no coincide con la sugerencia");
            exigir((s.getTipo().equals("ARTICULO")&&producto instanceof ArticuloTienda) ||
                   (s.getTipo().equals("SNACK")&&producto instanceof Snack),"Tipo de producto diferente");
            comercial.registrarProducto(producto);
        }
        s.setEstado(aprobada?"APROBADA":"RECHAZADA");
    }
    public List<Turno> getTurnos(){return List.copyOf(turnos);}
    public List<Sugerencia> getSugerencias(){return List.copyOf(sugerencias);}
}
