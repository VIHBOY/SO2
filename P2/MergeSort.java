import java.util.*;
import java.util.concurrent.*;


/*
Por lo general cuando se hace el proceso de división del arreglo de elementos en arreglos unitarios,
en el proceso de mezcla quedan esperando los otros arreglos, a que se mezcle por nivel y de forma secuencial.

El multithreading optimiza lo anterior, ya que permite que se haga multimezcla, dado a que mientras se produce 
la mezcla de un arreglo, paralelamente se hace la mezcla de los otros.    
*/
public class MergeSort<N extends Comparable<N>> extends RecursiveTask<List<N>> {

    private static final long serialVersionUID = 1L;
    private List<N> elementos;

    public MergeSort(List<N> elementos) {
        this.elementos = new ArrayList<>(elementos);
    }

    @Override


/*
Compute es una función que sirve para separar una lista en dos, a partir de un 
pivote que representa al elemento que se encuentra en la posicion this.elementos.size()/2.

Luego aplicar MergeSort de forma recursiva y paralela a las dos listas resultantes.

Finalmente hace merge entre el arreglo de la izquierda con el de la derecha.
*/
    protected List<N> compute() {
        if(this.elementos.size() <= 1)
            return this.elementos;
        else {
            final int pivote = this.elementos.size() / 2;
            MergeSort<N> leftTask = new MergeSort<N>(this.elementos.subList(0, pivote));
            MergeSort<N> rightTask = new MergeSort<N>(this.elementos.subList(pivote, this.elementos.size()));

            leftTask.fork();
            rightTask.fork();

            List<N> izq = leftTask.join();
            List<N> der = rightTask.join();

            return mezcla(izq, der);
        }
    }

/*
Mezcla es una función que sirve para mezlcar dos listas, en donde se  compara el 
elemento 0 de izq con el elemento 0 de der, de tal forma que si izq[0] > der[0], 
entonces se agrega a la nueva lista mezclada el elemento izq[0] y sino der[0].
*/
    private List<N> mezcla(List<N> izq, List<N> der) {
        List<N> ordenado = new ArrayList<>();
        while(!izq.isEmpty() || !der.isEmpty()) {
            if(izq.isEmpty())
                ordenado.add(der.remove(0));
            else if(der.isEmpty())
                ordenado.add(izq.remove(0));
            else {
                if( izq.get(0).compareTo(der.get(0)) > 0 )
                    ordenado.add(izq.remove(0));
                else
                    ordenado.add(der.remove(0));
            }
        }
        return ordenado;
    }

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        String entrada;
        String[] sLista;
        Scanner sc = new Scanner(System.in);
        System.out.println("Lista: ");
        entrada = sc.nextLine();
        
        sLista = entrada.split(",");
        int n = sLista.length;
        Integer [] lista = new Integer [n];
        for(int i=0; i<n; i++) {
            lista[i] = Integer.parseInt(sLista[i]);
        }
        List<Integer> listaO = forkJoinPool.invoke(new MergeSort<Integer>(Arrays.asList(lista)));
        System.out.println("Lista ordenada: " + listaO);
        sc.close();
    }
}