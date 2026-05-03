package main;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/*
 * @author El_Sanster & Natt
 */
//Inicializa una lista de clase Paciente y carga datos desde * el csv
public class ListaPacientes {
    String nombreArchivo;
    Paciente pacienteRaiz;
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
                        insertarPaciente(nombre, eps, fechanacimiento, id);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("error al cargar datos: " + e.getMessage());
            }
        }
    }
    // Recorrido InOrden (pacientes ordenados por ID)
    public void imprimirListaPacientes() {
        if (pacienteRaiz == null) {
            System.out.println("No hay pacientes registrados.");
            return;
        }
        // lo mismo q explique en 'Paciente.java' en el @override
        System.out.println("\n" + "=".repeat(60));
        System.out.printf("| %-5s | %-20s | %-10s | %-12s |\n",
                          "ID",  "NOMBRE", "EPS", "FECHA NACIMIENTO.");
        System.out.println("=".repeat(60));
        inOrdenRecPrint(pacienteRaiz);
        System.out.println("=".repeat(60));
    }
    private void inOrdenRecPrint(Paciente paciente) {
        if (paciente != null) {
            inOrdenRecPrint(paciente.nodoDer);
            System.out.print(paciente);
            inOrdenRecPrint(paciente.nodoIzq);
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

            this.insertarPaciente(nombre, eps, fechaNac, id);
            rescribirArchivo();

            System.out.println("Paciente agregado a la lista correctamente.");
        } catch (NumberFormatException e) {
            System.out.println("error: el id debe ser un numero entero");
        } catch (java.time.format.DateTimeParseException e) {
            System.err.println("error: formato de fecha invalido (usa aaaa-mm-dd)");
        }
    }

    public void insertarPaciente(String nombre, String eps, LocalDate fechaNacimiento, int id) {
        pacienteRaiz = insertarRec(pacienteRaiz, nombre, eps, fechaNacimiento, id);
    }

    public void insertarPaciente(Paciente paciente) {
        pacienteRaiz = insertarRec(pacienteRaiz, paciente.getNombre(), paciente.getEps(), paciente.getFechaNacimiento(), paciente.getId());
    }

    private Paciente insertarRec(Paciente raiz, String nombre, String eps, LocalDate fechaNacimiento, int id) {
        // Corregido error 'Paciente registrado correctamente si existe el archivo pero no existe ningun paciente
        if (raiz == null) {
            return new Paciente(nombre, eps, fechaNacimiento, id);
        }
        if (id < raiz.getId()) {
            raiz.nodoIzq = insertarRec(raiz.nodoIzq, nombre, eps, fechaNacimiento, id);
        } else if (id > raiz.getId()) {
            raiz.nodoDer = insertarRec(raiz.nodoDer, nombre, eps, fechaNacimiento, id);
        } else {
            System.err.println("Ya existe un paciente con ID " + id + " .");
        }
        return raiz;
    }

    public void borrarPaciente(Scanner sc) {
        try {
            System.out.println("--- Eliminar paciente ---");
            System.out.println("Ingrese el id del paciente que desea eliminar");

            int idPaciente = Integer.parseInt(sc.nextLine());

            pacienteRaiz = this.eliminar(pacienteRaiz, idPaciente);
            
        } catch (NumberFormatException e) {
            System.err.println("error: El id debe ser un numero entero.");
        }
    }
    // corregido error de Stackoverflow al intentar eliminar paciente
    private Paciente eliminar(Paciente raiz, int id) {
        if (raiz == null) {
            System.out.println("No se encontro el paciente con ID: " + id);
            return null;
        }
        if (id < raiz.getId()) {
            raiz.nodoIzq = eliminar(raiz.nodoIzq, id); // ID MENOR IZQ
        } else if (id > raiz.getId()){
            raiz.nodoDer = eliminar(raiz.nodoDer, id); // ID MAYOR DER
        }
        else {
            if (raiz.nodoIzq == null) return raiz.nodoDer;
            if (raiz.nodoDer == null) return raiz.nodoIzq;
        // Si el nodo tiene dos hijos
        // buscamos al sucesor mas pequeño del arbol derecho
        Paciente sucesor = buscarMinimo(raiz.nodoDer);
        // copiamos los datos del sucesor al nodo actual
        raiz.setAllData(sucesor.getNombre(), sucesor.getEps(), sucesor.getFechaNacimiento(), sucesor.getId());
        // eliminamos al sucesor original del subarbolderecho
        raiz.nodoDer = eliminar(raiz.nodoDer, sucesor.getId());
        
        System.out.println("Paciente eliminado correctamente");
        this.rescribirArchivo();
        }
        return raiz;
    }
    
    // metodo para encontrar el reemplazo
    private Paciente buscarMinimo(Paciente nodo){
        while (nodo.nodoIzq !=null){
            nodo = nodo.nodoIzq;
        }
        return nodo;
    }
   

    //Organizar lista por ID, si guardar con este orden al csv
    public void organizarListaPorID(boolean guardarCSV) {
        if (pacienteRaiz == null) {
            System.out.println("No hay pacientes registrados.");
            return;
        }
        inOrdenRecPrint(pacienteRaiz);
        this.rescribirArchivo();
        System.out.println("Lista organizada por id exitosamente.");
    }

    //Organizar lista por Nombre, guardar con este orden al csv
    // la idea es usar un arraylist para tomar todos los pacientes del arbol
    // y oredenamos la lista con un comparator para luego actualizar el csv
    public void organizarListaPorNombre(boolean guardarCSV) {
        if (pacienteRaiz == null) {
        System.out.println("la lista esta vacia.");
        return;    
        }
        // creamos una lista temporal para guardar los pacientes en el arbol
        List<Paciente> listaTemporal = new ArrayList<>();
        llenarListaDesdeArbol(pacienteRaiz, listaTemporal);
        
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
    
    private void llenarListaDesdeArbol(Paciente nodo, List<Paciente> lista) {
        if (nodo !=null){
            llenarListaDesdeArbol(nodo.nodoIzq, lista);
            lista.add(nodo);
            llenarListaDesdeArbol(nodo.nodoDer, lista);
        }
    }
    
    // VAMOS A OPTIMIZARLO CHICOS UWU 7W7 XDDDDD -natt
    //Rescribe el archivo actual con la lista existente
    public void rescribirArchivo(){
        if (pacienteRaiz == null) return;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))){
            bw.write("ID,Nombre,EPS,Fecha de nacimiento");
            // llamamos a la montapuercas (recursion) pasando el bufferdwriter que ya estaba abierto
            inOrdenRecEscribirArchivo(pacienteRaiz, bw);
            System.out.println("Archivo actualizado correctamente");
        } catch (IOException e){
            System.out.println("Error al escribir en el archivo" + e.getMessage());
        }
    }
    
    public void inOrdenRecEscribirArchivo (Paciente nodo, BufferedWriter bw) throws IOException{
        if (nodo != null){
            inOrdenRecEscribirArchivo(nodo.nodoIzq, bw);
            bw.newLine();
            bw.write(nodo.getId() + "," + nodo.getNombre() + "," + nodo.getEps() + "," + nodo.getFechaNacimiento());
            inOrdenRecEscribirArchivo(nodo.nodoDer, bw);
        }
    }
    
    
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
            System.out.println("5. Salir");
            System.out.println("Seleccione una opcion: ");
            
            try {
                opcion = Integer.parseInt(sc.nextLine());
                
                switch (opcion){
                    case 1 -> sistema.imprimirListaPacientes();
                    case 2 -> sistema.agregarPaciente(sc);
                    case 3 -> sistema.borrarPaciente(sc);
                    case 4 -> sistema.organizarListaPorNombre(true);
                    case 5 -> System.out.println("Saliendo del sistema ....");
                    default -> System.out.println("Opcion no valida, intente denuevo");
                }
            } catch (NumberFormatException e){
                System.out.println("Error: Porfavor ingrese un numero valido");
            }
    } while (opcion !=5);
    sc.close();
    }
}
