package main;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
/*
 * @author El_Sanster 
 */

//Inicializa una lista de clase Paciente y carga datos desde * el csv
public class ListaPacientes {

    List<Paciente> listaPacientes = new ArrayList<>();
    String nombreArchivo;
    
    //Constructor que pide el archivo, y genera la lista de pacientes en base del csv
    public ListaPacientes(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
        File archivo = new File(nombreArchivo);

            if (archivo.exists() && archivo.length() > 0) {
            try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
                
                String linea;
                boolean esprimeralinea = true;
                
                //La idea es que la primera linea contenga los nombres de cada dato
                //El csv debería tener este orden: id, nombre, eps, fecha nacimiento
                
                while ((linea = br.readLine()) != null) {
                    if (esprimeralinea) {
                        esprimeralinea = false;
                        continue;
                    }
                    String[] campos = linea.split(",");
                    if (campos.length >= 4) {
                        int id = Integer.parseInt(campos[0]);
                        String nombre = campos[1];
                        String eps = campos[2];
                        LocalDate fechanacimiento = LocalDate.parse(campos[3]);

                        Paciente p = new Paciente(nombre, eps, fechanacimiento, id);
                        listaPacientes.add(p);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("error al cargar datos: " + e.getMessage());
            }
        }
    }

    //Util para mostrar la lista de pacientes
    public List<Paciente> getListaPacientes() {
        return listaPacientes;
    }
    
    public void imprimirListaPacientes(){
        if (listaPacientes.isEmpty()){
            System.out.println("La lista esta vacia.");
            return;
        }
        System.out.println("ID | Nombre | EPS | Fecha de Nacimiento");
        for (Paciente paciente : listaPacientes) {
            System.out.println(paciente.toString());
        }
    }

    //Añade un paciente a la lista, guardar csv
    public void agregarPaciente(Scanner sc) {
        
        try {
            System.out.println("--- agregar nuevo paciente ---");
            System.out.println("Ingrese el id: ");
            int id = Integer.parseInt(sc.nextLine());
            
            System.out.println("Ingrese nombre completo: ");
            String nombre = sc.nextLine();
            
            System.out.println("Ingrese la eps: ");
            String eps = sc.nextLine();
            
            System.out.println("Ingrese fecha de nacimiento (aaaa-mm-dd): ");
            String fechatxt = sc.nextLine();
            LocalDate fechaNac = LocalDate.parse(fechatxt);
            
            Paciente nuevo = new Paciente (nombre , eps, fechaNac, id);
            this.agregarNuevoPaciente(nuevo, true);
            
            System.out.println("Paciente agregado a la lista exitosamente.");
            }  catch (NumberFormatException e) {
                System.out.println("error: el id debe sesr un numero entero");
            }  catch (java.time.format.DateTimeParseException e){
                System.err.println("error: formato de fecha invalido (usa aaaa-mm-dd");
            }
    }
    public void agregarNuevoPaciente(Paciente p, boolean guardarCSV){
        listaPacientes.add(p);
        if (guardarCSV){
            rescribirArchivo();
        }
    }
    public void borrarPaciente(Scanner sc) {
        try{
            System.out.println("--- Eliminar paciente ---");
            System.out.println("Ingrese el id del paciente que desea eliminar");
            
            int idPaciente = Integer.parseInt(sc.nextLine());
            boolean eliminado = listaPacientes.removeIf(p -> p.getId() == idPaciente);
            
            if (eliminado){
                System.out.println("Paciente con el id '"+idPaciente+"' eliminado exitosamente");
                this.rescribirArchivo();
                System.out.println("Archivo actualizado correctamente.");
                
            }else{
                System.out.println("No se encontro ningun paciente con ese id.");
            }
        } catch(NumberFormatException e){
            System.err.println("error: El id debe ser un numero entero.");
        }
    }

    //Organizar lista por ID, si guardar con este orden al csv
    public void organizarListaPorID(boolean guardarCSV) {
        if (listaPacientes.isEmpty()) {
            System.out.println("la lista esta vacia.");
        }
        //ordenamos usando un comparador por el atributo de id
        listaPacientes.sort((o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
        this.rescribirArchivo();
        System.out.println("Lista organizada por id exitosamente.");
    }

    //Organizar lista por Nombre, guardar con este orden al csv
    public void organizarListaPorNombre(boolean guardarCSV) {
        listaPacientes.sort(Comparator.comparing(Paciente::getNombre, String.CASE_INSENSITIVE_ORDER));
        if (guardarCSV) {
            rescribirArchivo();
        }
    }

    //Rescribe el archivo actual con la lista existente
    public void rescribirArchivo() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo, false))) {

            bw.append("ID,Nombre,EPS,Fecha de Nacimiento");

            for (Paciente p : listaPacientes) {
                bw.newLine();
                bw.append(p.getId() + "," + p.getNombre() + "," + p.getEps() + "," + p.getFechaNacimiento());
            }

        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado, verifique nombre/existencia del archivo");
        } catch (IOException e) {
        }
    }
}