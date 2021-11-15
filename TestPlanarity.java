import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Classe que comtem critérios de teste para dizer se um grafo é planar
 *
 */
public class TestPlanarity {

    /**
     * conjunto contendo a string de caracteres de tamanho quatro, 
     * que indicam o não entrelaçamento do grafo.
     */
    private static final Set<String> notInterlacedSet = makeNIS();

    /** Instanciação do conjunto de strings acima. */
    private static Set<String> makeNIS() {
        Set<String> set = new HashSet<String>();
        String[] strings = {"xbyb", "bybx", "ybxb", "bxby"};
        for (String s : strings) set.add(s);
        return set;
    }

    /**
     * Função que constroi um grafo a partir das arestas fornecidas
     * no arquivo de entrada
     * 
     */
    public Grafo<Integer> readGraphFromFile( String fileName ) {
        Grafo<Integer> grafo = new Grafo<Integer>();
        Scanner in = null;
        try {
            in = new Scanner( new BufferedInputStream( new FileInputStream( fileName )));
            while ( in.hasNextInt() ) {
                int a = in.nextInt();
                int b = in.nextInt();
                grafo.addEdge(a,b);
            }
        } catch ( Exception e ) {
            System.err.println( "Erro ao ler o arquivo de entrada \'" + fileName + "\'." );
            System.err.println( "Uso: java TestPlanarity Entrada\\<nome do arquivo>" );
            System.exit(1);
        }
        return grafo;
    }

    /**
     * Tests the planarity of a BICONNECTED graph.  Has to suppress warnings
     * because Java is silly and can't handle arrays of generics properly.
     *
     * @param graph     The graph to test for planarity.
     * @param cycle     A cycle within the above graph, preferably separating.
     * @return          Whether the graph is planar or not.
     */
    @SuppressWarnings("unchecked")
    public <T> boolean testPlanarity( Grafo<T> grafo, Grafo<T> cycle ) {
        if ( grafo.numEdges() > 3 * grafo.numVertices() - 6 ) {
            return false;
        }
        Set<Grafo<T>> pieces = Grafo.splitIntoPieces( grafo, cycle );
        for ( Grafo<T> piece : pieces ) {
            if ( !Grafo.isPath( piece ) ) {     // Don't bother if the piece is a path.

                // Need a starting vertex that is an attachment point between the piece and the cycle.
                T start = null;
                for ( T v : cycle.getVertices() ) {
                    if ( piece.hasVertex( v ) ) {
                        start = v;
                        break;
                    }
                }

                // Construct the part of the new cycle that is coming from the old cycle.
                Grafo<T> cycleSegment = new Grafo<T>( cycle );
                T prev = start;

                // Choose an arbitrary direction to traverse the cycle in.
                T curr = cycle.getNeighbors( prev ).iterator().next();

                // Remove all the edges between the starting attachment point and the
                // next found attachment point from the cycleSegment graph.
                cycleSegment.removeEdge( prev, curr );
                while ( !piece.hasVertex( curr ) ) {
                    for ( T v : cycle.getNeighbors( curr ) ) {
                        if ( !v.equals( prev ) ) {
                            prev = curr;
                            curr = v;
                            break;
                        }
                    }
                    cycleSegment.removeEdge( prev, curr );
                }
                T end = curr;       // end is the next attachment point found.

                // Find a path through the piece connecting the attachment points, but
                // make sure that it doesn't go through a different attachment point.
                GraphTraverser<T> traverser = new GraphTraverser<T>( piece );
                Grafo<T> piecePath = traverser.findPath( start, end, cycle.getVertices() );

                // Construct the new graph and the new cycle accordingly.
                Grafo<T> pp = Grafo.addGraphs( cycle, piece );
                Grafo<T> cp = Grafo.addGraphs( cycleSegment, piecePath );

                // Recurse using them as parameters.
                boolean planar = testPlanarity( pp, cp );
                if ( !planar ) return false;
            }
        }

        // If all the piece/cycle combinations are planar, then test the interlacement.
        Grafo<Integer> interlacement = new Grafo<Integer>();
        Object[] pieceArray = pieces.toArray();

        // For each pair of pieces, see if they're interlaced.
        for ( int i = 0; i < pieceArray.length; i++ ) {
            Grafo<T> x = (Grafo<T>) pieceArray[i];
            for ( int j = i + 1; j < pieceArray.length; j++ ) {
                Grafo<T> y = (Grafo<T>) pieceArray[j];

                char lastChar = ' ';    // Store the last character added to make things easier.
                String symList = "";    // The list of symbols representing the interlacement of the pieces.
                int bCount = 0;         // The number of 'b' symbols.  Again, to make things easy.

                // Walk around the cycle and construct the symbol list.
                GraphTraverser<T> traverser = new GraphTraverser<T>( cycle );
                for ( int k = 0; k < cycle.numVertices(); k++ ) {
                    T v = traverser.walkCycle();
                    // If a node is in both pieces, then add a 'b'.
                    if ( x.hasVertex( v ) && y.hasVertex( v ) ) {
                        bCount++;
                        symList += 'b';
                        lastChar = 'b';
                    // Else add if it's only in one piece and it's not the last symbol added.
                    } else if ( x.hasVertex( v ) && lastChar != 'x' ) {
                        symList += 'x';
                        lastChar = 'x';
                    } else if ( y.hasVertex( v ) && lastChar != 'y' ) {
                        symList += 'y';
                        lastChar = 'y';
                    }
                }
                // Check for wrap-around adjacency of x's or y's.
                if ( (lastChar == 'x' || lastChar == 'y') && symList.charAt(0) == lastChar ) {
                    symList = symList.substring(1);
                }
                boolean interlaced = false;
                if ( symList.length() > 4 || bCount > 2 ) {
                    interlaced = true;
                } else if ( symList.length() == 4 && !notInterlacedSet.contains( symList ) ) {
                    interlaced = true;
                }
                if ( interlaced ) {
                    interlacement.addEdge( i, j );
                }
            }
        }
        return Grafo.eBipartido( interlacement );
    }

} // TestPlanarity
