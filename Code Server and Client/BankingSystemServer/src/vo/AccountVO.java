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

	public String getVec() {
		return vec;
	}
	public void setVec(int[] vec) {
		synchronized (this) {

			StringBuilder d= new StringBuilder();
			for(int i=0;i<vec.length;i++)
			{
				d.append(String.valueOf(vec[i])+",");
			}

logger.debug("given "+ vec+ "sent" + d.toString());
			this.vec = d.toString();
		}
	}

	public void setVec(String vec) {
		synchronized (this) {
			this.vec = vec;
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}



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
			 System.out.println(" Error in logging in acc vo");
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
	private volatile String accountNumber;
	private boolean valid = true;
	private volatile String vec;
	private String host="";
	@Override
	public int compareTo(AccountVO o) {

		return this.getAccountNumber().compareTo(o.getAccountNumber());
	}
}