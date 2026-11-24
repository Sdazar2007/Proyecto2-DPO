package seneca.parte1;

public class Estudiante extends Usuario {
    private static final long serialVersionUID = 1L;
    private String codigoEstudiantil;
    private boolean matriculado;

    public Estudiante() {}

    public Estudiante(String login, String password, String codigoEstudiantil, boolean matriculado) {
        super(login, password);
        this.codigoEstudiantil = codigoEstudiantil;
        this.matriculado = matriculado;
    }

    public String getCodigoEstudiantil() { return codigoEstudiantil; }
    public void setCodigoEstudiantil(String codigoEstudiantil) { this.codigoEstudiantil = codigoEstudiantil; }
    public boolean isMatriculado() { return matriculado; }
    public void setMatriculado(boolean matriculado) { this.matriculado = matriculado; }
}
