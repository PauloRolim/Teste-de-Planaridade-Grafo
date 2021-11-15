import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class GraphTraverser<T> {
    
    private Grafo<T> grafo;

    private Set<T> searched = new HashSet<T>();

    /** A map used for colorings of the Grafo. */
    private Map<T, Integer> coloring = null;

    /** A variable to hold the Grafo resulting from an operation. */
    private Grafo<T> result = null;

    /** A target vertex. */
    private T goal = null;

    /** The next vertex in a traversal (used for walkCycle). */
    private T next = null;

    /** The previous vertex in a traversal (used for walkCycle). */
    private T prev = null;

    /**
     * Constructor for a GrafoTraverser object.
     * 
     * @param Grafo
     */
    public GraphTraverser( Grafo<T> grafo ) {
        this.grafo = grafo;
    }

    /**
     * Tests whether this graph is bipartite.
     *
     * @return      True if it is bipartite.
     */
    public boolean eBipartido() {
        if ( grafo.numVertices() == 0 ) return true;
        coloring = new HashMap<T, Integer>();
        return eBipartido( grafo.getVertices().iterator().next(), true );
    }

    /*
     *Função que testa se o grafo é bipartido
     * O parametro é o proprio grafo, e retorna true se o grafo 
     * for bipartido.
     * Utiliza a propriedade de que um grafo bipartido pode ser
     * colorido com apenas duas cores.
     */
    private boolean eBipartido( T v, boolean color ) {
        if ( coloring.containsKey( v ) ) {
            if ( !coloring.get( v ).equals( color ? 1 : 0 ) ) {
                return false;
            } else {
                return true;
            }
        } else {
            coloring.put( v, color ? 1 : 0 );
            boolean bipartite = true;
            for ( T n : grafo.getNeighbors( v ) ) {
                bipartite = bipartite && eBipartido( n, !color );
            }
            return bipartite;
        }
    }

    /**
     * Walks around a cycle, starting from an arbitrary vertex
     * and going in an arbitrary direction.
     *
     * @return      The next vertex in the walk.
     */
    public T walkCycle() {
        if ( next == null ) {
            prev = grafo.getVertices().iterator().next();
            next = grafo.getNeighbors( prev ).iterator().next();
        } else {
            for ( T n : grafo.getNeighbors( next ) ) {
                if ( !n.equals( prev ) ) {
                    prev = next;
                    next = n;
                    break;
                }
            }
        }
        return prev;
    }

    /**
     * Finds a path between two vertices in the grafo.
     *
     * @param start     The starting vertex.
     * @param end       The ending vertex.
     * @param banned    A collection of sets this path can't pass through.
     * @return          The path between the two vertices.
     */
    public Grafo<T> findPath( T start, T end, Collection<T> banned ) {
        searched.clear();
        searched.addAll( banned );
        result = new Grafo<T>();
        goal = end;
        boolean pathFound = findPath( start );
        return pathFound ? result : null;
    }

    /**
     * Private worker function for findPath.
     *
     * @param v     The current node in the path.
     * @return      True if the path was found.
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
     * Finds an arbitrary cycle in a biconnected graph.
     *
     * @return      A cycle.
     */
    public Grafo<T> findCycle() {
        searched.clear();
        result = new Grafo<T>();
        goal = grafo.getVertices().iterator().next();
        return findCycle( goal );
    }

    /**
     * Private worker function for findCycle.
     *
     * @param v     The current node in the cycle.
     * @return      A cycle.
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
     * Splits the graph into pieces using the given cycle.
     *
     * @param cycle     The cycle to split the graph with.
     * @return          A set containing all the pieces of the graph.
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
     * Private helper function for splitIntoPieces.  Creates a piece
     * (connected without going through the cycle) of the graph
     * from a cycle and a starting node.
     *
     * @param cycle     The separating cycle.
     * @param v         The current vertex.
     *
     * @return          This piece of the graph.
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
