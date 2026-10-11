package cl.biblioteca.model;

public class Usuario extends Persona {

    private String correo;
    private String contrasena;
    private String rol;

    public Usuario(int id, String nombre, String rut, String correo, String contrasena, String rol) {
        super(id, nombre, rut);
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public String mostrarInformacion() {
        return "Usuario: " + nombre +
                " | RUT: " + rut +
                " | Correo: " + correo +
                " | Rol: " + rol;
    }
}
