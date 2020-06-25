package cs2030.simulator;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.Optional;
/**
 * HumanServer class is a subclass of {@link Server}
 */
public class HumanServer extends Server {
	/** the probability of rest of the human server */
	private final double probRest;

	/** the queue length of this human server object */
	private int queueLen = 0;


	private double restingPeriod;
	private final Supplier<Double> restingTimeSupplier;
	private final Supplier<Double> restingDecisionSupplier;
	public HumanServer(int ID,int qMax,Supplier<Double> serviceTimeSupplier,Supplier<Double> restingTimeSupplier,Supplier<Double> restingDecisionSupplier,double probRest){
		super(ID,qMax,serviceTimeSupplier);
		this.restingTimeSupplier = restingTimeSupplier;
		this.restingDecisionSupplier = restingDecisionSupplier;
		this.probRest = probRest;
	}
	@Override
	public String toString() {
		return "server " + "" + this.ID;
	}
	@Override
	public boolean canServeNow(Event e){
	        return this.isIdle();
	}
	@Override
	public boolean canServeLater(){
		return queueLen < this.qMax;
	}
	public Event serveNow(Event e) {
		this.c = e.getCustomer();
		e.changeState(Event.State.SERVED);
		this.nextFree = e.getTime();
		e.setServerID(this.ID);
		e.setServerString(this.toString());
		return e;
	}
	public Event serveLater(Event e) {
	       this.c = e.getCustomer();
	       e.changeState(Event.State.WAITS);
	       queueLen++;
	       customerQueue.add(e.getCustomer().get());
	       e.setServerString(this.toString());
	       return e;
	}
	public Event serveArrival(Event e) {
		if(this.canServeNow(e)){
			return this.serveNow(e);
		}
		if(this.canServeLater()){
			return this.serveLater(e);
		}
		return e.leaveEvent();
	}
	@Override
	public Event serve(Event e) {
		if(this.canServeNow(e)){
			return this.serveNow(e);
		}
		return this.serveLater(e);
	}
	public Optional<Event> processDone(Event e){
               if(restingDecisionSupplier.get() < probRest){
		       Event event = Event.serverEvent(e.getServerID().get(),e.getTime());
		       return Optional.of(event);
	       }
	       else if(queueLen > 0){
		       Customer customerWaiting = customerQueue.get(0);
		       e.setCustomer(customerWaiting);
		       this.serveWaiting(e);
		       return Optional.of(e);
	       }else{
		       this.makeIdle();
		       return Optional.empty();
	       }
	}
	private Event serveWaiting(Event e) {
		this.c = e.getCustomer();
		e.changeState(Event.State.SERVED);
		queueLen = queueLen - 1;
                customerQueue.remove(0);
		return e;
	}
	@Override
	public int getQueueLen() {
		return this.queueLen;
	}
	public Event processServerRest(Event e){
                 this.restingPeriod = restingTimeSupplier.get();
                 double backTime = Optional.of(e.getTime()+restingPeriod).get();
		 e.changeTime(backTime);
		 nextFree = backTime;
		 e.changeState(Event.State.SERVER_BACK);
		 return e;
	}
	public Optional<Event> processServerBack(Event e){
		if(queueLen > 0) {
                      Customer customerWaiting = customerQueue.get(0);
                      e.setCustomer(customerWaiting);
		      Event event = this.serveWaiting(e);
		      return Optional.of(event);
		}
		this.makeIdle();
		return Optional.empty();
        }
	public Event processServed(Event e) {
		this.nextFree = e.getTime();
		this.nextFree += serviceTimeSupplier.get();
		e.changeState(Event.State.DONE);
		e.changeTime(this.nextFree);
		return e;
	}
}
