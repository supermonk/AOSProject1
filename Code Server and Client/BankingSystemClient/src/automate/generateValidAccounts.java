package automate;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Generates a list of valid accounts.
 */
public class generateValidAccounts 
{
	static Properties log = new Properties();
	static final Logger logger = Logger.getLogger(generateValidAccounts.class);
	public generateValidAccounts() 
	{
		try
		{
			log.load(new FileInputStream("log4jClient.properties"));
			PropertyConfigurator.configure(log);
		}
		catch(Exception e)
		{
			logger.error("Error in logging.in valid accoutns");
		}
	}

	/**
	 * Parses the configuration file and takes into account the number of servers and the accounts 
	 * per server and returns a list of the valid accounts.
	 */
	public ArrayList<String> returnAccounts(String path,int num)
	{   
		Properties props = new Properties();
		FileInputStream fis;
		ArrayList<String> accounts = new ArrayList<String>();
		int[] noOfServers= new int[100];
		try 
		{
			fis = new FileInputStream(path);
			props.load(fis);
			for(int f=1;f<=num;f++)
			{
				noOfServers[f] = Integer.parseInt(props.getProperty("noOfServersforHost"+f)); 
				int[] noOfAccountsServer = new int[100];
				int accSum =0;
				for(int k=1;k<=noOfServers[f];k++)
				{
					noOfAccountsServer[k] = Integer.parseInt(props.getProperty("noOfAccountsforHost"+f+"Server"+k));
					accSum += noOfAccountsServer[k];
				}
				if(accSum > Integer.parseInt(props.getProperty("maxAccounts")) )
				{
				}
				else
				{
					for(int z=1;z<=accSum;z++)
					{
						accounts.add(props.getProperty("host"+f+"accountNumber"+z));                        
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.debug(("Error in automation."));
		}
		return accounts;
	}


}
