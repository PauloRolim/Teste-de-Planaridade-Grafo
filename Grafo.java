import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe responsável por representar um grafo.
 * Também implementa algumas funções para manipular o grafo.
 * 
 * @author Paulo Rolim
 */

 public class Grafo<T> {
 
    /*Mapeamento dos vertices para um conjunto contendo todos os vértices adjacentes*/
    HashMap<T, Set<T>> adjacencyMap = new HashMap<T, Set<T>>();

    //Construtor da classe
    public Grafo(){}

    /*Constrói um grafo idêntico o grafo dado como entrada.
     *O parametro source é o grafo de entrada.
    */
    public Grafo(Grafo<T> source) {
        for (T v : source.getVertices()) {
            for (T u : source.getNeighbors(v)) {
                this.addEdge(v,u);
            }
        }
    }

    /*Adiciona um vértice ao grafo
     *O parametro v é o vértice a ser inserido
    */
    public void addVertex(T v) {
        if (!adjacencyMap.containsKey(v)) {
            adjacencyMap.put(v, new HashSet<T>());
        }
    }

    /*Adiciona uma aresta não direcionada entre dois vértices
     *O parametro v1 é o primeiro vértice da aresta
     *O parmetro v2 é o segundo vértice da aresta
    */
    public void addEdge(T v1, T v2) {
        addVertex(v1);
        addVertex(v2);
        adjacencyMap.get(v1).add(v2);
        adjacencyMap.get(v2).add(v1);
    }

    /*Remove uma aresta entre dois vértices
     * 
     *O parametro v1 é o primeiro vértice da aresta
     *O parmetro v2 é o segundo vértice da aresta
    */
    public void removeEdge(T v1, T v2) {
        if (hasEdge(v1,v2) && hasEdge(v2,v1)) {
            adjacencyMap.get(v1).remove(v2);
            adjacencyMap.get(v2).remove(v1);
            if (adjacencyMap.get(v1).size() == 0) adjacencyMap.remove(v1);
            if (adjacencyMap.get(v2).size() == 0) adjacencyMap.remove(v2);
        }
    }

    /**
    * Função que retorna o número de vértices do grafo
    * 
    */
    public int numVertices() {
        return adjacencyMap.size();
    }

    /*Função que retorna o número de arestas do grafo
     * 
     */
    public int numEdges() {
        int count = 0;
        for (Set<T> edges : adjacencyMap.values()) {
            count += edges.size();
        }
        return count/2;
    }

    /**
     * Função que retorna todos os vértices vizinhos ao vértice fornecido
     * Onde o praâmetro v é o vértice cujos vizinhos serão retornados
    */
    public Set<T> getNeighbors(T v) {
        return adjacencyMap.get(v);
    }

    /**
     * Função que retorna o grau de um vértice fornecido
     *Onde o parametro v é o vértice que terá seu grau como retorno da função 
     *Quando a função retorna -1 significa que o vértice informado não existe no grafo
     */
    public int getDegree(T v) {
        if ( adjacencyMap.containsKey( v ) ) {
            return adjacencyMap.get( v ).size();
        } else {
            return -1;
        }
    }

    /*
     * Função que retorna o conjunto de todos os vértices contidos no grafo.
     */
    public Set<T> getVertices() {
        return adjacencyMap.keySet();
    }

    /**
     * Função booleana que verifica se o vértice informado existe no grafo.
     *O parametro v é o vértice que será verificado.
     *Retorna true se encontrar o vértice fornecido, e false caso não encontre.
     */
    public boolean hasVertex(T v) {
        return adjacencyMap.containsKey( v );
    }

    /**
     *Função que verifica se a aresta fornecida está presente no grafo
     *O parametro v1 é o primeiro vértice da aresta
     *O parmetro v2 é o segundo vértice da aresta
     */
    public boolean hasEdge(T v1, T v2) {
        return adjacencyMap.containsKey( v1 ) && adjacencyMap.get( v1 ).contains( v2 );
    }

    /** Função quer exibe na saida pradrão o grafo fornecido */
    public void print() {
        System.out.println(  );
        for ( T k : adjacencyMap.keySet() ) {
            System.out.print( k + ":" );
            for ( T v : adjacencyMap.get(k) ) {
                System.out.print( " " + v );
            }
            System.out.println();
        }
    }

    /*Função utilizada para testar se o grafo conexo possui um ciclo.
     *O parametro grafo é um grafo fornecido como entrada
     *O retorno é uma operação lógica entre o número de vértices do grafo 
     *e o grau de cada vértice. Retorna true e o grafo possuir mais de dois
     *vértices e se o grau de cada vértice for igual a 2. 
     */
    public static <T> boolean isCycle(Grafo<T> grafo) {
        boolean isCycle = grafo.numVertices() > 2;
        for (T v : grafo.getVertices()) {
            isCycle = isCycle && grafo.getDegree(v) == 2;
        }
        return isCycle;
    }

    /*Função utilizada para testes se o grafo possui um caminho.
     *O parametro grafo é um grafo fornecido como entrada
     *retorna true se existir um caminho no grafo, retorna false
     *se não existir nenhum caminho.
     */
    public static <T> boolean isPath( Grafo<T> grafo ) {
        int endPoints = 0;
        for ( T v : grafo.getVertices() ) {
            int degree = grafo.getDegree( v );
            if ( degree == 1 ) {
                endPoints++;
            } else if ( degree != 2 ) {
                return false;
            }
        }
        if ( endPoints != 2 ) return false;
        return true;        
    }

    /**
     * Função que testa se o grafo é bipartido
     * 
     * O parametro é o proprio grafo, e retorna true se o grafo 
     * for bipartido
     *
     */
    public static <T> boolean eBipartido( Grafo<T> grafo ) {
        return (new GraphTraverser<T>( grafo )).eBipartido();
    }

    /**
     * Divide o grafo em duas partes usando um ciclo.
     *
     * O parametro grafo é a propria entrada, e
     * o parametro ciclo recebe uma das partes do grafo
     * Retorna o conjunto contendo duas partes do grafo.
     */
    public static <T> Set<Grafo<T>> splitIntoPieces( Grafo<T> grafo, Grafo<T> ciclo ) {
        return (new GraphTraverser<T>(grafo)).splitIntoPieces(ciclo);
    }

    /**
     * Função que soma dois grafos distintos produzindo um novo grafo
     * com cada vértice e aresta dos dois grafos originais.
     *
     * O parametro g1 é o primeiro grafo. O parametro g2 é o segundo grafo.
     * Retorna um novo grafo com todos dos vértices e arestas de g1 e g2.
     */
    public static <T> Grafo<T> addGraphs( Grafo<T> g1, Grafo<T> g2 ) {
        Grafo<T> novoGrafo = new Grafo<T>();
        for (T v : g1.getVertices()) {
            for (T u : g1.getNeighbors(v)) {
                novoGrafo.addEdge(v,u);
            }
        }
        for (T v : g2.getVertices()) {
            for (T u : g2.getNeighbors(v)) {
                novoGrafo.addEdge(v,u);
            }
        }
        return novoGrafo;
    }

 }