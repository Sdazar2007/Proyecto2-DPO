package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Clase basada en el UML aprobado de la Parte 1. */
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private String login;
    private boolean activo = true;
    private String password;

    public Usuario() {}

    public Usuario(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Usuario instance = new Usuario();
        public Builder login(String value) { instance.setLogin(value); return this; }
        public Builder password(String value) { instance.setPassword(value); return this; }
        public Usuario build() { return instance; }
    }
}
