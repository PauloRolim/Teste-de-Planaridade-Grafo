import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class GraphTraverser<T> {
    
    private Grafo<T> grafo;

    private Set<T> searched = new HashSet<T>();

    /** Um map usado para colorir o grafo */
    private Map<T, Integer> coloring = null;

    /** Variável que guarda o grafo resultante de uma operação */
    private Grafo<T> result = null;

    /** Vértice alvo. */
    private T goal = null;

    /** O próximo vértice a ser percorrido (usado para percorrer um ciclo). */
    private T next = null;

    /** O vértice anterior a ser perccorido (usado para percorrer um ciclo). */
    private T prev = null;

    /**
     * Construtor para a classe GrafoTraverser.
     * 
     */
    public GraphTraverser( Grafo<T> grafo ) {
        this.grafo = grafo;
    }

    /**
     * Função que testa se o grafo é bipartido.
     *
     */
    public boolean eBipartido() {
        if (grafo.numVertices() == 0) return true;
        coloring = new HashMap<T, Integer>();
        return eBipartido(grafo.getVertices().iterator().next(), true);
    } 

    /**
     *Função que testa se o grafo é bipartido
     * O parametro é o proprio grafo, e retorna true se o grafo 
     * for bipartido.
     * Utiliza a propriedade de que um grafo bipartido pode ser
     * colorido com apenas duas cores.
     */
    private boolean eBipartido(T v, boolean color) {
        if (coloring.containsKey(v)) {
            if (!coloring.get(v).equals(color? 1 : 0)) {
                return false;
            } else {
                return true;
            }
        } else {
            coloring.put(v, color? 1 : 0);
            boolean bipartite = true;
            for (T n : grafo.getNeighbors(v)) {
                bipartite = bipartite && eBipartido(n, !color);
            }
            return bipartite;
        }
    }

    /**
     * Função que percorre um ciclo, inicia num vértice aleatório
     * e percorre o ciclo sempre pelos vértices adjacentes ao
     * vertice escolhido.
     *
     */
    public T walkCycle() {
        if ( next == null ) {
            prev = grafo.getVertices().iterator().next();
            next = grafo.getNeighbors(prev).iterator().next();
        } else {
            for (T n : grafo.getNeighbors(next)){
                if (!n.equals(prev)) {
                    prev = next;
                    next = n;
                    break;
                }
            }
        }
        return prev;
    }

    /**
     * Encontra um caminho viável entre dois vértices do grafo
     *
     */
    public Grafo<T> findPath( T start, T end, Collection<T> banned ) {
        searched.clear();
        searched.addAll( banned );
        result = new Grafo<T>();
        goal = end;
        boolean pathFound = findPath( start ); //chama a função acessória findPath
        return pathFound ? result : null;
    }

    /**
     * Auxiliar da função encontrar caminho.
     *
     */
    private boolean findPath( T v ) {
        searched.add( v );
        for ( T n : grafo.getNeighbors( v ) ) {
            if ( n.equals( goal ) ) {
                result.addEdge( v, n );
                return true;
            } else if ( !searched.contains( n ) ) {
                result.addEdge( v, n );
                boolean pathFound = findPath( n );
                if ( pathFound ) return true;
                result.removeEdge( v, n );
            }
        }
        return false;
    }

    /**
     * Encontra um ciclo no grafo de maneira aleatória,
     * caso encontre o ciclo ele retorna o vértice.
     */
    public Grafo<T> findCycle() {
        searched.clear();
        result = new Grafo<T>();
        goal = grafo.getVertices().iterator().next();
        return findCycle( goal ); //chama a função acessória
    }

    /**
     * Função privada que cria o ciclo testando se
     * há um caminho possível e se esse caminho forma
     * um ciclo
     */
    private Grafo<T> findCycle( T v ) {
        searched.add( v );
        for ( T n : grafo.getNeighbors( v ) ) {
            if ( n.equals( goal ) && result.numVertices() > 2 ) {
                result.addEdge( v, n );
                return result;
            } else if ( !searched.contains( n ) ) {
                result.addEdge( v, n );
                Grafo<T> completedCycle = findCycle( n );
                if ( completedCycle != null ) return completedCycle;
                result.removeEdge( v, n );
            }
        }
        return null;
    }

    /**
     * Divide o grafo em duas partes. Cria um pedaço em forma de ciclo, e
     * separa o restante dos vértices na outra parte.
     *
     * @param cycle     Ciclo extraído do grafo
     * @return          O conjunto de todos vértices do grafo
     */
    public Set<Grafo<T>> splitIntoPieces ( Grafo<T> cycle ) {
        searched.clear();
        Set<Grafo<T>> pieces = new HashSet<Grafo<T>>();
        for ( T v : cycle.getVertices() ) {
            searched.add( v );
            for ( T n : grafo.getNeighbors( v ) ) {
                if (   !searched.contains( n ) && !cycle.hasEdge( n, v ) ) {
                    result = new Grafo<T>();
                    result.addEdge( v, n );
                    makePiece( cycle, n );
                    pieces.add( result );
                }
            }
        }
        return pieces;
    }

    /**
     * Função auxiliar.  Extrai do grafo um ciclo e o vértice de onde 
     * esse cilco inicia-se.
     *
     * @param cycle     O ciclo extraído do grafo.
     * @param v         O vértice atual
     *
     * @return          Essa parte do grafo
     */
    private void makePiece( Grafo<T> cycle, T v ) {
        if ( cycle.hasVertex( v ) ) return;
        searched.add( v );
        for (T n : grafo.getNeighbors( v ) ) {
            if ( !result.hasEdge( n, v ) ) {
                result.addEdge( v, n );
                makePiece( cycle, n );
            }
        }
    }


}
