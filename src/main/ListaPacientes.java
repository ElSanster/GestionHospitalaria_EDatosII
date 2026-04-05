/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author El_Sanster 
 * Inicializa una lista de clase Paciente y carga datos desde
 * el csv
 */
public class ListaPacientes {

    List<Paciente> listaPacientes = new ArrayList<>();
    String nombreArchivo;
    
    
    //Constructor que pide el archivo, y genera la lista de pacientes en base del csv
    public ListaPacientes(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
        try (BufferedReader br = (new BufferedReader(new FileReader(nombreArchivo)))) {

            String linea;
            boolean esPrimeraLinea = true;

            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(",");

                //La idea es que la primera linea contenga los nombres de cada dato
                //El csv debería tener este orden: id, nombre, eps, fecha nacimiento
                if (esPrimeraLinea) {
                    esPrimeraLinea = false;
                    continue;
                }

                int id = Integer.parseInt(campos[0]);
                String nombre = campos[1], eps = campos[2];
                LocalDate fechaNacimiento = LocalDate.parse(campos[3]);

                Paciente p = new Paciente(nombre, eps, fechaNacimiento, id);
                listaPacientes.add(p);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado, verifique nombre/existencia del archivo");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Util para mostrar la lista de pacientes
    public List<Paciente> getListaPacientes() {
        return listaPacientes;
    }
    
    public void imprimirListaPacientes(){
        System.out.println("ID | Nombre | EPS | Fecha de Nacimiento");
        for (Paciente paciente : listaPacientes) {
            System.out.println(paciente.toString());
        }
    }

    //Añade un paciente a la lista, guardar csv
    public void addPaciente(Paciente p, boolean guardarCSV) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo, true))) {
            listaPacientes.add(p);
            bw.newLine();
            bw.append(p.getId() + "," + p.getNombre() + "," + p.getEps() + "," + p.getFechaNacimiento());
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado, verifique nombre/existencia del archivo");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(guardarCSV) rescribirArchivo();
    }

    public void borrarPaciente(int idPaciente, boolean guardarCSV) {
        boolean eliminado = false;
        for (Paciente e : listaPacientes) {
            if (e.getId() == idPaciente) {
                eliminado = true;
                listaPacientes.remove(e);
            }
        }
        if(eliminado) System.out.println("Paciente encontrado e eliminado de la lista.");
        else System.out.println("Ningún paciente concuerda con el ID dado");
        if (guardarCSV && eliminado) {
            rescribirArchivo();
        }
        
    }

    //Organizar lista por ID, si guardar con este orden al csv
    public void organizarListaPorID(boolean guardarCSV) {
        listaPacientes.sort((o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
        if (guardarCSV) {
            rescribirArchivo();
        }
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
            e.printStackTrace();
        }
    }

}
