package main;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/*
 * @author El_Sanster & Natt
 */
// Inicializa un grafo de Pacientes y carga datos desde el csv
public class ListaPacientes {
    String nombreArchivo;
    // Hacemos el mapa que funcionara como nuestro Grafo 
    private Map<Integer, Paciente> grafoPacientes;
    // Integramos el grafo de las sedes de hospitales
    private Map<String, SedeHospital> grafoSedes;
    private RedHospitalaria redEps = new RedHospitalaria();
    //Constructor que pide el archivo, y genera la lista de pacientes en base del csv
    public ListaPacientes(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
        // Iniciamos el grafo vacio en memoria
        this.grafoPacientes = new HashMap<>();
        // Iniciamos el grafo de sedes vacio en la memoria
        this.grafoSedes = new HashMap<>();
        
        this.redEps.conectarSedes("Hospital Central", "Clinica Norte", 10);
        this.redEps.conectarSedes("Hospital Central", "Unidad medica sur", 15);
        this.redEps.conectarSedes("Clinica Norte", "Puesto de salud Oriente", 5);
        this.redEps.conectarSedes("Unidad medica sur", "Puesto de salud Oriente", 7);
        
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
                        // objeto paciente
                        Paciente p = new Paciente(nombre, eps, fechanacimiento, id);
                        insertarPacienteEnGrafo(p);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("error al cargar datos: " + e.getMessage());
            }
        }
    }
    public RedHospitalaria getRedEps(){
        return this.redEps;
    }
    // Metodo para insertar un paciente directamente en la estructura del grafo
    public final void insertarPacienteEnGrafo(Paciente nuevoPaciente){
        if (grafoPacientes.containsKey(nuevoPaciente.getId())){
            System.err.println("Ya existe un paciente con ID "+ nuevoPaciente.getId() + " .");
            return;
        }
            // si no existe, lo metemos al mapa
            grafoPacientes.put(nuevoPaciente.getId(), nuevoPaciente);
    }
    // Recorrido del Grafo ordenado por ID
    public void imprimirListaPacientes() {
        if (grafoPacientes.isEmpty()) {
            System.out.println("No hay pacientes registrados.");
            return;
        }
        // lo mismo q explique en 'Paciente.java' en el @override
        System.out.println("\n" + "=".repeat(60));
        System.out.printf("| %-5s | %-20s | %-10s | %-12s |\n",
                          "ID",  "NOMBRE", "EPS", "FECHA NACIMIENTO.");
        System.out.println("=".repeat(60));
        // como el mapa no tiene orden, sacamos las llaves (IDs)a una lista
        List<Integer> idsOrdenados = new ArrayList<>(grafoPacientes.keySet());
        // ordenamos la lista de IDs de menor a mayor
        Collections.sort(idsOrdenados);
        for (int id : idsOrdenados){
            System.out.print(grafoPacientes.get(id));
        }
        System.out.println("=".repeat(60));
    }
    //Añade un paciente a la lista, guardar csv
    public void agregarPaciente(Scanner sc) {
        try {
            System.out.println("--- agregar nuevo paciente ---");
            System.out.println("Ingrese el id: ");
            int id = Integer.parseInt(sc.nextLine());
            
            if (grafoPacientes.containsKey(id)){
                System.err.println("Error: Ya existe un paciente con ID "+id+".");
                return;
            }
            
            System.out.println("Ingrese nombre completo: ");
            String nombre = sc.nextLine();

            System.out.println("Ingrese la eps: ");
            String eps = sc.nextLine();

            System.out.println("Ingrese fecha de nacimiento (aaaa-mm-dd): ");
            String fechatxt = sc.nextLine();
            LocalDate fechaNac = LocalDate.parse(fechatxt);

            // agregamos al grafo usando el metodo de insertar...
            Paciente nuevo = new Paciente(nombre, eps, fechaNac, id);
            this.insertarPacienteEnGrafo(nuevo);
            rescribirArchivo();

            System.out.println("Paciente agregado a la lista correctamente.");
        } catch (NumberFormatException e) {
            System.out.println("error: el id debe ser un numero entero");
        } catch (java.time.format.DateTimeParseException e) {
            System.err.println("error: formato de fecha invalido (usa aaaa-mm-dd)");
        }
    }

    public void borrarPaciente(Scanner sc) {
        try {
            System.out.println("--- Eliminar paciente ---");
            System.out.println("Ingrese el id del paciente que desea eliminar");
            int idPaciente = Integer.parseInt(sc.nextLine());

            if (grafoPacientes.containsKey(idPaciente)){
                // eliminamos el vertice
                grafoPacientes.remove(idPaciente);
                System.out.println("Paciente eliminado correctamente");
                this.rescribirArchivo();
            } else {
                System.out.println("No se encontro el paciente con ID: "+ idPaciente);
            }
            
        } catch (NumberFormatException e) {
            System.err.println("error: El id debe ser un numero entero.");
        }
    }
    
    //Organizar lista por ID, si guardar con este orden al csv
    public void organizarListaPorID(boolean guardarCSV) {
        if (grafoPacientes.isEmpty()) {
            System.out.println("No hay pacientes registrados.");
            return;
        }
        imprimirListaPacientes();
        if (guardarCSV){
            this.rescribirArchivo();
        System.out.println("Lista organizada por id exitosamente.");
        }
    }

    //Organizar lista por Nombre, guardar con este orden al csv
    public void organizarListaPorNombre(boolean guardarCSV) {
        if (grafoPacientes.isEmpty()) {
            System.out.println("la lista esta vacia.");
            return;    
        }
        // creamos una lista temporal para guardar los pacientes en el grafo
        List<Paciente> listaTemporal = new ArrayList<>(grafoPacientes.values());
        
        // ordenamos de A-Z por el nombre
        listaTemporal.sort((p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()));
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Lista organizada por nombre (alfabeticamente)");
        System.out.println("=".repeat(60));
        System.out.printf("| %-5s | %-20s | %-10s | %-15s |\n",
                          "ID",  "NOMBRE", "EPS", "FECHA NACIMIENTO.");
        System.out.println("=".repeat(60));
        for (Paciente p : listaTemporal){
            System.out.print(p);
        }
        // confirmacion de que se sobrescriba el archivo
        if (guardarCSV){
            escribirListaEnArchivo(listaTemporal);
            System.out.println("=".repeat(60));
            System.out.println("Archivo CSV actualizado por orden alfabetico");
        }
    }
    // Escribe la lista temporal en el CSV
    private void escribirListaEnArchivo(List<Paciente> lista){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
            bw.write("ID,Nombre,EPS,Fecha de nacimiento");
            for (Paciente p : lista){
                bw.newLine();
                bw.write(p.getId() + "," + p.getNombre() + "," + p.getEps() + "," + p.getFechaNacimiento());
            }
        } catch (IOException e){
            System.err.println("Error al guardar " + e.getMessage());
        }
    }
    
    //Rescribe el archivo actual con la lista existente
    public void rescribirArchivo(){
        if (grafoPacientes.isEmpty()) return;
        // ordemnamos los IDs para guardarlos organizados
        List<Integer> idsOrdenados = new ArrayList<>(grafoPacientes.keySet());
        Collections.sort(idsOrdenados);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))){
            bw.write("ID,Nombre,EPS,Fecha de nacimiento");
            for (int id : idsOrdenados){
                Paciente p = grafoPacientes.get(id);
                bw.newLine();
                bw.write(p.getId()+","+p.getNombre()+","+p.getEps()+","+p.getFechaNacimiento());
            }
            bw.flush();
            System.out.println("Archivo actualizado correctamente");
        } catch (IOException e){
            System.out.println("Error al escribir en el archivo" + e.getMessage());
        }
    }
    
    // Funciones dedicadas para la interfaz gráfica
    
    // Listar el grafo a mostrar en la interfaz, organizada por ID    
    // Este es una copia de imprimirListaPacientes()
    public List<Paciente> listaPacientesID(){
        List<Integer> idsOrdenados = new ArrayList<>(grafoPacientes.keySet());
        Collections.sort(idsOrdenados);
        
        List <Paciente> listaPacientes = new ArrayList<>();
        for (Integer idPaciente : idsOrdenados) {
            listaPacientes.add(grafoPacientes.get(idPaciente));
        }
        
        return listaPacientes;
    }
    
    // Listar el grafo a mostrar en la interfaz, organizada por nombre
    // este es una copia de organizarListaNombre()
    public List<Paciente> listaPacientesNombre(){
        // creamos una lista temporal para guardar los pacientes en el grafo
        List<Paciente> listaTemporal = new ArrayList<>(grafoPacientes.values());
        
        // ordenamos de A-Z por el nombre
        listaTemporal.sort((p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()));    
        return listaTemporal;
    }
    
    public boolean borrarPaciente (int idPaciente){
        if (grafoPacientes.containsKey(idPaciente)){
                // eliminamos el vertice
                grafoPacientes.remove(idPaciente);
                return true;
        }else {
            return false;
        }
    }
    
    // Era bait, nunca hubo una funcion para verificar ids
    
    public static void main(String[] args){
        //Nombre del archivo CSV donde se guardaran los datos
        String rutaArchivo = "pacientes.csv";
        
        ListaPacientes sistema = new ListaPacientes(rutaArchivo);
        Scanner sc = new Scanner(System.in);
        int opcion = 0;
        
        System.out.println("--- Sistema gestion Hospitalaria ---");
        
        do {
            System.out.println("\n--- Menu Principal ---");
            System.out.println("1. Imprimir lista de pacientes (Ordenados por ID)");
            System.out.println("2. Agregar nuevo paciente");
            System.out.println("3. Eliminar paciente por ID");
            System.out.println("4. Organizar lista por nombre (Alfabetico)");
            System.out.println("5. Calcular ruta mas corta entre sedes");
            System.out.println("6. Salir");
            System.out.println("Seleccione una opcion: ");
            
            try {
                opcion = Integer.parseInt(sc.nextLine());
                
                switch (opcion){
                    case 1 -> sistema.imprimirListaPacientes();
                    case 2 -> sistema.agregarPaciente(sc);
                    case 3 -> sistema.borrarPaciente(sc);
                    case 4 -> sistema.organizarListaPorNombre(true);
                    case 5 -> {
                        System.out.println("--- Calculo de ruta ---");
                        System.out.println("Ingrese la sede de Origen (EJ: Hospital Central): ");
                        String origen = sc.nextLine();
                        System.out.println("Ingrese la sede de Destino (EJ: Puesto de salud Oriente): ");
                        String destino = sc.nextLine();
                        
                        sistema.getRedEps().calcularRutaMasCorta(origen, destino);
                    }
                    case 6 -> System.out.println("Saliendo del sistema ....");
                    default -> System.out.println("Opcion no valida, intente denuevo");
                }
            } catch (NumberFormatException e){
                System.out.println("Error: Porfavor ingrese un numero valido");
            }
    } while (opcion !=6);
    sc.close();
    }
}
