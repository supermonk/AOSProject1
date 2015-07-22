package client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import printme.Helper;

import automate.automated;

/**
 * The number of arguments passed to the input file is first validated.
 * If the validation passes, the arguments are parsed and the account number is verified.
 * If the account exists, a connection request is sent to the server and a reader and writer thread is created.
 * Now the client has established a connection with the server.
 * A helper document is displayed to help the users to enter in a correct input.
 * When a client receives an input asking for balance, the client retrieves the balance for the clients account
 * and provides it back to the client.
 * When a client receives an input asking to deposit a certain amount of money, the client adds the amount to the
 * current balance in the account, thus depositing the money into the account.
 * When a client receives an input asking to withdraw a certain amount of money, the client first checks if the 
 * balance will become less than zero and if it doesnot, the amount us subtracted from the users account.
 * When a client receives an input asking to transfer a certain amount of money to another account, the client first 
 * checks if the balance will become less than zero and if it doesnot and if the destination account is valid the amount 
 * us subtracted from the users account and a transfer message is sent to the other server and it updates its balance. 
 * An automatic tester is implemented which on activating generates a random set of inputs for testing.
 * Sctp working has been implemented by providing delimiters.
 *
 * @author Saranya Suresh
 * @author Narendra Bidari
 */

public class Client 
{
	private static final byte DELIMITER = '$';
	static DataOutputStream Rwriter;
	static Socket Rsock;
	static Helper helper = new Helper();
	static volatile boolean flag = false;
	String path="";

	static Properties log = new Properties();
	static final Logger logger = Logger.getLogger(Client.class); 

	public static InputStream setUpNetworking(String host, int port) 
	{
		InputStream Rreader = null;
		try 
		{
			Rsock = new Socket(host, port);
			Rreader = new DataInputStream(Rsock.getInputStream());
			Rwriter = new DataOutputStream(Rsock.getOutputStream());
			System.out.println("Client established connection");
			helper.Help();
		} 
		catch (IOException e) 
		{
			System.out.println("Server is not Started at host " + host +" and port number "+ port);
			System.out.println("Program Exited Sucessfully");
			try 
			{
				Rsock.close();
			} 
			catch (IOException ex) 
			{
				logger.warn("Socket closed");
			}
			System.exit(0);
		}
		return Rreader;
	}


	public static class IncomingReader implements Runnable 
	{
		InputStream abc = null;    
		public IncomingReader(InputStream rReader) 
		{
			this.abc = rReader;
		}
		@Override
		public void run() 
		{
			String message;
			try 
			{
				while (true) 
				{
					message = new String(nextMsg());   
					if(message.split(":")[0].compareToIgnoreCase("Invalid")==0)
					{
						helper.TransferError();
					}
					else if(message.split(":")[0].compareToIgnoreCase("print")==0)
					{
						String[] it = message.split(":");

						System.out.println("**************************************Snapshot****************************************"); 
						for(int i=1;i<it.length;i++)
						{
							String my_new_str = it[i].replaceAll(",", "   ");
							System.out.println(my_new_str); 
						}
						System.out.println("**************************************************************************************"); 
					}
					else
					{
						System.out.println("Message From Server : " + message);
					}
				}
			} 
			catch (Exception e) 
			{
				try 
				{
					Rsock.close();
					System.out.println("Server might have stopped responding.. closing the program");
					logger.warn("Server closed ... Closing client");
				} 
				catch (IOException ex) 
				{
					logger.error(ex);
				}
				finally
				{
					System.exit(0);
				}
			}
		}

		public byte[] nextMsg() throws IOException 
		{
			ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();
			int nextByte;
			while ((nextByte = abc.read()) != DELIMITER) 
			{
				if (nextByte == -1) 
				{ 
					if (messageBuffer.size() == 0) 
					{
						return null;
					} else 
					{ 
						throw new EOFException("Message without delimiter. ");
					}
				}
				messageBuffer.write(nextByte); 
			}
			return messageBuffer.toByteArray();
		}
	}

