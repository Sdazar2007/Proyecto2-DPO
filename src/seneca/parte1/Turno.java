package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class Turno implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dia;
    private String horaInicio;
    private String horaFin;
    private Empleado empleado;
    private boolean activo;

    public Turno() {}

    public Turno(String dia, String horaInicio, String horaFin, Empleado empleado, boolean activo) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.empleado = empleado;
        this.activo = activo;
    }

    public String getDia() { return dia; }
    public void setDia(String value) { this.dia = value; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String value) { this.horaInicio = value; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String value) { this.horaFin = value; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado value) { this.empleado = value; }

    public boolean getActivo() { return activo; }
    public void setActivo(boolean value) { this.activo = value; }
    public boolean isActivo() { return activo; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Turno x = new Turno();
        public Builder dia(String value) { x.setDia(value); return this; }
        public Builder horaInicio(String value) { x.setHoraInicio(value); return this; }
        public Builder horaFin(String value) { x.setHoraFin(value); return this; }
        public Builder empleado(Empleado value) { x.setEmpleado(value); return this; }
        public Builder activo(boolean value) { x.setActivo(value); return this; }
        public Turno build() { return x; }
    }
}
