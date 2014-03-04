package ca.uqam.labunix.lotomontecarlo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

	private static int _lotoRange = 36;
	private static int _nbProcessors;
	private static int _nbTachesDynamic = 100;

	private static List<Integer> _combinaison = new ArrayList<Integer>();

	/**
	 * arguments de la forme :
	 * 	1.2.3.4.5.6 10000 0 (100)
	 * avec 
	 * 		1.2.3.4.5.6 : combinaison gagnante
	 * 		10000 : nombre de tests � faire
	 * 		0 : nombres de processeurs (0 pour d�faut)
	 * 		(Facultatif) 100 : nombre de t�ches � cr�er pour la version parallele dynamic
	 */
	public static void main(String[] args) throws Exception {

		if(args.length < 3) {
			System.out.println("Nombres d'arguments incorrect");
			System.exit(-1); 
		}

		// R�cup�re la combinaison en param�tre
		String[] combinaison = args[0].split("\\.");

		for (String string : combinaison) {
			_combinaison.add(Integer.parseInt(string));
		}

		// R�cup�re le nombre de tests en param�tre
		int repeat = Integer.parseInt(args[1]);

		int procs = Integer.parseInt(args[2]);
		_nbProcessors = (procs == 0) ? Runtime.getRuntime().availableProcessors() : procs;

		_nbTachesDynamic = (args.length > 3) ? Integer.parseInt(args[3]) : 100;





		play(repeat, Type.SEQUENTIEL);
		
		play(repeat, Type.PARA_STATIC);
		
		play(repeat, Type.PARA_DYNAMIC);
		
		
		
		//............

		long time = -System.currentTimeMillis();
		// Test en Sac de T�ches
		HashMap<Integer, Integer> result4 = playSAC(repeat);
		time += System.currentTimeMillis();
		System.out.println("Time: " + time / 1000.0 + " sec.");
		// Affiche le resultat
		printResult(repeat, result4);

	}

	private static void play(int repeat, Type type) throws Exception {
		long time = -System.currentTimeMillis();
		HashMap<Integer,Integer> result = null;

		switch (type) {
			case SEQUENTIEL:
				result = playSeq(repeat);
				break;
			case PARA_STATIC:
			case PARA_DYNAMIC:
				result = playPar(repeat, type);
				break;
		}

		time += System.currentTimeMillis();
		System.out.println("Time: " + time / 1000.0 + " sec.");
		// Affiche le resultat
		printResult(repeat, result);
	}


	//================================================================================
	// Version s�quentielle
	//================================================================================
	private static HashMap<Integer,Integer> playSeq(int repeat) throws Exception {
		HashMap<Integer,Integer> result = initResult(); // Initialise le HashMap des r�sultats
		for (int i = 0; i < repeat; i++) {
			int compteur = tirage();
			result.put(compteur, result.get(compteur) + 1);
		}
		return result;
	}

	//================================================================================
	// Version parall�le
	//================================================================================
	private static HashMap<Integer,Integer> playPar(int repeat, Type type) throws Exception {
		HashMap<Integer,Integer> result = initResult(); // Initialise le HashMap des r�sultats
		ExecutorService executor = Executors.newFixedThreadPool(_nbProcessors); // On cr�� _nbProcessors de Thread

		Collection<Future<HashMap<Integer,Integer>>> resu = new ArrayList<Future<HashMap<Integer,Integer>>>();
		Collection<TirageCallable> tasks = new ArrayList<Main.TirageCallable>();

		// On r�cup�re le type de parall�le choisit : STATIC ou DYNAMIC
		// En STATIC on cr�� autant de t�ches qu'il y a de processeurs
		// En DYNAMIC on r�cup�re le nombre de t�ches � cr�er pass� en param�tre du programme
		int nbTaches = (type == Type.PARA_STATIC) ? 
				_nbProcessors : //STATIC
					_nbTachesDynamic; //DYNAMIC


		Main main = new Main();
		for (int i = 0; i < nbTaches; i++) {
			int nbElementsParTache = getNbElementsParTache(i, repeat, nbTaches); // Nombre d'�l�ments � traiter par t�che
			tasks.add(main.new TirageCallable(nbElementsParTache)); // Ajoute la t�che � la liste
		}
		resu = executor.invokeAll(tasks); // Execute toutes les t�ches (FORK)	

		// On parcourt les r�sultat pour les merger quand ils sont disponible (JOIN)
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

	private static HashMap<Integer,Integer> playSAC(int repeat) throws Exception {
		HashMap<Integer,Integer> result = initResult();

		int nThreads = Runtime.getRuntime().availableProcessors();

		ExecutorService executor = Executors.newCachedThreadPool();
		Collection<Future<HashMap<Integer,Integer>>> resu = new ArrayList<Future<HashMap<Integer,Integer>>>();

		Collection<TirageCallable> tasks = new ArrayList<Main.TirageCallable>();


		Main main = new Main();
		for (int i = 0; i < nThreads; i++) {
			int nbParThread = getNbElementsParTache(i, repeat, nThreads);
			tasks.add(main.new TirageCallable(nbParThread));
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

	/**
	 * TirageCallable repr�sente une t�che, qui peut r�aliser de 1 � x tirages (en fonction de la granularit�)
	 */
	public class TirageCallable implements Callable<HashMap<Integer,Integer>> {
		private int repeat = 0;

		public TirageCallable(int repeat) {
			this.repeat = repeat;
		}

		@Override
		public HashMap<Integer,Integer> call() throws Exception {
			HashMap<Integer,Integer> result = initResult();
			for (int i = 0; i < repeat; i++) {
				int compteur = tirage();
				result.put(compteur, result.get(compteur) + 1);
			}
			return result;
		}

	}


	//================================================================================
	// Outils
	//================================================================================

	/**
	 * D�coupe le nombres de t�ches en fonction du nombre de Threads
	 */
	private static int getNbElementsParTache(int position, int nbTachesTotales, int nbThreads) {
		int nbParThread = (int) Math.ceil(nbTachesTotales / nbThreads);
		nbParThread = ( (position * nbParThread) > nbTachesTotales) ?
				nbTachesTotales - (nbParThread * (position-1)) : 
					nbParThread;
				return nbParThread;
	}


	/**
	 * R�alise un tirage au sort et retourne le nombre de num�ros gagnants
	 */
	private static int tirage() throws InterruptedException {
		List<Integer> numeros = new ArrayList<Integer>();
		int numerosGagnants = 0;
		for (int j = 0; j < _combinaison.size(); j++) {
			int k = 0;
			do {
				k = getRandomValue(1, _lotoRange+1);
			} while (numeros.contains(k)); // un nombre ne peut �tre tir� qu'une seule fois
			numeros.add(k);

			// On regarde si le nombre est dans la combinaison gagnante
			if(_combinaison.contains(k)) {
				numerosGagnants++;
			}
		}
		return numerosGagnants;
	}


	/**
	 * Affiche les r�sultats en pourcentage
	 */
	private static void printResult(int loop, HashMap<Integer,Integer> result) {
		NumberFormat format = new DecimalFormat("0.00000");
		for (Entry<Integer,Integer> e : result.entrySet()) {
			Integer key = e.getKey();
			Integer value = e.getValue();
			System.out.println(key + " : " + format.format(((double)value * 100) / (double)loop) + "%");
		}
	}

	/**
	 * Initialise le HashMap avec le nombre d'�l�ments
	 */
	private static HashMap<Integer, Integer> initResult() {
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (int i = 0; i < _combinaison.size() + 1; i++) {
			result.put(i, 0);
		}
		return result;
	}

	/**
	 * Obtient une valeur al�atoire (m�thode Thread Safe)
	 */
	private static int getRandomValue(int i, int j) throws InterruptedException {
		return ThreadLocalRandom.current().nextInt(i,j);
	}


	/**
	 * Enum�ration pour le parall�le STATIC ou DYNAMIC
	 */
	public enum Type {
		SEQUENTIEL, PARA_STATIC, PARA_DYNAMIC
	}

}




