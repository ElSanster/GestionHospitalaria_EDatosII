package main;
import java.util.*;
/**
 *
 * @author Natt
 */
public class SedeHospital {
    private String nombre;
    // guarda la sede vecina y su distancia (peso de la arista)
    private Map<SedeHospital, Integer> conexiones;
    
    public SedeHospital(String nombre){
        this.nombre = nombre;
        this.conexiones = new HashMap<>();
    }
    // metodo para conectar esta sede con otra (grafo ponderado)
    public void conectarCon(SedeHospital destino, int distancia){
        this.conexiones.put(destino, distancia);
    }
    public String getNombre(){
        return nombre;
    }
    public Map<SedeHospital, Integer> getConexiones(){
        return conexiones;
    }
    @Override
    public String toString(){
        return nombre;
    }
}
