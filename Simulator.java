package cs2030.simulator;
import java.util.PriorityQueue;
import java.util.function.Supplier;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
public class Simulator {
        /** random generator of this simulator object */
	private final RandomGenerator rg;

	/** checkoutlanes of this object */
	private final CheckOutLanes lanes;

	/** list of servers of this object */
	private List<Server> servers = new ArrayList<>();

	/** priority queue of this object */
	private PriorityQueue<Event> events = new PriorityQueue<>();

	/** number of arrival events */
	private final int arrivalCount;

	/** number of customers who left */
	private int leaveCount = 0;

	/** number of customers who are done */
	private int doneCount = 0;

	/** total time customers spent waiting to be served */
	private double totalWaitTime = 0;

	/** the time a customer spent waiting to be served */
	private double waitTime;

	/** the supplier of a probability to check if a customer will be greedy*/
	private final Supplier<Double> greedyDecisionSupplier;

	/** the probability a customer will be greedy */
	private final double greedyProb;

	/** @param seed the seed of the random generator 
	 * @param numOfMachines the number of self checkout objects 
	 * @param qMax the maximum queue length of the servers
	 * @param numOfArrivals the number of customers
	 * @param arrivalRate the arrival rate of a customer
	 * @param serviceRate the service rate of a server
	 * @param resting rate the resting rate of s human server
	 * @param probRest the probability a human server will rest
	 * @param greedyProb the probability a customer will be greedy
	 */
	public Simulator(int seed, int humanServerCount,int numOfMachines,int qMax,int numOfArrivals,double arrivalRate,double serviceRate,double restingRate,double probRest,double greedyProb) {
		this.arrivalCount = numOfArrivals;
		this.rg = new RandomGenerator(seed,arrivalRate,serviceRate,restingRate);
		this.greedyProb = greedyProb;
		this.greedyDecisionSupplier = ()->rg.genCustomerType();
		this.lanes = new CheckOutLanes(humanServerCount,numOfMachines,qMax,rg,probRest);
		double t = 0;
		for(int i=0;i<numOfArrivals;i++){
			if(greedyDecisionSupplier.get()<greedyProb){
				events.add(Event.customerEvent(new GreedyCustomer(i+1),t));
				Event e = events.poll();
				events.add(e);
				t += rg.genInterArrivalTime();
			}
		        else{
			events.add(Event.customerEvent(new Customer(i+1),t));
			t += rg.genInterArrivalTime();
			}
                }
	}

	/** prints out all the events in the priority queue and the statistics*/
	public void simulate(){
	while(!(events.isEmpty())) {
		Event e = events.poll();
		if(e.isPrintable()){
		System.out.println(e);
	        }
                if(e.getState() == Event.State.WAITS){
			continue;
		}
		if(e.getState() == Event.State.LEAVES){
			totalWaitTime += e.getTime();
			leaveCount++;
			continue;
		}
       		if(e.getState() == Event.State.DONE){
			doneCount++;
		}
		if(e.getState() == Event.State.ARRIVES){
			totalWaitTime -= e.getTime();
		}
		if(e.getState() == Event.State.SERVED){
			totalWaitTime += e.getTime();
		}
		switch(e.getState()){
                        case ARRIVES:
	                case SERVED:
			case DONE:
			case SERVER_REST:
			case SERVER_BACK:
			Optional<Event> event = lanes.serve(e);
			event.ifPresent(x->events.add(x));
			default:
			continue;
		}
	}
	       int serveCount = arrivalCount - leaveCount;	      
	       double avgWaitTime = (serveCount == 0)
		                    ? 0
				    :totalWaitTime/(serveCount);
	       String stats = String.format("[%.3f %d %d]",avgWaitTime,doneCount,leaveCount);
	       System.out.println(stats);
	}
}

	
