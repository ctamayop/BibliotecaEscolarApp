package cl.biblioteca.model;

public class Estudiante extends Persona {

    private String curso;
    private String correo;

    public Estudiante(int id, String nombre, String rut, String curso, String correo) {
        super(id, nombre, rut);
        this.curso = curso;
        this.correo = correo;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String mostrarInformacion() {
        return "Estudiante: " + nombre +
                " | RUT: " + rut +
                " | Curso: " + curso +
                " | Correo: " + correo;
    }
}
