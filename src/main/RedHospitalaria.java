package main;
import java.util.*;
/**
 *
 * @author Natt
 */
public class RedHospitalaria {
    // lista de adyacencia principal: Nombre y objeto sede
    private Map<String,SedeHospital> mapaSedes;
    
    public RedHospitalaria(){
        this.mapaSedes = new HashMap<>();
    }
    public void registrarSede(String nombre){
        mapaSedes.putIfAbsent(nombre, new SedeHospital(nombre));
    }
    //conectar dos sedes con una distancia bidireccional
    public void conectarSedes(String origen, String destino, int distancia){
        registrarSede(origen);
        registrarSede(destino);
        
        SedeHospital sOrigen = mapaSedes.get(origen);
        SedeHospital sDestino = mapaSedes.get(destino);
        
        sOrigen.conectarCon(sDestino, distancia);
        sDestino.conectarCon(sOrigen, distancia);
    }
    // Algoritmo de Dijkstra para calcular la ruta con menor distancia
    // Que es el algoritmo de Dijkstra? es un método de búsqueda eficiente utilizado para encontrar 
    // la ruta más corta desde un nodo (o vértice) de origen hacia todos los demás nodos en un grafo ponderado
    public void calcularRutaMasCorta(String nombreOrigen, String nombreDestino){
        SedeHospital origen = mapaSedes.get(nombreOrigen);
        SedeHospital destino = mapaSedes.get(nombreDestino);
        if (origen == null || destino == null){
            System.out.println("Error: una o ninguna de las sedes exiten en la red");
            return;
        }
        // estructura de contlor del algoritmo
        Map<SedeHospital, Integer> distancias = new HashMap<>();
        Map<SedeHospital, SedeHospital> predecesores = new HashMap<>();
        // Fila de prioridad, siempre evalua primero la sede mas cercana
        PriorityQueue<SedeHospital> filaPrioridad = new PriorityQueue<>(
            Comparator.comparingInt(s -> distancias.getOrDefault(s, Integer.MAX_VALUE))
        );
        // iniciamos todas las distancias en infinito
        for (SedeHospital s : mapaSedes.values()){
            distancias.put(s, Integer.MAX_VALUE);
        }
        distancias.put(origen, 0);
        filaPrioridad.add(origen);
        
        while (!filaPrioridad.isEmpty()){
            SedeHospital actual = filaPrioridad.poll();
            // si llegamos al destion, terminamos la busqueda
            if (actual.equals(destino)) break;
            //
            for (Map.Entry<SedeHospital, Integer> conexion : actual.getConexiones().entrySet()){
                SedeHospital vecino = conexion.getKey();
                int pesoArista = conexion.getValue();
                
                int nuevaDistancia = distancias.get(actual) + pesoArista;
                
                // si encuentra un camino mas corto al vecino, actualiza los datos
                if (nuevaDistancia < distancias.get(vecino)){
                    distancias.put(vecino, nuevaDistancia);
                    predecesores.put(vecino, actual);
                    // Actuaalizamos la posicion del vecino en la lista de prioridad
                    filaPrioridad.remove(vecino);
                    filaPrioridad.add(vecino);
                    
                }
            }
        }
        //
        if (distancias.get(destino) == Integer.MAX_VALUE){
            System.out.println("No existe una ruta fisica de conexion entre "+ nombreOrigen + " Y "+ nombreDestino);
        } else {
            System.out.println("\n"+"=".repeat(60));
            System.out.println("---Ruta mas optimizada encontrada---");
            System.out.println("=".repeat(60));
            System.out.println("Distancia total minima: "+distancias.get(destino)+"km");
            System.out.print("Trazado de la ruta: ");
            mostrarCaminoRec(destino, predecesores);
            System.out.println("\n"+"=".repeat(60));
        }
    }
    
    private void mostrarCaminoRec(SedeHospital nodo, Map<SedeHospital, SedeHospital> predecesores){
        if (nodo == null) return;
        mostrarCaminoRec(predecesores.get(nodo), predecesores);
        System.out.print(nodo.getNombre() + (predecesores.containsKey(nodo) ? "" : " ->"));
    }
}
