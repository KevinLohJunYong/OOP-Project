package cs2030.simulator;
public class Customer {
      /** unique id of the customer object */
      public final int ID;

      /** @param ID the unique id of the customer object */
      public Customer(int ID) {
	      this.ID =ID;
      }

      /** @return the string representation of the customer object */
      @Override
      public String toString() {
	      return "" + ID;
      }
}
