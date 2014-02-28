package ca.uqam.labunix.lotomontecarlo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

	private static int _range = 36;
	private static int _processors = Runtime.getRuntime().availableProcessors() / 2;

	private static List<Integer> _combinaison = new ArrayList<Integer>();

	/**
	 * arguments de la forme :
	 * 	1.2.3.4.5.6 10000
	 * avec 
	 * 		1.2.3.4.5.6 : combinaison gagnante
	 * 		10000 : nombre de tests à faire
	 */
	public static void main(String[] args) throws Exception {



		// Récupère la combinaison en paramètre
		String[] combinaison = args[0].split("\\.");

		for (String string : combinaison) {
			_combinaison.add(Integer.parseInt(string));
		}

		// Récupère le nombre de tests en paramètre
		int loop = Integer.parseInt(args[1]);


		long begin = System.currentTimeMillis();
		// Test en séquentiel
		HashMap<Integer,Integer> result = playS(loop);
		long end = System.currentTimeMillis();
		System.out.println("Time: " + (end - begin) / 1000.0 + " sec.");
		// Affiche le resultat
		printResult(loop, result);

		begin = System.currentTimeMillis();
		// Test en séquentiel
		HashMap<Integer, Integer> result2 = playPIGA(loop);
		end = System.currentTimeMillis();
		System.out.println("Time: " + (end - begin) / 1000.0 + " sec.");
		// Affiche le resultat
		printResult(loop, result2);


	}



	private static HashMap<Integer,Integer> playS(int loop) throws Exception {
		HashMap<Integer,Integer> result = initResult();
		for (int i = 0; i < loop; i++) {
			int compteur = tirage();
			result.put(compteur, result.get(compteur) + 1);
		}
		return result;
	}

	private static HashMap<Integer,Integer> playPIGA(int loop) throws Exception {
		HashMap<Integer,Integer> result = initResult();

		ExecutorService executor = Executors.newFixedThreadPool(_processors);
		Collection<Future<HashMap<Integer,Integer>>> resu = new ArrayList<Future<HashMap<Integer,Integer>>>();

		Collection<TirageCallable> tasks = new ArrayList<Main.TirageCallable>();

		Main main = new Main();
		for (int i = 0; i < _processors; i++) {
			tasks.add(main.new TirageCallable(loop / _processors));
		}
		resu = executor.invokeAll(tasks);

		for (Future<HashMap<Integer,Integer>> res : resu) {
			for (Entry<Integer,Integer> e : res.get().entrySet()) {
				Integer key = e.getKey();
				Integer value = e.getValue();
				result.put(key, result.get(key) + value);
			}
		}
		executor.shutdown();

		return result;
	}


	private static int tirage()
			throws InterruptedException {
		List<Integer> numbers = new ArrayList<Integer>();
		int compteur = 0;
		for (int j = 0; j < _combinaison.size(); j++) {
			int k = 0;
			do {
				k = getRandomValue(1, _range+1);
			} while (numbers.contains(k)); // un nombre ne peut être tiré qu'une seule fois
			numbers.add(k);

			// On regarde si le nombre est dans la combinaison gagnante
			if(_combinaison.contains(k)) {
				compteur++;
			}
		}
		return compteur;
	}



	private static void printResult(int loop, HashMap<Integer,Integer> result) {
		NumberFormat format = new DecimalFormat("#.#####");
		for (Entry<Integer,Integer> e : result.entrySet()) {
			Integer key = e.getKey();
			Integer value = e.getValue();
			System.out.println(key + " : " + format.format((value * 100) / (double)loop) + "%");
		}
	}

	//Initialise le HashMap avec le nombre d'éléments
	private static HashMap<Integer, Integer> initResult() {
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (int i = 0; i < _combinaison.size() + 1; i++) {
			result.put(i, 0);
		}
		return result;
	}

	private static int getRandomValue(int i, int j) throws InterruptedException {
		//Thread.sleep(500);
		return ThreadLocalRandom.current().nextInt(i,j);
	}

	public class TirageCallable implements Callable<HashMap<Integer,Integer>> {

		private int loop = 0;

		public TirageCallable(int loop) {
			this.loop = loop;
		}

		@Override
		public HashMap<Integer,Integer> call() throws Exception {
			HashMap<Integer,Integer> result = initResult();


			for (int i = 0; i < loop; i++) {
				int compteur = tirage();
				result.put(compteur, result.get(compteur) + 1);
			}

			return result;
		}

	}

}




