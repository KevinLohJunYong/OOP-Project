package cs2030.simulator;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.List;
import java.util.ArrayList;
	/**
	 * server class is the abstract parent class of {@link HumanServer} and {@link SelfCheckOut}
	 * They represent human servers and self-checkout counter servers respectily
	 */
public abstract class Server {
	/** the unique ID of this server */
	public final int ID;

	/** the customer being served */
	protected Optional<Customer> c = Optional.empty();

	/** the customer(s) waiting to be served by a server */
	protected List<Customer> customerQueue = new ArrayList<>();

	/** the next time available of the server which is initialised to 0 */
	protected double nextFree = 0;

	/** whether server is avaiable or not to serve */
	protected boolean isIdle = true;

	/** the maximum queue size of the server */
	protected final int qMax;

	/* the service time supplier of the server which
	 * lazily evaluates(eval only when neccessary) the service time of the server
	 * the service time increases exponentially upon evaluation
	 */
	protected final Supplier<Double> serviceTimeSupplier;

	/**
	 * Constructs a Server with specified ID,maximum queue length and the service time supplier
	 * @param ID server ID
	 * @param qMax maximum queue length
	 * @param serviceTimeSupplier serviceTimeSupplier
	 */ 	
	public Server(int ID,int qMax,Supplier<Double> serviceTimeSupplier) {
		this.ID = ID;
		this.qMax = qMax;
		this.serviceTimeSupplier = serviceTimeSupplier;
	}

	/** @return the unique id of this server */
	public int getID() {
		return this.ID;
	}

	/** @return false if server is serving a customer,true otherwise */
	public boolean isIdle() {
		return c.map(x->false).orElse(true);
	}

	/** change this servers state to idle by removing customer served */
	public void makeIdle() {
		this.c = Optional.empty();
		this.isIdle = true;
	}

	/** @return next free of the server */
	public double getNextFree() {
		return this.nextFree;
	}

	/** @return maximum queue length of the server */
	public int getQMax() {
		return this.qMax;
	}

	/** @return the current queue length of the server */
	abstract int getQueueLen();

	/** @return true if server can serve now, false otherwise */
	abstract boolean canServeNow(Event e);

	/** @return true if server can serve later, false otherwise */
	abstract boolean canServeLater();

	/** @param e event
	 * @return a served Event 
	 */
	abstract Event serveNow(Event e);

	/** @param e event
	 * @return a wait event
	 */
	abstract Event serveLater(Event e);

	/** @param e event
	 * @return a wait or served event
	 */
	abstract Event serve(Event e);

	/** @param e event
	 * @return a done event
	 */
	abstract Event processServed(Event e);

	/** @param e event
	 * @return an optional event it could be a 
	 * server rest event if the server is resting 
	 * immediately after its done
	 * it could be a 
	 * served event when server immediately serves waiting customers
	 * or it could have no events
	 * this happens when server is not resting and doesnt have
	 * any waiting customers */
	abstract Optional<Event> processDone(Event e);
}
