import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import main.ListaPacientes;
/**
 *
 * @author elsanster & Natt
 */
public class MenuPrincipal {

    static String filename = "";
    static ListaPacientes listaDePacientes;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Sistema Hospitalario");
        
        
        //Loop para no salir hasta que se ingrese 7
        while (true) {
            //Si la lista de pacientes es nula, crearla
            if (listaDePacientes == null) {
                if (filename.isBlank()) {
                    System.out.print("Ingrese el archivo de la lista de pacientes (.csv) (creara uno nuevo si no existe):");
                    filename = sc.nextLine();
                    
                    // si se deja vacio el campo
                    if (filename.isBlank()) {
                    System.err.println("error: el nombre del archivo no puede estar vacio.");
                    continue;
                    }
                    
                    // Asegurar que tenga la extension .csv
                    if (!filename.toLowerCase().endsWith(".csv")) {
                        filename += ".csv";
                }

                //Creamos el archivo .csv si no existe 
                File archivo = new File(filename);
                if (!archivo.exists()) {
                    System.out.println("El archivo '" + filename + "' no existe. Creando uno nuevo...");
                    try {
                        if (archivo.createNewFile()) {
                            System.out.println("Archivo creado exitosamente.");
                        }
                    } catch (IOException e) {
                        System.err.println("No se pudo crear el archivo: " + e.getMessage());
                        filename = "";
                        continue;
                    }
                } else {
                    System.out.println("Archivo '" + filename + "' encontrado. Cargando datos...");
                }
                listaDePacientes = new ListaPacientes(filename);
                listaDePacientes.rescribirArchivo();
            }
            //Ir al menú principal
            System.out.println("===Menu principal===\nIngrese el numero para alguna de las siguientes opciones:\nNota: En todas las opciones se pregunta por ");
            System.out.println("""
                               1. Obtener lista de pacientes en la lista
                               2. Agregar paciente a la lista
                               3. Eliminar paciente de la lista (Por ID o Nombre)
                               4. Organizar lista por ID
                               5. Organizar lista por Nombre
                               6. Guardar y Salir
                               """);
            
            int op = -1;
            try {
                //usamos nextline y parseint para evitar saltar el menu
                op = Integer.parseInt(sc.nextLine());
            } catch(InputMismatchException e ){
                System.err.println("error: Ingrese un numero valido");
                continue; // sirve para mostrar el menu inmediatamente despues de darle a alguna opcion
            }
            switch (op) {
                case 1 -> listaDePacientes.imprimirListaPacientes();
                case 2 -> listaDePacientes.agregarPaciente(sc);
                case 3 -> listaDePacientes.borrarPaciente(sc);
                case 4 -> listaDePacientes.organizarListaPorID(true);
                case 5 -> listaDePacientes.organizarListaPorNombre(true);
                case 6 -> {
                    return;
                    }
                default -> System.out.println("Ingrese una opcion valida");
                }
            }
        }
    }
}

