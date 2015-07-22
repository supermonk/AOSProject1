package server;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import vo.AccountVO;
import vo.ServerVO;


/**
 * This extracts all the details available in the configuration file.
 * It validates the configuration file for the number of accounts.
 * Arrays for Servers in a particular host and the number of accounts for the server are created.
 * It links each host with its account and each account with the initial balance.
 * @author Narendra Bidari
 * @author Saranya Suresh
 */

public class ConfigServerReader 
{
	static Properties log = new Properties();
	static final Logger logger = Logger.getLogger(ConfigServerReader.class);
	public ConfigServerReader() 
	{
		try
		{
			log.load(new FileInputStream("log4jServer.properties"));
			PropertyConfigurator.configure(log);
			logger.debug("adsfas");
		}
		catch(Exception e)
		{
			System.out.println(" Error in logging");
			logger.error("Error in logging.");
		}
	}

	public ArrayList<ServerVO> loadServers(String args) 
	{
		ArrayList<ServerVO> servers = new ArrayList<ServerVO>();
		HashMap<String, String> accountsMap = new HashMap<String, String>();
		HashMap<String, String> hostsMap = new HashMap<String, String>();
		Properties props = new Properties();
		File filer = new File(args);
		if(filer.exists())
		{
			FileInputStream fis;
			try 
			{
				fis = new FileInputStream(args);
				props.load(fis);
				int e = 1;
				boolean valid = false;
				while (valid == false && e <= 50) 
				{
					if (InetAddress.getLocalHost().getHostName().compareTo(props.getProperty("host" + e)) == 0) 
					{
						valid = true;
					} 
					else 
					{
						e++;
					}
				}
				if (valid) 
				{
					try 
					{
						int i = 1, hostnumber = e;
						int[] noOfServers = new int[100];
						for (int f = 1; f <= e; f++) 
						{
							noOfServers[f] = Integer.parseInt(props.getProperty("noOfServersforHost" + f));
						}
						int[] noOfAccountsServer = new int[100];
						for (int f = 1; f <= noOfServers[e]; f++) 
						{
							noOfAccountsServer[f] = Integer.parseInt(props.getProperty("noOfAccountsforHost" + hostnumber + "Server" + f));    
						}
						int s = 1;
						while (s <= Integer.parseInt(props.getProperty("maxHosts"))) 
						{
							int x = 1;
							while (x <= Integer.parseInt(props.getProperty("maxAccounts"))) 
							{
								accountsMap.put("host" + s + "accountNumber" + x, props.getProperty("host" + s + "accountNumber" + x));
								x++;
							}
							s++;
						}
						int y = 1;
						while (y <= Integer.parseInt(props.getProperty("maxHosts"))) 
						{
							hostsMap.put("host" + y, props.getProperty("host" + y));
							y++;
						}
						while (i <= noOfServers[hostnumber]) 
						{
							ServerVO server = new ServerVO();
							server.setPort(Integer.parseInt(props.getProperty("port" + i)));
							server.setHostName("host" + e);
							server.setHostLocalName(props.getProperty("host" + e));
							ArrayList<AccountVO> accounts = new ArrayList<AccountVO>();
							ArrayList<String> previousServers = new ArrayList<String>();
							String hostNumber = "";
							hostNumber = "host" + hostnumber;
							int prev = 0;
							for (int gg = 1; gg < i; gg++) 
							{
								prev = prev + noOfAccountsServer[gg];
							}
							for (int j = 1; j <= noOfAccountsServer[i]; j++) 
							{
								AccountVO account = new AccountVO();
								account.setAccountNumber(props.getProperty(hostNumber + "accountNumber" + (prev + j)));
								account.setAmount(Double.parseDouble(props.getProperty(hostNumber + "amount" + (prev + j))));
								account.setHost(props.getProperty(hostNumber)+"-server"+i);
								accounts.add(account);
							}
							//System.out.println( " " );
							for (int k = 1; k < hostnumber; k++) 
							{
								for (int z = 1; z <= noOfServers[k]; z++) 
								{
									previousServers.add(props.getProperty("host" + k) + ":" + props.getProperty("port" + z));
								}
							}
							for (int x = 1; x < i; x++) 
							{
								previousServers.add(props.getProperty("host" + (hostnumber)) + ":" + props.getProperty("port" + x));
							}
							logger.debug(server.getHostLocalName() + " :: "+ previousServers.size());
							server.setAccountsMap(accountsMap);
							server.setHostsMap(hostsMap);
							server.setpreviousServers(previousServers);
							server.setAccounts(accounts);
							servers.add(server);
							i++;
						}
					} 
					catch (Exception sdfs) 
					{
						System.out.println("Error : sufficient accounts not present. Update config file with account details.");
						logger.warn("Update config file with account details.");
					}
				} 
				else 
				{
					System.out.println("Not on the valid hosts. kindly run on Net Machines / UTD");
				}
				fis.close();
			} 
			catch (Exception e) 
			{
				logger.debug("Error in parsing config file.");
				logger.warn("Error in parsing config file.");
			}
			return servers;
		}
		else
		{
			System.out.println("No config file @"+args );
			return null;
		}
	}

	/**
	 * Checks where the account is present and if it is present it returns the host and port of that account.
	 */
	public String isAccount(String fileName, String argument) 
	{
		Properties props = new Properties();
		FileInputStream fis = null;
		try 
		{
			fis = new FileInputStream(fileName);
			props.load(fis);
			int hostNumber = 1;
			int maxHosts = Integer.parseInt(props.getProperty("maxHosts"));
			boolean valid = false;
			for (; hostNumber <= maxHosts && valid == false; hostNumber++) 
			{
				int accountNumber = 1;
				int accountMax = Integer.parseInt(props.getProperty("maxAccounts"));
				for (; accountNumber <= accountMax && valid == false; accountNumber++) 
				{
					if (props.getProperty("host" + hostNumber + "accountNumber" + accountNumber).compareTo(argument) == 0) 
					{
						valid = true;
						int accounts = 0;
						int z = 1;
						for (; accounts < accountNumber; z++) 
						{
							accounts = accounts + Integer.parseInt(props.getProperty("noOfAccountsforHost" + hostNumber + "Server" + z));
						}
						String hostName = props.getProperty("host" + hostNumber);
						int portNumber = Integer.parseInt(props.getProperty("port" + (z - 1)));
						return hostName + ":" + portNumber;
					}
				}
			}
			fis.close();
		} 
		catch (Exception e) 
		{
			System.out.println("Error in validating account.");
			logger.warn("Error in account validation.");
			return "invalid1";
		}
		return "invalid";
	}
}