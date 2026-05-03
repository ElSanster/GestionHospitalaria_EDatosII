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
        System.out.print(pacienteRaiz);
        System.out.println("  " + "-".repeat(55));
        inOrdenRecPrint(pacienteRaiz);
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

            System.out.println("Paciente agregado a la lista exitosamente.");
        } catch (NumberFormatException e) {
            System.out.println("error: el id debe sesr un numero entero");
        } catch (java.time.format.DateTimeParseException e) {
            System.err.println("error: formato de fecha invalido (usa aaaa-mm-dd");
        }
    }

    public void insertarPaciente(String nombre, String eps, LocalDate fechaNacimiento, int id) {
        pacienteRaiz = insertarRec(pacienteRaiz, nombre, eps, fechaNacimiento, id);
    }

    public void insertarPaciente(Paciente paciente) {
        pacienteRaiz = insertarRec(pacienteRaiz, paciente.getNombre(), paciente.getEps(), paciente.getFechaNacimiento(), paciente.getId());
    }

    private Paciente insertarRec(Paciente raiz, String nombre, String eps, LocalDate fechaNacimiento, int id) {
        if (raiz == null) {
            System.out.println("Paciente registrado correctamente.");
            return new Paciente(nombre, eps, fechaNacimiento, id);
        }
        if (id < raiz.getId()) {
            raiz.nodoIzq = insertarRec(raiz.nodoIzq, nombre, eps, fechaNacimiento, id);
        } else if (id > raiz.getId()) {
            raiz.nodoDer = insertarRec(raiz.nodoDer, nombre, eps, fechaNacimiento, id);
        } else {
            System.err.println("Ya existe un paciente con ID " + id + ".");
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

    private Paciente eliminar(Paciente pacienteRaiz, int id) {
        if (pacienteRaiz == null) {
            return pacienteRaiz;
        }
        if (id < pacienteRaiz.getId()) {
            pacienteRaiz.nodoDer = this.eliminar(pacienteRaiz.nodoDer, id);
        } else {
            if (pacienteRaiz.nodoIzq == null && pacienteRaiz.nodoDer == null) {
                pacienteRaiz = null;
                System.out.println("Paciente con el id '" + id + "' eliminado exitosamente");
                this.rescribirArchivo();
                System.out.println("Archivo actualizado correctamente.");
            } else if (pacienteRaiz.nodoDer != null) {
                Paciente sucesor = this.sucesor(pacienteRaiz);
                pacienteRaiz.setAllData(sucesor.getNombre(), sucesor.getEps(), sucesor.getFechaNacimiento(), sucesor.getId());
                pacienteRaiz.nodoIzq = this.eliminar(pacienteRaiz, id);
            } else {
                Paciente predecesor = this.predecesor(pacienteRaiz);
                pacienteRaiz.setAllData(predecesor.getNombre(), predecesor.getEps(), predecesor.getFechaNacimiento(), predecesor.getId());
            }
        }
        return pacienteRaiz;
    }

    private Paciente sucesor(Paciente paciente) {
        paciente = paciente.nodoDer;
        while (paciente.nodoIzq != null) {
            paciente = paciente.nodoIzq;
        }
        return paciente;
    }

    private Paciente predecesor(Paciente paciente) {
        paciente = paciente.nodoIzq;
        while (paciente.nodoDer != null) {
            paciente = paciente.nodoDer;
        }
        return paciente;
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
    public void organizarListaPorNombre(boolean guardarCSV) {
//TO DO: Hacer esto, no se como hacerlo así que lo dejare para Nattsuki o para mañana

//        if (listaPacientes.isEmpty()) {
//            System.out.println("la lista esta vacia.");
//            return;
//        }
//        // Orden alfabetico de A a la Z
//        listaPacientes.sort((p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()));
//        this.rescribirArchivo();
//        System.out.println("Lista organizada por nombre alfabeticamente.");
    }

    //Rescribe el archivo actual con la lista existente
    public void rescribirArchivo() {
        File a = new File(nombreArchivo);
        a.delete();
        inOrdenRecEscribirArchivo(pacienteRaiz);
    }
    
    public void inOrdenRecEscribirArchivo (Paciente raiz){
        //Un gastadero de memoria y uso de disco creo pero ps no se que otra manera se puede
        if (raiz != null){
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo, true))) {
            bw.append("ID,Nombre,EPS,Fecha de Nacimiento");
            if (raiz == null) {
                return;
            }
            inOrdenRecEscribirArchivo(raiz.nodoIzq);
            bw.newLine();
            bw.append(raiz.getId() + "," + raiz.getNombre() + "," + raiz.getEps() + "," + raiz.getFechaNacimiento());
            inOrdenRecEscribirArchivo(raiz.nodoDer);
            
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado, verifique nombre/existencia del archivo");
        } catch (IOException e) {
        }
        }
    }

}