	public static class Write implements Runnable 
	{
		DataOutputStream out = null;
		String accountNumber ="";
		String path="";
		public Write(DataOutputStream out, String accountNumber,String path) 
		{
			this.out = out;
			this.accountNumber = accountNumber;
			this.path = path;
		}
		@Override
		public void run() 
		{
			try 
			{
				frameMsg("client:".getBytes(), out);
			} 
			catch (IOException e1) 
			{
				logger.warn("Error when retrieving client bytes. ");
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String xx;
			try 
			{
				frameMsg(accountNumber.getBytes(), out);
			} 
			catch (IOException e1) 
			{
				logger.warn("Error when retrieving message bytes");

			}
			while (true ) 
			{
				if(flag==false)
				{

					try 
					{
						xx = br.readLine();
						String inp[] = xx.split(" ");
						if(inp.length ==1)
						{
							if(inp[0].compareToIgnoreCase("Balance") == 0)
							{
								try
								{
									frameMsg(xx.getBytes(), out);
								}
								catch(Exception e)
								{
                                    logger.warn("Balance Error");
									helper.BalanceError();
								}
							}
							else if(inp[0].compareToIgnoreCase("Snapshot") == 0)
							{
								try
								{
									frameMsg(xx.getBytes(), out); 
								}
								catch(Exception e)
								{
                                    logger.warn("Snapshot Error");
									helper.SnapshotError();
								}
							}
							else if(inp[0].compareToIgnoreCase("Exit") == 0)
							{
								try
								{
									frameMsg(xx.getBytes(), out);
								}
								catch(Exception e)
								{
                                    logger.warn("Exit Error");
									helper.Help();
								}
							}
							else
								helper.Help();
						}
						else if(inp.length == 2)
						{
							if(inp[0].compareToIgnoreCase("Deposit") == 0)
							{
								Double dd =(double) 0;
								try 
								{
									dd=  Double.parseDouble(inp[1]);
									frameMsg(xx.getBytes(), out);
								}
								catch(Exception e)
								{
                                    logger.warn("Deposit Error. Input is "+dd);
									helper.DepositError();
								}
							}
							else if(inp[0].compareToIgnoreCase("Withdraw") == 0)
							{
								Double dd =(double) 0;
								try 
								{
									dd = Double.parseDouble(inp[1]);
									frameMsg(xx.getBytes(), out);
								}
								catch(Exception e)
								{
                                    logger.warn("Withdraw Error. Input is "+dd);
									helper.WithdrawError();
									
								}
							} 
							else if(inp[0].compareToIgnoreCase("GSnap") == 0)
							{
								flag = true;
								System.out.println("Automatic commands generations .. please wait  untill \'OVER Snapshots \' ");
								try
								
								{// 1 for simply
									ArrayList<String> comm =  automateSnapshot(accountNumber,(int)Integer.parseInt(inp[1]),1,path);
									for(int i=0;i<comm.size();i++)
									{
										
										frameMsg(comm.get(i).getBytes(), out);
										System.out.println("commad "+ i +" : "+comm.get(i));
										Thread.sleep(25000);

									}
									flag = false;
									System.out.println("\'OVER Snapshots \' ");
								}
								catch(Exception e)
								{
									helper.TestingError();;
									flag=false;
								}
								flag = false;
							}
							else
							{
								helper.Help();
							}
						}
						else if(inp.length == 3)
						{
							if(inp[0].compareToIgnoreCase("Transfer") == 0)
							{
								Double dd =(double) 0;
								try 
								{
									dd = Double.parseDouble(inp[2]);
									frameMsg(xx.getBytes(), out);
								}
								catch(Exception e)
								{
                                    logger.warn("Transfer Error. Input is "+dd);
									helper.TransferError();
									
								}
							}

							else if(inp[0].compareToIgnoreCase("GTrans") == 0)
							{flag = true;
							System.out.println("Automatic commands generations .. please wait  untill \'OVER Transfer \' ");
							try
							{
								ArrayList<String>comm = automatetransfer(accountNumber,(int)Integer.parseInt(inp[1]),(int)Integer.parseInt(inp[2]),path);

								for(int i=0;i<comm.size();i++)
								{	
									frameMsg(comm.get(i).getBytes(), out);
									System.out.println("commad "+ i +" : "+comm.get(i));
									Thread.sleep(1250);
									if(i%4 == 0)
										Thread.sleep(5000);

								}
								flag = false;
								System.out.println("\'OVER Transfer \' ");
							}
							catch(Exception e)
							{
								helper.TestingError();;
								flag = false;
							}
							flag = false;
							}
							else
							{
								helper.Help();

							}
						}
						else
						{
							helper.Help();
						}
					} 
					catch (IOException e) 
					{
						logger.error("Error in reading input.");
					}

				}else
				{
					logger.error("Automated process running");
				}
			}
		}

		public void frameMsg(byte[] message, DataOutputStream out) throws IOException 
		{
			for (byte b : message) 
			{
				if (b == DELIMITER) 
				{
					throw new IOException("Message contains delimiter.");
				}
			}
			out.write(message);
			out.write(DELIMITER);
			out.flush();
		}
	}

