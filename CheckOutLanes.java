package cs2030.simulator;
import java.util.function.Supplier;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.function.Predicate;
import java.util.stream.IntStream;
public class CheckOutLanes {
	/** the list of servers in the checkoutlanes */
	private List<Server> servers = new ArrayList<>();

	/** @param humanServerCount the number of human servers
	 * @param machinesCount the number of SelfCheckOut objects
	 * @param queueMax the maximum queue length of the servers
	 * @param rg RandomGenerator
	 * @param probRest the probability a human server will rest
	 */
	public CheckOutLanes(int humanServerCount,int machinesCount, int queueMax, RandomGenerator rg, double probRest) {
		Supplier<Double> serviceTimeSupplier = () -> rg.genServiceTime();
		Supplier<Double> restingTimeSupplier = () -> rg.genRestPeriod();
		Supplier<Double> restingDecisionSupplier = () -> rg.genRandomRest();
		IntStream.rangeClosed(1,humanServerCount).forEach(i->servers.add(new HumanServer(i,queueMax,serviceTimeSupplier,restingTimeSupplier,
						restingDecisionSupplier,probRest)));
		IntStream.rangeClosed(1,machinesCount).forEach(i->servers.add(new SelfCheckOut(i+humanServerCount,queueMax,serviceTimeSupplier)));
	}

	/** @param p predicate that takes in a server and returns true if
	 * it satisfies the predicate,false otherwise
	 * @return a server if any that satisfies the predicate
	 */
	public Optional<Server> find(Predicate<? super Server> p){
		return servers.stream().filter(p).findFirst();
	}

	/** @param e the arrival event
	 * @return a served,wait or leave event
	 */
	private Event serveArrival(Event e) {
		return find(ser->ser.canServeNow(e))
		       .map(ser-> (ser.serveNow(e)))
		       .orElseGet(()->find(ser->ser.canServeLater())
					.map(ser-> ser.serveLater(e))
		                        .orElseGet(()->e.leaveEvent()));
	}

	/** @param e the event which has a greedy customer
	 * @return a served,wait or leave event
	 */
	private Event serveGreedyArrival(Event e){
		GreedyCustomer greedy =(GreedyCustomer)e.getCustomer().get();
	        Optional<Event> event = find(x->x.canServeNow(e)).map(x->x.serveNow(e));
                if(event.isPresent()){
			return event.get();
                }else{
		Optional<Integer> shortestQueueLen
		      	= servers.stream()
			         .filter(x->x.canServeLater())
				 .map(x->x.getQueueLen())
				 .reduce((a,b)->a>b?b:a);
                if(shortestQueueLen.isPresent()){
			Server s = this.find(x->x.getQueueLen()==shortestQueueLen.get()).get();
			return s.serveLater(e);
                }
                return e.leaveEvent();		
	        }		
	}

	/** @param e the event to be served
	 * @return a processed event according to its state
	 */
	public Optional<Event> serve(Event e){
		if(e.getState() == Event.State.ARRIVES) {
                  boolean greedyArrival = e.getCustomer().get() instanceof GreedyCustomer;
			if(greedyArrival){
			     Event event = this.serveGreedyArrival(e);
			     return Optional.of(event);
			}
			Event event = this.serveArrival(e);
			return Optional.of(event);
		}
		int serverID = e.getServerID().get();
		Server s = servers.get(serverID-1);
		e.setServerString(s.toString());
		if(e.getState() == Event.State.SERVED) {
			Event event = s.processServed(e);
			return Optional.of(event);
		}
		if(e.getState() == Event.State.DONE) {
			Optional<Event> event = s.processDone(e);
			return event;
		}
		if(e.getState() == Event.State.SERVER_REST){
		        HumanServer h = (HumanServer)s; 
			Event event = h.processServerRest(e);
			return Optional.of(event);
		}
		if(e.getState() == Event.State.SERVER_BACK){
			HumanServer h = (HumanServer)s;
			Optional<Event> event = h.processServerBack(e);
			return event;
		}else{
			return Optional.empty();
		}

	}
}
