public class Main {
    
    /* Para compilar use o comando javac *.java
     * Para executar use o comando java Main Entrada\<nomeDoArquivo>
     */
    public static void main ( String[] args ) {
        TestPlanarity testPlanarity = new TestPlanarity();

        // Check number of arguments.
        if ( args.length != 1 ) {
            System.err.println( "Uso: java TestPlanarity Entrada\\<nome do arquivo>" );
            System.exit(1);
        }

        Grafo<Integer> grafo = testPlanarity.readGraphFromFile(args[0]);
        Grafo<Integer> cycle = (new GraphTraverser<Integer>( grafo )).findCycle();
        System.out.println(testPlanarity.testPlanarity(grafo,cycle) ? "Planar!" : "Nao planar!" );
    }


}
