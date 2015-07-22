package server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import vo.AccountVO;
import vo.ServerVO;
import vo.VectorClockVO;

/**
 * Reads the configuration file and loads the servers.
 * It then checks if there are any previous servers and if so, it establishes connection with them to form a connected graph.
 * It also creats a hash map of the writers of every server it is connected to, so that it can communicate with them.
 * The server handler waits on the servers listening port and accepts new client connections and creates client specific reader and writer.
 * The all handler receives messages and then calls the manage inputs method to process the message.
 * The manage inputs checks if the message is from a client or server or if is for a snapshot process.
 * Depending on this result, the manage inputs transfers the meaage to tghe appropriate method.
 * The manage inputs server waits on the server reader and processes the incoming message from another server.
 * If the incoming message is for a transfer function, it then adds the amount to the balance and returns an acknowledgement.
 * If the incoming message is for a snapshot process, it then records its local state and sends marker message to all its neighbours.
 * Once it receives the snapshot of the other processes, it send it to the client.
 * The messages are marked as 'W' if they are sent before the snapshot starts and after the snapshot ends.
 * The messages are marked as 'R' if they are sent during the snapshot process.
 * In the manage inputs client method if the message recied is a balance, deposit or withdraw, the server performs the corresponding
 * action on the message and returns back the result.
 * Before it processes a withdraw action, it first checks if the balance will go below zero and if so the action is not performed
 * and the respective error message is displayed.
 * If the action to be performed is a transfer, it first checks if the destination account is valid or not. 
 * If it is valid, it decrements the amount from its account and then it searches through the hash map and finds the respective writer 
 * and writes to it.
 * If it receives a snapshot request, it updates its color to 'R' and takes a local snapshot, sets the snapshot in progress flag and
 * send the marker message on all its outgoing ports.
 * Once it receives the snapshot from all the processes, it send the data back to the client and resets all the flags.
 * The next message method reads the messages in the form of bytes and this takes care of message boundaries that is not part of the 
 * TCP protocol and it uses a delimiter to find the end of message which is the implementation of the sctp protocol to take care
 * of the message boundaries.
 * The write others method, writes the message on all the outgoing ports.
 * The write once method, writes the message on a specified port.
 *
 * @author Narendra Bidari
 * @author Saranya Suresh
 */



public class Server 
{
	static Properties log = new Properties();
	static final Logger logger = Logger.getLogger(Server.class);
	public Server() 
	{
		try
		{
			File directory = new File (".");
			logger.debug ("Current directory's absolute  path: " + directory.getAbsolutePath());
			File ff = new File("log4jServer.properties");
			logger.debug("file exists   " + ff.exists());
			log.load(new FileInputStream("log4jServer.properties"));
			PropertyConfigurator.configure(log);
		}
		catch(Exception e)
		{
			logger.debug(" Error in logging @ Server ");
		}
	}

	static int[] received = new int[100];
	static double[] sumStore = new double[100];
	static ArrayList<ServerVO> servers = new ArrayList<ServerVO>();
	private static final byte DELIMITER = '$';
	static HashMap<String, String> accountsMap = new HashMap<String, String>();
	static HashMap<String, String> hostsMap = new HashMap<String, String>();
	static String path="";

	// dealy for transfer
	RandomGen amount = new RandomGen(1000, 4000);

	public  class transferDelayThread implements Runnable 
	{
		ServerVO serverVO = null;
		AccountVO destaccount = null;
		String message= "";
		public transferDelayThread(ServerVO serverVO, AccountVO destaccount, String message)
		{
			
			this.serverVO = serverVO;
			this.destaccount = destaccount;
			this.message = message;
		}

