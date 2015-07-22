package vo;

/**
 * This contains the methods for depositing and withdrawing money from an account.
 * This also contains all the getters and setters required.
 *
 * @author Narendra Bidari
 * @author Saranya Suresh
 */

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class AccountVO implements Comparable<AccountVO>{
    
    static Properties log = new Properties();
    static final Logger logger = Logger.getLogger(AccountVO.class);
    
    public AccountVO()  
    {
        
        /**
         * Log4J
         */
        try
        {
            log.load(new FileInputStream("log4jServer.properties"));
            PropertyConfigurator.configure(log);
        }
        catch(Exception e)
        {
            logger.warn(" Error in logging in acc vo");
        }
    }
    
    /**
     * @param addMoney	Adds the amount to the balance and returns the new balance.
     */
    public double addMoney(double deposit) {
    	synchronized (this) {
        this.amount = this.amount + deposit;
        return this.amount;
    	}
    }
    
    /**
     * @param withdraw	Subtracts the amount from the balance and returns the new balance.
     */
    public double subMoney(double withdraw) {
        synchronized(this)
        {
            this.amount = this.amount - withdraw;
            return this.amount;
        }
    }
    
    /**
     * @return	amount	Getter for the amount in the account.
     */
    public double getAmount() {
        return amount;
    }
    
    /**
     * @param amount	Sets the account balance to amount.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    /**
     * @return	accountNumber	Getter for the account number.
     */
    public String getAccountNumber() {
        return accountNumber;
    }
    
    /**
     * @param accountNumber	Setter for account number.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    /**
     * Returns the valid parameter.
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * @param valid Sets the valid parameter with the current value.
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    private volatile double amount;
    private String accountNumber;
    private boolean valid = true;

	@Override
	public int compareTo(AccountVO o) {
		
		return this.getAccountNumber().compareTo(o.getAccountNumber());
	}
}