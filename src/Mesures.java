
import java.util.HashMap;

 
public class Mesures {

	public static void main(String[] args) throws Exception {
		
		testRepetitions(100000000);

		testProcesseurs(20);
		
		testNbTaches(10000000);

		System.out.println("# Fin des tests");
		
	}

	private static void testNbTaches(int sup) throws Exception {
		System.out.println("# Test du nombre de t�ches");
		System.out.print(" 				");
		for (int i = 1; i < sup; i = i * 10) {
			System.out.print(" | " + i);
		}
		System.out.println();
		
		System.out.print("M�thode S�quentielle : 		");
		for (int i = 1; i < sup; i = i * 10) {
			//System.out.println("Valeur de i : " + Integer.toString(i));
			String[] args = new String[] {"1.2.3.4.5.6","10000000","0",Integer.toString(i)};
			Main.initialiseParam�tres(args);

			long time = -System.currentTimeMillis();
			HashMap<Integer,Integer> res = Main.play(10000000, Type.SEQUENTIEL);
			time += System.currentTimeMillis();
			System.out.print(" | " +time / 1000.0);
		}
		System.out.println();
		
		System.out.print("M�thode Parall�le Statique : 	");
		for (int i = 1; i < sup; i = i * 10) {
			//System.out.println("Valeur de i : " + Integer.toString(i));
			String[] args = new String[] {"1.2.3.4.5.6","10000000","0",Integer.toString(i)};
			Main.initialiseParam�tres(args);

			long time = -System.currentTimeMillis();
			HashMap<Integer,Integer> res = Main.play(10000000, Type.PARA_STATIC);
			time += System.currentTimeMillis();
			System.out.print(" | " +time / 1000.0);
		}
		System.out.println();
		
		System.out.print("M�thode Parall�le Dynamique : 	");
		for (int i = 1; i < sup; i = i * 10) {
			//System.out.println("Valeur de i : " + Integer.toString(i));
			String[] args = new String[] {"1.2.3.4.5.6","10000000","0",Integer.toString(i)};
			Main.initialiseParam�tres(args);

			long time = -System.currentTimeMillis();
			HashMap<Integer,Integer> res = Main.play(10000000, Type.PARA_DYNAMIC);
			time += System.currentTimeMillis();
			System.out.print(" | " +time / 1000.0);
		}
		System.out.println();
		System.out.println();
	}

	private static void testProcesseurs(int sup) throws Exception {
		
		System.out.println("# Test des threads");
		System.out.print(" 				");
		for (int i = 1; i < sup; i = i + 2) {
			System.out.print(" | " + i);
		}
		System.out.println();
		
		System.out.print("M�thode S�quentielle : 		");
		for (int i = 1; i < sup; i = i + 2) {
			//System.out.println("Valeur de i : " + Integer.toString(i));
			String[] args = new String[] {"1.2.3.4.5.6","10000000",Integer.toString(i),"100"};
			Main.initialiseParam�tres(args);

			long time = -System.currentTimeMillis();
			HashMap<Integer,Integer> res = Main.play(10000000, Type.SEQUENTIEL);
			time += System.currentTimeMillis();
			System.out.print(" | " +time / 1000.0);
		}
		System.out.println();
		
		System.out.print("M�thode Parall�le Statique : 	");
		for (int i = 1; i < sup; i = i + 2) {
			//System.out.println("Valeur de i : " + Integer.toString(i));
			String[] args = new String[] {"1.2.3.4.5.6","10000000",Integer.toString(i),"100"};
			Main.initialiseParam�tres(args);

			long time = -System.currentTimeMillis();
			HashMap<Integer,Integer> res = Main.play(10000000, Type.PARA_STATIC);
			time += System.currentTimeMillis();
			System.out.print(" | " +time / 1000.0);
		}
		System.out.println();
		
		System.out.print("M�thode Parall�le Dynamique : 	");
		for (int i = 1; i < sup; i = i + 2) {
			//System.out.println("Valeur de i : " + Integer.toString(i));
			String[] args = new String[] {"1.2.3.4.5.6","10000000",Integer.toString(i),"100"};
			Main.initialiseParam�tres(args);

			long time = -System.currentTimeMillis();
			HashMap<Integer,Integer> res = Main.play(10000000, Type.PARA_DYNAMIC);
			time += System.currentTimeMillis();
			System.out.print(" | " +time / 1000.0);
		}
		System.out.println();
		System.out.println();
	}

	private static void testRepetitions(int sup) throws Exception {
		System.out.println("# Test des r�p�titions");
		System.out.print(" 				");
		for (int i = 10; i < sup; i = i * 10) {
			System.out.print(" | " + i);
		}
		System.out.println();
		
		System.out.print("M�thode S�quentielle : 		");
		for (int i = 10; i < sup; i = i * 10) {
			//System.out.println("Valeur de i : " + Integer.toString(i));
			String[] args = new String[] {"1.2.3.4.5.6",Integer.toString(i),"0","100"};
			Main.initialiseParam�tres(args);

			long time = -System.currentTimeMillis();
			HashMap<Integer,Integer> res = Main.play(i, Type.SEQUENTIEL);
			time += System.currentTimeMillis();
			System.out.print(" | " +time / 1000.0);
		}
		System.out.println();
		
		System.out.print("M�thode Parall�le Statique : 	");
		for (int i = 10; i < sup; i = i * 10) {
			//System.out.println("Valeur de i : " + Integer.toString(i));
			String[] args = new String[] {"1.2.3.4.5.6",Integer.toString(i),"0","100"};
			Main.initialiseParam�tres(args);

			long time = -System.currentTimeMillis();
			HashMap<Integer,Integer> res = Main.play(i, Type.PARA_STATIC);
			time += System.currentTimeMillis();
			System.out.print(" | " +time / 1000.0);
		}
		System.out.println();
		
		System.out.print("M�thode Parall�le Dynamique : 	");
		for (int i = 10; i < sup; i = i * 10) {
			//System.out.println("Valeur de i : " + Integer.toString(i));
			String[] args = new String[] {"1.2.3.4.5.6",Integer.toString(i),"0","100"};
			Main.initialiseParam�tres(args);

			long time = -System.currentTimeMillis();
			HashMap<Integer,Integer> res = Main.play(i, Type.PARA_DYNAMIC);
			time += System.currentTimeMillis();
			System.out.print(" | " +time / 1000.0);
		}
		System.out.println();
		System.out.println();
	}

}
