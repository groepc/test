package edu.avans.hartigehap.domain;

public class EmptyBillException extends Exception {

	private static final long serialVersionUID = 1L;

    /**
     * Constructs an instance of <code>EmptyBillException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public EmptyBillException(String msg) {
        super(msg);
    }
}
