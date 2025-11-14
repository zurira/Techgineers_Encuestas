package mx.edu.utez.encuestas.model;

public class Usuario {
    private int id;
    private String correo;
    private String nombre;
    private String nombreUsuario;
    private String contraseña;

    // Constructor, getters y setters
    public Usuario(int id, String correo, String nombre, String nombreUsuario, String contraseña) {
        this.id = id;
        this.correo = correo;
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public String getContraseña() { return contraseña; }
    public String getNombre() { return nombre; }
}
