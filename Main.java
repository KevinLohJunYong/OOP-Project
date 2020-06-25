import java.util.Scanner;
import cs2030.simulator.Simulator;
class Main {

	/** args the arguments in string format */
	public static void main(String[] args) {

		/** the scanner to scan the inputs */
		Scanner sc = new Scanner(System.in);

		/** seed of the random generator object */
		int seed = sc.nextInt();

		/** number of human servers */
		int humanServerCount = sc.nextInt();

		/** num of self check outs */
		int numOfMachines = sc.nextInt();

		/** maximum queue length of servers */
		int qMax = sc.nextInt();

		/** number of arrival events */
		int numOfArrivals = sc.nextInt();

		/** arrival rate of the customer */
		double arrivalRate = sc.nextDouble();

		/** service rate of the customer */
		double serviceRate = sc.nextDouble();

		/** resting rate of the human server */
		double restingRate =  sc.nextDouble();

		/** probability of rest of a human server */
		double probRest = sc.nextDouble();

		/** the probability a customer will be greedy */
		double greedyProb = sc.nextDouble();

		/** creates a simulator object */
		Simulator simulator = new Simulator(seed,humanServerCount,numOfMachines,qMax,numOfArrivals,arrivalRate,serviceRate,restingRate,probRest,greedyProb);            /** simulate method of simulator to print out the events */
		simulator.simulate();
	}
}
