package ca.uqam.labunix.lotomontecarlo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
	
	private static int _range = 36;

	/**
	 * arguments de la forme :
	 * 	1.2.3.4.5 10000
	 * avec 
	 * 		1.2.3.4.5 : combinaison gagnante
	 * 		10000 : nombre de tests à faire
	 * @param args 
	 */
	public static void main(String[] args) {
		
		
		
		// Récupère la combinaison en paramètre
		String[] combinaison = args[0].split("\\.");
		List<Integer> combi = new ArrayList<Integer>();
		for (String string : combinaison) {
			combi.add(Integer.parseInt(string));
		}
		
		// Récupère le nombre de tests en paramètre
		int loop = Integer.parseInt(args[1]);
		
		
		long begin = System.currentTimeMillis();
		
		// Test en séquentiel
		HashMap<Integer,Integer> result = playSequentiel(combi, loop);
		
		long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - begin) / 1000.0 + " sec.");
		
		// Affiche le resultat
		printResult(loop, result);
		
		
		
		/*ForkJoinPool pool = new ForkJoinPool();
		TestTask task1 = new TestTask("Task one");
		TestTask task2 = new TestTask("Task two");
		pool.invoke(task1);
		pool.invoke(task2);*/
		
	}

	private static void printResult(int loop, HashMap<Integer, Integer> result) {
		NumberFormat format = new DecimalFormat("#.#####");
		for (Entry<Integer,Integer> e : result.entrySet()) {
			Integer key = e.getKey();
	        Integer value = e.getValue();
	        System.out.println(key + " : " + format.format((value * 100) / (double)loop) + "%");
		}
	}

	private static HashMap<Integer,Integer> playSequentiel(List<Integer> combinaison, int loop) {
		// List du nombre de bonnes combinaisons
		HashMap<Integer,Integer> result = initResult(combinaison);
		 
		// Réalise x fois le random (Méthode Monte Carlo)
		for (int i = 0; i < loop; i++) {
			List<Integer> numbers = new ArrayList<Integer>();
			int compteur = 0;
			// Simule un tirage
			for (int j = 0; j < combinaison.size(); j++) {
				int k = 0;
				do {
					k = getRandomValue(1, _range);
				} while (numbers.contains(k)); // un nombre ne peut être tiré qu'une seule fois
				numbers.add(k);
				
				// On regarde si le nombre est dans la combinaison gagnante
				if(combinaison.contains(k)) {
					compteur++;
				}
			}
			// On enregistre le resultat
			result.put(compteur, result.get(compteur) + 1);
		}
		
		return result;
	}

	//Initialise le HashMap avec le nombre d'éléments
	private static HashMap<Integer, Integer> initResult(List<Integer> combinaison) {
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (int i = 0; i < combinaison.size() + 1; i++) {
			result.put(i, 0);
		}
		return result;
	}
	
	private static int getRandomValue(int i, int j) {
		return ThreadLocalRandom.current().nextInt(i,j);
	}

}

class TestTask extends ForkJoinTask<String> {
	private String msg = null;
	public TestTask(String msg){
		this.msg = msg;
	}
	private static final long serialVersionUID = 1L;
	@Override
	protected boolean exec() {
		//Random a = new Random();
		//int i = a.nextInt(10);
	   int i = ThreadLocalRandom.current().nextInt(1, 10);		
	   System.out.println("ThreadLocalRandom for "+msg+":"+i);
	   return true;
	}
	@Override
	public String getRawResult() {
		return null;
	}
	@Override
	protected void setRawResult(String value) {
	}
}
