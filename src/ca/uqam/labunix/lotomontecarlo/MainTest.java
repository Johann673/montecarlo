package ca.uqam.labunix.lotomontecarlo;

import org.junit.Test;

public class MainTest {

	@Test
	public void test() throws Exception {
		
		//Test loop
		for (int i = 10; i < 1000000000; i = i * 10) {
			System.out.println("Valeur de i : " + Integer.toString(i));
			String[] args = new String[] {"1.2.3.4.5.6",Integer.toString(i),"0","100"};
			Main.main(args);
			System.out.println("====================================");
		}
		
		//Test nbTaches para dynamic
		/*for (int i = 1; i < 10000000; i = i * 10) {
			System.out.println("Valeur de i : " + Integer.toString(i));
			String[] args = new String[] {"1.2.3.4.5.6","10000000","0",Integer.toString(i)};
			Main.main(args);
			System.out.println("====================================");
		}*/
		
		//test nb processor
		/*for (int i = 1; i < 10000; i = i * 10) {
			System.out.println("Valeur de i : " + Integer.toString(i));
			String[] args = new String[] {"1.2.3.4.5.6","1000000",Integer.toString(i),"100"};
			Main.main(args);
			System.out.println("====================================");
		}*/
		
		
	}

}
