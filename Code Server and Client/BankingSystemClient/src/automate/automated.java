package automate;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class automated 
{    
	static Properties log = new Properties();
	static final Logger logger = Logger.getLogger(automated.class);
	public automated() 
	{
		try
		{
			log.load(new FileInputStream("log4jClient.properties"));
			PropertyConfigurator.configure(log);
		}
		catch(Exception e)
		{
			logger.error("Error in loggingin automated.");
		}
	}
	public  String accountNumber = "";
	public  int times =0;
	public String path = "";
	public int noOfServersActive =0;
	public automated(String accountNumber, int times, int noOfServersActive, String path)
	{
		this.accountNumber = accountNumber;
		this.times = times;
		this.path = path;
		this.noOfServersActive = noOfServersActive;
	}
    
	/**
	 * Automatically generates a list of commands and returns it.
	 */
	public ArrayList<String> returnTransferCommands ()
	{
		ArrayList<String> commandsList = new ArrayList<String>();
		RandomGen amount = new RandomGen(50, 150);
		ArrayList<String> Function = new ArrayList<String>();
		Function.add("Balance");
		Function.add("Deposit");
		Function.add("Withdraw");
		Function.add("Transfer");
		
		generateValidAccounts ac = new generateValidAccounts();
		ArrayList<String> acc=  ac.returnAccounts(path, this.noOfServersActive);
		acc.remove(this.accountNumber);
		
		
		if(acc.size()-1>0)
		{
			int k=0;
			
			for(int i=0;i<=this.times;i++)
			{
				k=k%(acc.size()-1);
				StringBuffer pri = new StringBuffer();
				switch (3) 
				{
                    case 0: pri.append(Function.get(0));
                        break;
                    case 1: pri.append(Function.get(1));
                        pri.append(" ");
                        pri.append(amount.RandomFirst());
                        break;
                    case 2: pri.append(Function.get(2));
                        pri.append(" ");
                        pri.append(amount.RandomFirst());
                        break;
                        
                    case 3: pri.append(Function.get(3));
                        pri.append(" ");
                        pri.append(acc.get(k));
                        pri.append(" ");
                        pri.append(amount.RandomFirst());
                        break;
                    default: break;
				}
				commandsList.add(pri.toString());
				k++;
			}
		}
		else
		{
			System.out.println("No accounts present for transfer");
		}
        
		return commandsList;
	}
    
    
	public ArrayList<String> returnSnapshotCommands ()
	{
		ArrayList<String> commandsList = new ArrayList<String>();
		RandomGen amount = new RandomGen(100, 500);
		ArrayList<String> Function = new ArrayList<String>();
		Function.add("Balance");
		Function.add("Deposit");
		Function.add("Withdraw");
		Function.add("Transfer");
		Function.add("Snapshot");
		generateValidAccounts ac = new generateValidAccounts();
		ArrayList<String> acc=  ac.returnAccounts(path, this.noOfServersActive);
		acc.remove(this.accountNumber);
		
		if(acc.size()>0)
		{
			RandomGen accountRand = new RandomGen(0, acc.size()-1);
			for(int i=0;i<=this.times;i++)
			{
				StringBuffer pri = new StringBuffer();
				switch (4) 
				{
                    case 0: pri.append(Function.get(0));
                        break;
                    case 1: pri.append(Function.get(1));
                        pri.append(" ");
                        pri.append(amount.RandomFirst());
                        break;
                    case 2: pri.append(Function.get(2));
                        pri.append(" ");
                        pri.append(amount.RandomFirst());
                        break;
                    case 3: pri.append(Function.get(3));
                        pri.append(" ");
                        pri.append(acc.get(accountRand.RandomFirst()));
                        pri.append(" ");
                        pri.append(amount.RandomFirst());
                        break;
                    case 4: pri.append(Function.get(4));
						break;
                    default: break;
				}
				commandsList.add(pri.toString());
			}
		}
		else
		{
			System.out.println("No accounts present for transfer");
		}
        
		return commandsList;
	}
}


