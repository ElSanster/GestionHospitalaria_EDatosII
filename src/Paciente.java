package main;
import java.time.LocalDate;
/**
 *
 * @author El_Sanster
 * Esta clase solo sirve para cargar los datos a la memoria
 * obtener datos cargados previamente de la clase de lista de pacientes
 */
public class Paciente {

    String nombre, eps;
    LocalDate fechaNacimiento;
    int id;

    public Paciente(String nombre, String eps, LocalDate fechaNacimiento, int id) {
        this.nombre = nombre;
        this.eps = eps;
        this.fechaNacimiento = fechaNacimiento;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEps() {
        return eps;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + "| " + nombre + "|" + eps + "|" + fechaNacimiento ;
    }
}
