
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import main.ListaPacientes;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author elsanster
 */
public class MenuPrincipal {

    static String filename;
    static ListaPacientes listaDePacientes;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Sistema Hospitalario");
        
        //Loop para no salir hasta que se ingrese 7
        while (true) {
            //Si la lista de pacientes es nula, crearla
            if (listaDePacientes == null) {
                if (filename.isBlank()) {
                    System.out.print("Ingrese el archivo de la lista de pacientes (.csv) (creará uno nuevo si no existe):");
                    if (sc.hasNext()) {
                        filename = sc.nextLine();
                    }else{
                        System.err.println("Ingrese un nombre porfavor.");
                        continue;
                    }
                }

                //Abrimos el archivo temporalmente para verificar acceso
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
                    System.out.println("Acceso a archivo "+filename+" Exitoso");
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                listaDePacientes = new ListaPacientes(filename);
                listaDePacientes.rescribirArchivo(); //Agrega la cabecera de nombres de dato
            }
            //Ir al menú principal
            System.out.println("===Menú principal===\nIngrese el número para alguna de las siguientes opciones:\nNota: En todas las opciones se pregunta por ");
            System.out.println("""
                               1. Obtener lista de pacientes en la lista
                               2. Agregar paciente a la lista
                               3. Eliminar paciente de la lista (Por ID o Nombre)
                               4. Organizar lista por ID
                               5. Organizar lista por Nombre
                               6. Guardar Lista en el archivo
                               7. Guardar y Salir
                               """);
            
            int op = -1;
            try {
                op = sc.nextInt();
            }catch(InputMismatchException e ){
                System.err.println("Ingrese un número");
            }
            switch (op) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Ingrese una opción válida");
            }
        }
    }
}
