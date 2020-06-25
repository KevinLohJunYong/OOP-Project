package cs2030.simulator;
import java.util.Optional;
/**
 * the event class keeps track of the different events possible
 * the customer events include: Arrival, Waits, Served, Leaves, Done
 * the server events include: Server Rest,Server Back
 * the output of the project will ultimately print these events
 * so the event class has a toString() method which enables this
 */
public class Event implements Comparable<Event>{
	/** time of the event */
        private double time;

	/** 
	 * the customer associated with the event 
	 * no customers are associated in server events
	 */
	private Optional<Customer> c;

	/** 
	 * the server id of the sever associated in the events 
	 * to print the associated events out correctly
	 */
	private Optional<Integer> serverID;

	/** state of the event */
        private State state;

	/**
	 * whether the event should be printed or not
	 * server events are not printed but are processed
	 * into customer events
	 */
        private boolean isPrintable;

	/** the associated toString() of the server if any */
	private Optional<String> serverString = Optional.empty();

	/** the event states which can be modelled by a static inner class enum */
	static enum State {
		ARRIVES,
		WAITS,
		SERVED,
		LEAVES,
		DONE,
		SERVER_REST,
		SERVER_BACK;
		@Override
		public String toString(){
		switch(this) {
                     case ARRIVES:
			    return "arrives";
		     case WAITS: 
			    return "waits to be served by ";
		     case SERVED: 
			    return "served by ";
		     case LEAVES: 
			    return "leaves";
		     case DONE: 
			    return "done serving by ";
		     case SERVER_REST:
		     case SERVER_BACK:
			    return "";
		     default:
		     throw new IllegalStateException("Invalid EventState");
	        }
	}
	}
	/** @return a leave event
	 * since i will be using streams later on in {@link CheckOutLanes}
	 * i adopt an functional programming approach to model leave event 
	 * with a function
	 */
	public Event leaveEvent() {
		this.changeState(Event.State.LEAVES);
		return this;
	}

	/** @param serverString the toString() of the server
	 *  the serverString is used to construct the toString()
	 *  of the event in this implementation
	 * 
	  */
	public void setServerString(String serverString) {
		this.serverString = Optional.of(serverString);
	}

        /** @return the serverString of this event object */
	public Optional<String> getServerString() {
		return this.serverString;
	}

	/** @return true if the event object has a server String
	 * or if it doesnt 
	 */
	public boolean hasServerString() {
		return serverString.map(x->true).orElse(false);
	}

	/** @return the current time of the event object */
	public double getTime() {
		return this.time;
	}

	/** @return the customer,if any, of this event object */
	public Optional<Customer> getCustomer() {
		return this.c;
	}

	/** set an event with a particular customer */
	public void setCustomer(Customer c){
		this.c = Optional.of(c);
	}

	/** @return the serverID,if any, of this event object */
	public Optional<Integer> getServerID() {
		return this.serverID;
	}

	/** @return the state of this event object */
	public State getState() {
		return this.state;
	}

	/** @param t the updated time of the event object */
	public void changeTime(double t){
		time = t;
	}

	/** @param state the updated state of the event object */
	public void changeState(State state) {
		this.state = state;
	}

	/** @param id the assigned server id of the event object */
	public void setServerID(int id) {
		this.serverID = Optional.of(id);
	}

	/** @return true if the event object has server id false otherwise */
	public boolean hasServerID() {
		return this.serverID.map(x->true).orElse(false);
	}

	/**
	 * @return true if the particular event object needs to be printed 
	 * false for server event objects
	 */
	public boolean isPrintable() {
		switch(this.state) {
			case SERVER_REST:
			case SERVER_BACK:
			     return false;
			default:
			     return true;
	        }
	}
	/**
	 * @param time the current time of the event
	 * @param serverID the server id, if any, of the event object
	 * @param state the state of the event object
	 */
public Event(double time,Optional<Customer> c,Optional<Integer> serverID,State state){
	this.time = time;
	this.c = c;
	this.serverID = serverID;
	this.state = state;
     }  
        /**
	 * @param time the current time of the customer event object
	 * @param c the customer associated with this customer event object
	 */
        private Event(double time,Optional<Customer> c){
		this(time,c,Optional.empty(),Event.State.ARRIVES);
	}

	/**
	 * @param time the current time of the server event object
	 * @param serverID the serverID of the server event object
	 * @param state the state of the server event object
	 */
	private Event(double time,Optional<Integer> serverID,State state){
		this(time,Optional.empty(),serverID,state);
	}

	/**
	 * @param c the customer needed to construct a customer event object
	 * @param time the initial time of the cutsomer event object
	 * @return a customer event object with the initial customer state ARRIVES
	 */
        public static Event customerEvent(Customer c,double time) {
		return new Event(time,Optional.of(c));
	}

	/**
	 * @param serverID the serverID of the server event object
	 * @param time the time of the server event event object
	 * @return a server event object whose state is initialised to be SERVER_REST
	 */
	public static Event serverEvent(int serverID,double time) {
		return new Event(time,Optional.of(serverID),State.SERVER_REST);
	}

	/**
	 * @return true if a customer is present,false otherwise
	 */
	public boolean isCustomerEvent() {
		return c.map(x->true).orElse(false);
	}
	/**
	 * @return true if it is not a customerEvent,false otherwise
	 */
        public boolean isServerEvent() {
		return !(isCustomerEvent());
	}
	/**
	 * @return the id of the event object
	 * since customer done event is processed before SERVER_REST
	 * event though both occurs at the same time,
	 * to compare them i assign a -1 for server event objects so they will be processed after customer event objects
	 */
	public int getEffectiveID() {
		return isCustomerEvent()
		     ? c.get().ID
		     : -1;
	}
	/**
	 * @param other the other event to be compared to
	 * @return a postive,zero or negative value 
	 * it first checks if effective ids are equal
	 * then checks if time is equal
	 */
       public int compareTo(Event other) {
        return (other.time == this.time)
            ? this.getEffectiveID() - other.getEffectiveID()
            : this.time - other.time < 0
                ? -1
                : 1;
       }

       /**
	* @return the toString() of this event object
	*/
    	@Override
	public String toString() {
		String s = state.toString();
		return String.format("%.3f %s %s",time,c.get().toString(),s + (hasServerString()?serverString.get()
        : ""));
	}
}