	public static ArrayList<String> automateSnapshot(String accNo, int count, int noOfServersActive, String path)
	{
		automated auto = new automated(accNo, count,noOfServersActive,path);
		ArrayList<String> commandsList = auto.returnSnapshotCommands();
        return commandsList;
	}
	
    public static ArrayList<String> automatetransfer (String accNo, int count, int noOfServersActive, String path)
	{
		automated auto = new automated(accNo, count,noOfServersActive,path);

		ArrayList<String> commandsListss = auto.returnTransferCommands();

		return commandsListss;
    }

	public static void main(String[] args) 
	{
		try
		{
			log.load(new FileInputStream("log4jClient.properties"));
			PropertyConfigurator.configure(log);
		}
		catch(Exception e)
		{
			logger.debug(" Error in logging" + args[1]);
		}
		
		if(args.length ==2)
		{
			File f = new File(args[1]);
			if(f.exists()) 
			{
				try
				{
					String accountNumber = args[0];
					String path = args[1];
					ConfigReader configReader = new ConfigReader();
					String connectionDetails = configReader.isAccount(path, accountNumber);
					if (connectionDetails.compareTo("invalid") != 0)
					{
						Thread readerThread = new Thread();
						Thread writerThread = new Thread();
						String[] conn = connectionDetails.split(":");
						InputStream xx = setUpNetworking(conn[0].trim(), Integer.parseInt(conn[1].trim()));
						readerThread = new Thread(new IncomingReader(xx));
						readerThread.start();
						writerThread = new Thread(new Write(Rwriter, accountNumber,path));
						writerThread.start();
					}
					else 
					{
						System.out.println("Wrong input. Account name is not present in config file");
					}
				}
				catch(Exception e)
				{
					System.out.println("might be Wrong Input Arguments / or socket connection unable to establish");
					System.out.println("Type 'java -jar <jar name> <Account Name> <configfile path>'");
					System.out.println(" (ex: java -jar BankingSystemClient.jar AB12CD ./BankingConfig.config ))");
					logger.warn("Wrong input arguments");
				}
			}
			else
			{
				System.out.println("Client : No config file @ "+args[0] );
				System.out.println("Type 'java -jar <jar name> <Account Name> <configfile path>'");
				System.out.println(" (ex: java -jar BankingSystemClient.jar AB12CD ./BankingConfig.config ))");
				logger.warn("Config file is not present.");
			}
		}
		else
		{
			System.out.println("Wrong number of arguments");
			System.out.println("Type 'java -jar <jar name> <Account Name> <configfile path>'");
			System.out.println(" (ex: java -jar BankingSystemClient.jar AB12CD ./BankingConfig.config ))");
			logger.warn("Wrong number of arguments");
		}   
	}
}