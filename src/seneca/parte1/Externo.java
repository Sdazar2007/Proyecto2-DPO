package seneca.parte1;

public class Externo extends Usuario {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String documento;

    public Externo() {}

    public Externo(String login, String password, String nombre, String documento) {
        super(login, password);
        this.nombre = nombre;
        this.documento = documento;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
}
