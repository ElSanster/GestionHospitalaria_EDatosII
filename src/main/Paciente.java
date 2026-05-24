package main;
import java.time.LocalDate;
/**
 *
 * @author El_Sanster & Natt
 * Esta clase solo sirve para cargar los datos a la memoria
 * obtener datos cargados previamente de la clase de lista de pacientes
 */
public class Paciente {

    private String nombre, eps;
    private LocalDate fechaNacimiento;
    int id;
    Paciente nodoIzq, nodoDer;

    public Paciente(String nombre, String eps, LocalDate fechaNacimiento, int id) {
        this.nombre = nombre;
        this.eps = eps;
        this.fechaNacimiento = fechaNacimiento;
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void setAllData (String nombre, String eps, LocalDate fechaNacimiento, int id){
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
    
    // Lo cambiamos para que use un formato de colimnas y se vea organizado
    // pq cuando ejecutamos el codigo de ver la lista, se ve uno sobre otro
    @Override
    public String toString() {
        return String.format("| %-5d | %-20s | %-10s | %-12s |\n",
                                  id,  nombre,   eps,  fechaNacimiento);
    }
    
    // Para interfaz Gráfica
    // Devolver un array con datos del usuario
    public Object[] toArray() {
        return new Object[]{id, nombre, eps, fechaNacimiento};
    }
}