		@Override
		public void run() {
			synchronized(this)
			{
				String[] input = message.split(":");
				try
				{
					System.out.println(" transfer  recieved"+ message);
					logger.debug("message @ transfer"+ message+" "+ serverVO.getColor());
					try 
					{
						Thread.sleep(1250);
						//System.out.println("Check @server7 ###################################"+ serverVO.getPort());
					} 
					catch (InterruptedException e)
					{
						//e.printStackTrace();
						System.out.println("error");
						//System.out.println("Check @server8 ###################################"+ serverVO.getPort());
					}
					//System.out.println("Chec9###################################"+ serverVO.getColor());
					//@vec  clock update
					int[] destVectorClock = serverVO.getVectorClock().decodePiggy(input[4]); 
					serverVO.getVectorClock().receiveAction(destVectorClock);
					//System.out.println("Recieved "+ input[4]+" sent "+ serverVO.getVectorClock().getVal());
					//System.out.println("Check @server9################################### "+ serverVO.getPort());




					//System.out.println("Check @server10################################### "+ serverVO.getPort());
					//logger.debug("c" + c + " "+ input[5].toCharArray()[0] );

					System.out.println("Check @server6################################### "+ serverVO.getPort()+"before" + serverVO.getColor() + "coming"+ input[5].toCharArray()[0]);

					if(serverVO.getColor() =='R' && input[5].toCharArray()[0]=='W')
					{

						if(serverVO.isOwner())
						{
							//System.out.println("Check *********************************@server 11"+ serverVO.getPort());
							serverVO.updateGlobalstate("TransitMessageto"+input[1] +" "+input[2]);
							//System.out.println("transfer run");
							//System.out.println("Check @server 12"+ serverVO.getPort());
						}
						else
						{
							//System.out.println("Check *********************************"+ serverVO.getPort());
							if(serverVO.getCountMarkers()!=0 && serverVO.getCountMarkers()!=serverVO.getConnectedMap().size())
							{
								serverVO.updateIntransit("TransitMessageto"+input[1] +" "+input[2]);
							}
							else
							{
								System.out.println("wing");
							}


							System.out.println("transfer run");
							//System.out.println("Check @server 12"+ serverVO.getPort());
						}

					}
					else
					{
						System.out.println(" "+ message);
					}
					//System.out.println("Check @server 13"+ serverVO.getPort());
					destaccount.addMoney(Double.parseDouble(input[2]));
					//DataOutputStream returnwrite = serverVO.getConnectedMap().get(serverVO.getHostLocalName()+":"+input[3]);
					//System.out.println("Check @server14 "+ serverVO.getPort());
					//writeOnce("client:ackk", returnwrite);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}




	public  class AllHandler implements Runnable 
	{
		DataInputStream allReader = null;
		DataOutputStream allWriter = null;
		Socket sock = null;
		double sum = 0;
		int count = 0;
		int senderMachine = 0;
		ServerVO serverVO = null;

		public AllHandler(Socket clientSocket, ServerVO serverVO) 
		{
			try 
			{
				this.serverVO = serverVO;
				sock = clientSocket;
				InputStream isReader = sock.getInputStream();
				OutputStream isWriter = sock.getOutputStream();
				allReader = new DataInputStream(isReader);
				allWriter = new DataOutputStream(isWriter);
			} 
			catch (Exception e) 
			{
				logger.debug(" Error in server socket @ AllHandler ");
			}
		}
		@Override
		public void run() 
		{
			try 
			{
				String message = new String(nextMsg(allReader));
				logger.debug(message);
				manageInputs(allReader, allWriter, sock, serverVO, message, "fromAll");
			} 
			catch (Exception e) 
			{
				logger.debug(" Socket Close  @ All Handler");
			}
			finally
			{
				try 
				{
					sock.close();
				} 
				catch (IOException ex) 
				{    
				}
			}
		}
	}

	/**
	 * @param reader	Contains the input data stream.
	 * @return messageBuffer.toByteArray()		Returns a byte array of the message.
	 * @throws IOException
	 * 
	 */

	public static byte[] nextMsg(DataInputStream reader) 
	{
		ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();
		int nextByte;
		try 
		{
			while ((nextByte = reader.read()) != DELIMITER) 
			{
				if (nextByte == -1) 
				{
					if (messageBuffer.size() == 0) 
					{
						return null;
					} 
					else 
					{
						throw new EOFException("Non-empty message without delimiter");
					}
				}
				messageBuffer.write(nextByte);
			}
		} 
		catch (EOFException e) 
		{
			e.printStackTrace();
			logger.debug(" EOF occured @ nextMsg");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			logger.debug(" IO error occured @ nextMsg");
		}
		return messageBuffer.toByteArray();
	}

	public  class ServerHandler implements Runnable 
	{
		ServerVO serverVO = null;

		public ServerHandler(ServerVO serverVO) 
		{
			this.serverVO = serverVO;
		}
		@Override
		public void run() 
		{
			ServerSocket serverSock = null;
			try 
			{
				serverSock = new ServerSocket(this.serverVO.getPort());
			} 
			catch (IOException e) 
			{
				logger.debug(" Error in creating server socket @ ServerHandler");
			}
			Thread[] readerThread = new Thread[100];
			int k = 0;
			while (true) 
			{
				Socket clientSocket = null;
				try 
				{
					clientSocket = serverSock.accept();
					readerThread[k] = new Thread(new AllHandler(clientSocket, serverVO));
					readerThread[k].start();
					k++;
				} 
				catch (IOException e) 
				{
					logger.debug(" Socket accepting error from input @ Server Handler for port " + this.serverVO.getPort());
				}
			}
		}
	}

	static public void printHashMap(HashMap<String, DataOutputStream> h) 
	{
		Set<Entry<String, DataOutputStream>> set = h.entrySet();
		Iterator<Entry<String, DataOutputStream>> i = set.iterator();
		while (i.hasNext()) 
		{
			//Entry<String, DataOutputStream> me = i.next();
			//////logger.debug("Mapping the key " + me.getKey() + " with value " + me.getValue());
		}
	}

	public  class IncomingReader implements Runnable 
	{
		DataInputStream reader = null;
		DataOutputStream writer = null;
		Socket sock = null;
		ServerVO serverVO = null;

		public IncomingReader(DataInputStream inreader, DataOutputStream inWriter, Socket sock, ServerVO serverVO) 
		{
			this.reader = inreader;
			this.writer = inWriter;
			this.sock = sock;
			this.serverVO = serverVO;
		}
		@Override
		public void run() 
		{
			try 
			{
				manageInputs(reader, writer, sock, serverVO, "server", "fromIn");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				String kk ="";
				try 
				{
					sock.close();
					System.out.println("Server socket closed between " + InetAddress.getLocalHost().getHostName()+ " : " + sock.getLocalPort()+  " and "+ sock.getRemoteSocketAddress());
					kk=InetAddress.getLocalHost().getHostName();

					logger.debug("Server socket closed between " + InetAddress.getLocalHost().getHostName()+ " : " + sock.getLocalPort()+  " and "+ sock.getRemoteSocketAddress());
				} 
				catch (IOException ex) 
				{
					logger.debug("Catch in closing Server socket  " + kk+ " : " + sock.getLocalPort()+  " and "+ sock.getRemoteSocketAddress());
				}
			}
		}
	}

	public  void manageInputs(DataInputStream allReader, DataOutputStream allWriter, Socket sock, ServerVO serverVO, String message, String fromwhere) 
	{
		String[] dd = message.split(":");
		//System.out.println("Check1");
		if (dd[0].compareTo("server") == 0) 
		{      //System.out.println("Check2");   
			manageInputsServer( allReader,  allWriter,  sock,  serverVO,  message,  fromwhere,  dd);
		} 
		else if (dd[0].compareTo("client") == 0) 
		{
			//System.out.println("Check3");
			manageInputsClients( allReader,  allWriter,  sock,  serverVO,  fromwhere,dd) ;
		} 
		else 
		{
			//System.out.println("Check4");
			System.out.println("Not a valid client.");
			logger.debug("Not a valid client @ ManageInputs.");
		}
	}














































































	public  void manageInputsServer(DataInputStream allReader, DataOutputStream allWriter, Socket sock, ServerVO serverVO, String message, String fromwhere, String[] dd)
	{

		DataInputStream serverReader = allReader;
		DataOutputStream serverWriter = allWriter;
		if (fromwhere.compareTo("fromIn") != 0) 
		{
			synchronized (this)
			{
				serverVO.getVectorClock().setVal(serverVO.getVectorClock().expand(serverVO.getVectorClock().getVal().length+1));
				InetAddress addy = sock.getInetAddress();
				HashMap<String, DataOutputStream> connectedMap = serverVO.getConnectedMap();
				connectedMap.put(addy.getHostName() + ":" + dd[1], serverWriter);
				serverVO.updateMarkers(addy.getHostName()+" "+dd[1]);
				serverVO.setConnectedMap(connectedMap);
			}
		}

		while (true ) 
		{
			//System.out.println("Check @server1 "+ serverVO.getPort());

			message = new String(nextMsg(serverReader));
			//System.out.println("Check @server2 "+ serverVO.getPort());
			synchronized (this) 
			{
				//System.out.println("Check @server3 "+ serverVO.getPort());
				//("message"+ message);
				logger.debug("@ server" + message);
				String[] input = message.split(":");
				if (input[0].compareTo("transfer") == 0) 
				{
					//System.out.println("Check @server5 "+ serverVO.getPort());

					//logger.debug(" @ server "+ serverVO.getPort()+" message is " + message);
					Validate validate = new Validate();
					AccountVO destaccount = new AccountVO();
					destaccount = validate.isValid(serverVO, input[1]);

					//logger.debug(serverVO.getVectorClock().printVector());
					if (destaccount.isValid() != false) 
					{
						Thread  transfer = new Thread(new transferDelayThread(serverVO, destaccount , message));
						transfer.run();
					} 
					else 
					{
						logger.debug("Invalid account.");
					}

				} 
				else  if(input.length > 1 && input[1].compareTo("ackk") == 0)
				{
					logger.debug("completed");
				}
				// fist triggering
				else  if(input.length > 0 && input[0].compareTo("R")==0 && serverVO.getColor()=='W' )
				{//System.out.println("Check @server15 "+ serverVO.getPort());
					synchronized (serverVO) 
					{  //System.out.println("Check @server16 "+ serverVO.getPort());
						serverVO.setColor('R');
						serverVO.recordLocalState(serverVO.getVectorClock().getVal());
						serverVO.setCountMarkers(serverVO.getConnectedMap().size()-1);
						//System.out.println(" "+ message + "  ");
						writeOthers(serverVO.getColor()+":"+input[2]+":"+serverVO.getHostLocalName()+":"+serverVO.getPort()+":"+serverVO.getLocalState()+":"+serverVO.getVectorClock()+":"+input[6], serverVO.getConnectedMap());
						//System.out.println("**********"+input[6]+"*************:");
						serverVO.setLocalSnapshotRecorded(false);
					}


				}
				else  if(input.length > 0 && input[0].compareTo("R")==0 && serverVO.getColor()=='R'  )
				{
					//System.out.println("Check @server17 "+ serverVO.getPort());
					synchronized (this) 
					{//System.out.println("Check @server 18"+ serverVO.getPort());
						if( serverVO.isOwner())
						{//System.out.println("Check @server 19"+ message);

							if(serverVO.getConnectedMap().containsKey(input[2]+":"+ input[3]))
							{  

								//System.out.println("Check @server 20"+ serverVO.getPort());
								//System.out.println("4" + input[4]);
								if(input[4].compareTo("")!=0)
								{
									serverVO.updateGlobalstate(input[4]);
								}

								serverVO.decCountMarkers();
								if(serverVO.getCountMarkers() ==0)
								{ //System.out.println("Check @serve21r "+ serverVO.getPort());
									System.out.println("Recieved all markers "+ serverVO.getHostLocalName()+":"+serverVO.getPort());

									serverVO.setInProgressSnapshot(false);
									serverVO.setCountMarkers(serverVO.getConnectedMap().size());
									serverVO.setColor('W');
									serverVO.setOwner(false);
									//serverVO.printGlobalSnapshot();
									serverVO.setOwner(false);
									//System.out.println();
								}
								else
								{
									System.out.println("Check @server 22"+ serverVO.getPort());
									//System.out.println("else" + serverVO.getPort() + "  "+ serverVO.getCountMarkers());
								}
							}
							else
							{
								System.out.println("Check @server23 "+ serverVO.getPort());
								logger.error("Error "+message +"  ::"+input[2]+" "+ input[3]+"   "+input[4]+ ""+ serverVO.getCountMarkers());
							}
							//System.out.println("Check @server 24"+ serverVO.getPort());

						}
						else
						{//System.out.println("Check @server25 "+ serverVO.getPort());

							serverVO.decCountMarkers();
							if(serverVO.getCountMarkers() ==0)
							{
								//System.out.println("Check @server26 "+ serverVO.getPort());

								///
								//System.out.println(" in transit ************"+ serverVO.getIntransit()+"*******************");
								//System.out.println("message @ transit" + input[6] );
								String[] det =input[6].split(",");
								DataOutputStream it = serverVO.searchConnectedMap(det[0]+":"+det[1]);

								writeOnce(serverVO.getColor()+":"+input[2]+":"+serverVO.getHostLocalName()+":"+serverVO.getPort()+":"+serverVO.getIntransit()+":"+serverVO.getVectorClock()+":"+input[6], it);


								//writeOnce("Balance " + useraccount.getAmount()+" < " + "Withdraw amount " + input[2], it); 
								////
								serverVO.clearIntransit();
								serverVO.setColor('W');
								//System.out.println("Check @server "+ serverVO.getPort());
								serverVO.setCountMarkers(serverVO.getConnectedMap().size()-1);
								//System.out.println("Check @server27 "+ serverVO.getPort());
							}
							else
							{
								logger.info("count " + serverVO.getPort() + "  "+ serverVO.getCountMarkers());
								System.out.println("Check @server 28"+ serverVO.getPort());
							}
						}//System.out.println("Check @server29 "+ serverVO.getPort());
						//System.out.println("Check @server 30"+ serverVO.getPort());
					}//System.out.println("Check @server40 "+ serverVO.getPort());
				}

				else
				{
					//System.out.println("Check @server41 "+ serverVO.getPort());
					logger.debug("Wrong command "+ message+" port: "+ serverVO.getPort());
				}


			}
		}
	}

	public  void manageInputsClients(DataInputStream allReader, DataOutputStream allWriter,  Socket sock, ServerVO serverVO,  String fromwhere, String[] dd) 
	{
		DataInputStream clientReader = allReader;
		DataOutputStream clientWriter = allWriter;
		AccountVO useraccount = new AccountVO();
		String message = new String(nextMsg(clientReader));
		Validate validate = new Validate();
		useraccount = validate.isValid(serverVO, message);
		boolean sockset = true;
		if (useraccount.isValid()) 
		{
			writeOnce("Account " + useraccount.getAccountNumber() + "  Exists from server", clientWriter);
			while (true && sockset) 
			{   
				System.out.print("Good with "+ serverVO.getColor()+"  ");
				message = new String(nextMsg(clientReader));
				if(serverVO.isInProgressSnapshot() && serverVO.isOwner() && message.equalsIgnoreCase("Snapshot")  ) 				
				{
					//System.out.println("Check @Client 1"+ serverVO.getPort());
					logger.debug("Snapshot is in progress, kindly wait..");
					writeOnce("Snapshot is in progress, kindly wait..", clientWriter);
				}
				else
				{
					//System.out.println("Check @Client2 "+ serverVO.getPort());
					String[] input = message.split(" ");
					// @vec send action
					serverVO.getVectorClock().sendAction();
					if (input[0].compareToIgnoreCase("Balance") == 0) 
					{
						writeOnce("Balance of account" + useraccount.getAmount(), clientWriter);
					} 
					else if (input[0].compareToIgnoreCase("Deposit") == 0 && input.length == 2 ) 
					{
						useraccount.addMoney(Double.parseDouble(input[1]));
						writeOnce("New Balance of account" + useraccount.getAmount(), clientWriter);
					} 
					else if (input[0].compareToIgnoreCase("Withdraw") == 0 && input.length == 2 ) 
					{
						if(useraccount.getAmount() >= Double.parseDouble(input[1]))
						{
							useraccount.subMoney(Double.parseDouble(input[1]));
							writeOnce("New Balance of account" + useraccount.getAmount(), clientWriter);
						}
						else
						{
							writeOnce("Balance " + useraccount.getAmount()+" < " + "Withdraw amount " + input[1], clientWriter);
						}
					} 
					else if (input[0].compareToIgnoreCase("Transfer") == 0 && input.length == 3 && serverVO.getColor()=='W') 
					{//System.out.println("Check @Client 5"+ serverVO.getPort());
						synchronized (this) 
						{//System.out.println("Check @Clien6t "+ serverVO.getPort());
							logger.debug("Transfer message"+ message+ " "+ input.length);
							try {//System.out.println("Check @Client7 "+ serverVO.getPort());
								if(useraccount.getAccountNumber().compareToIgnoreCase(input[1])==0)
								{//System.out.println("Check @Client 8"+ serverVO.getPort());
									writeOnce("Source account " + useraccount.getAccountNumber()+" same as Destination account " +input[1]+ " is Not Allowed", clientWriter);
								}
								else
								{//System.out.println("Check @Client9 "+ serverVO.getPort());
									accountsMap = serverVO.getAccountsMap();
									ConfigServerReader configReader = new ConfigServerReader();
									String connectionDetails = configReader.isAccount(path, input[1]);
									if(connectionDetails.compareTo("invalid") == 0) 
									{
										writeOnce("Invalid:" +input[1] + " Account is Invalid", clientWriter); 
									}	
									else
									{
										if(connectionDetails.split(":")[0].compareToIgnoreCase(serverVO.getHostLocalName())==0 && Integer.parseInt(connectionDetails.split(":")[1])==serverVO.getPort())
										{
											AccountVO dest =  serverVO.getAccount(input[1]);
											if(dest== null)
											{
												writeOnce("Account fetching error : Balance is " + useraccount.getAmount(), clientWriter);
											}
											else
											{
												if(useraccount.getAmount() < Double.parseDouble(input[2]))
												{ 
													writeOnce("Transfer not possible :Balance " + useraccount.getAmount()+" < " + "Withdraw request amount " + input[2], clientWriter); 
												}
												else
												{
													useraccount.subMoney(Double.parseDouble(input[2]));
													dest.addMoney(Double.parseDouble(input[2]));
													writeOnce("Balance " + useraccount.getAmount(), clientWriter);
												}
											}

										}
										else
										{
											//System.out.println("Check @Client10 "+ serverVO.getPort());
											DataOutputStream it = serverVO.searchConnectedMap(connectionDetails);
											if(useraccount.getAmount() < Double.parseDouble(input[2]))
											{ 
												//System.out.println("Check @Client11 "+ serverVO.getPort());
												writeOnce("Balance " + useraccount.getAmount()+" < " + "Withdraw amount " + input[2], clientWriter); 
											}
											else
											{
												try 
												{
													//System.out.println("Check @Client 12"+ serverVO.getPort());
													useraccount.subMoney(Double.parseDouble(input[2]));
													String bb = serverVO.getVectorClock().returnForPiggy();
													writeOnce(("transfer:" + input[1] + ":" + input[2] + ":" + serverVO.getPort()+":"+bb+":"+serverVO.getColor()), (DataOutputStream) it);
													writeOnce("Balance " + useraccount.getAmount(), clientWriter);
												} 
												catch (Exception e) 
												{
													//System.out.println("Check @Client 13"+ serverVO.getPort());
													logger.error(" Error in sending transfer information."+ ("transfer:" + input[1] + ":" + input[2] + ":" + serverVO.getPort()+":"+serverVO.getVectorClock().returnForPiggy() +":"+serverVO.getColor()));
												}
											}
										}
									}
								}
							} 
							catch (Exception e) 
							{
								//System.out.println("Check @Client 14"+ serverVO.getPort());
								writeOnce("Invalid : Balance " + useraccount.getAmount(), clientWriter);
								logger.debug(" Error in connection details.");
							}

						}
					}
					else if (message.equalsIgnoreCase("Snapshot") ) 
					{

						if(serverVO.getColor()=='W')
						{
							//System.out.println("Check @Client15 "+ serverVO.getPort());

							serverVO.setOwner(true);
							serverVO.setColor('R');
							//System.out.println("Check @Client 16"+ serverVO.getPort());
							serverVO.recordLocalState(serverVO.getVectorClock().getVal());
							serverVO.setLocalSnapshotRecorded(true);
							//System.out.println("Check @Client17 "+ serverVO.getPort());
							serverVO.updateGlobalstate(serverVO.getLocalState());
							serverVO.setInProgressSnapshot(true);
							if(serverVO.getConnectedMap().size()>0)
							{
								//System.out.println("Check @Client 18"+ serverVO.getPort());
								writeOthers(serverVO.getColor()+":"+serverVO.getHostLocalName()+":"+serverVO.getHostLocalName()+":"+serverVO.getPort()+":e"+":e:"+serverVO.getHostLocalName()+","+serverVO.getPort(), serverVO.getConnectedMap());
								serverVO.setCountMarkers(2*serverVO.getConnectedMap().size());
								boolean bbb = serverVO.isInProgressSnapshot();
								while( bbb == true)
								{
									try 
									{
										Thread.sleep(30);
									} 
									catch (InterruptedException e) 
									{
										//System.out.println("Check @Client 19"+ serverVO.getPort());
										logger.error("Problem in thread sleep @ manageInputsClients");
									}
									bbb = serverVO.isInProgressSnapshot();
								}
							}
							else
							{
								//System.out.println("Check @Client 21"+ serverVO.getPort());
								System.out.println("Only one server");

							}
							//System.out.println("Check @Client22 "+ serverVO.getPort());
							serverVO.setColor('W');
							writeOnce("print:"+serverVO.prepareGlobalSnapshot(), clientWriter);
							serverVO.clearGlobalState();
							serverVO.setOwner(false);
							//System.out.println("Check @Client23 "+ serverVO.getPort());
							serverVO.setInProgressSnapshot(false);
							serverVO.setLocalSnapshotRecorded(false);

							//System.out.println("Check @Client 24"+ serverVO.getPort());

						}
						else
						{
							//System.out.println("Check @Client25 "+ serverVO.getPort());
							System.out.println("Snapshot shot in progress .. kindly try again");
						}

					}
					else if (input[0].compareToIgnoreCase("Transfer") == 0 && input.length == 3 && serverVO.getColor()=='R') 
					{
						System.out.println("Snapshot shot in progress .. kindly try again");
					}
					else if (message.equalsIgnoreCase("Exit")) 
					{
						//logger.debug("Thank You.");
						try 
						{
							sockset = false;
							writeOnce("closed: "+useraccount.getAccountNumber(), clientWriter);
							sock.close();

							logger.debug("Server socket closed between  "+ InetAddress.getLocalHost().getHostName()+ " : " + sock.getLocalPort()+  " and client "+ sock.getRemoteSocketAddress());
							System.out.println("Server socket closed between  "+ InetAddress.getLocalHost().getHostName()+ " : " + sock.getLocalPort()+  " and client "+ sock.getRemoteSocketAddress());
						} 
						catch (IOException ex) 
						{   
							System.out.println("Warning in closing the socket");
						}
					}//System.out.println("Check @Client 26"+ serverVO.getPort());
				}//System.out.println("Check @Client27 "+ serverVO.getPort());
			}
		}
		else 
		{
			logger.debug("Account is not present.");
		}
	}

	public  void writeOthers(String message, HashMap<String, DataOutputStream> serverStream) 
	{
		Set<Entry<String, DataOutputStream>> set = serverStream.entrySet();
		Iterator<Entry<String, DataOutputStream>> it = set.iterator();
		while (it.hasNext()) 
		{
			Entry<String, DataOutputStream> me = it.next();
			try 
			{
				frameMsg(message.getBytes(), (DataOutputStream) me.getValue());
			} 
			catch (IOException e) 
			{
				//logger.debug(" Error in sending message @ writeOthers.");
			}
		}
	}

	public  void writeOnce(String message, DataOutputStream it) 
	{
		try 
		{
			frameMsg(message.getBytes(), it);
		} 
		catch (Exception e) 
		{
			//logger.debug(" Error in sending message @ writeOnce");
		}
	}

	public static void frameMsg(byte[] message, DataOutputStream out) throws IOException 
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

	public void runme(String args)
	{
		try
		{
			ConfigServerReader configReader = new ConfigServerReader();
			path = args;
			servers = configReader.loadServers(args);
			if(servers != null)
			{
				Thread[] serverThread = new Thread[100];
				Thread[] otherServerThread = new Thread[100];
				for (int i = 0; i < servers.size(); i++) 
				{
					ArrayList<String> previousServers = servers.get(i).getpreviousServers();
					int k = 0;
					ServerVO serverVO = servers.get(i);
					servers.get(i).setProcessId(previousServers.size());
					VectorClockVO vectorClockVO = new VectorClockVO((previousServers.size()+1), serverVO.getProcessId());
					servers.get(i).setVectorClock(vectorClockVO);
					for (int j = 0; j < previousServers.size(); j++) 
					{
						String[] add = previousServers.get(j).split(":");
						Socket Rsock = null;
						InputStream Rreader = null;
						DataOutputStream Rwriter = null;
						try {
							Rsock = new Socket(add[0].trim(), Integer.parseInt(add[1].trim()));
							Rreader = new DataInputStream(Rsock.getInputStream());
							Rwriter = new DataOutputStream(Rsock.getOutputStream());
							writeOnce("server:" + serverVO.getPort(), Rwriter);
							HashMap<String, DataOutputStream> connectedMap = serverVO.getConnectedMap();
							connectedMap.put(add[0].trim() + ":" + add[1].trim(), Rwriter);
							serverVO.updateMarkers(add[0].trim() +" "+ add[1].trim());
							serverVO.setConnectedMap(connectedMap);
						} 
						catch (IOException e) 
						{
							logger.debug(" IO error occured. or Order missing. host1 host2 .... @ runme");
						}
						DataInputStream abc = new DataInputStream(Rreader);
						otherServerThread[k] = new Thread(new IncomingReader(abc, Rwriter, Rsock, serverVO));
						otherServerThread[k].start();
						k++;
					}
					serverThread[i] = new Thread(new ServerHandler(servers.get(i)));
					serverThread[i].start();
				}
			}
		}
		catch(Exception e)
		{
			logger.debug("Abrupt Ending");
		}
	}
}

