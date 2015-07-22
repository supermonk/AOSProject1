package vo;

/**
 * Contains getters and setters for servers.
 * @author Narendra
 * @author Saranya
 */

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ServerVO 
{

	public String getIntransit() {
		return intransit;
	}
	public void clearIntransit() {
		this.intransit="";
	}
	public void updateIntransit(String str) {

		synchronized (this) {
			if (this.intransit.length()>1)
			{

				this.intransit= this.intransit+" "+str;
			}else
			{
				this.intransit+=str;
			}
		}
	}

	public void setIntransit(String intransit) {
		this.intransit = intransit;
	}
	static Properties log = new Properties();
	static final Logger logger = Logger.getLogger(ServerVO.class);
	ReentrantLock lock = new ReentrantLock();
	ReentrantLock getlock = new ReentrantLock();
	public ServerVO()  
	{

		/**
		 * Log4j
		 */
		try
		{
			log.load(new FileInputStream("log4jServer.properties"));
			PropertyConfigurator.configure(log);
		}
		catch(Exception e)
		{
			System.out.println(" Error in logging in server vo");
		}
	}

	/**
	 * Getter to obtain the list of all the previous servers that are up and running.
	 */
	public ArrayList<String> getpreviousServers() {
		return this.previousServers;
	}

	/**
	 * @param Sets the arraylist of the previous servers with the current value.
	 */
	public void setpreviousServers(ArrayList<String> previousServers) {
		this.previousServers = previousServers;
	}

	/**
	 * Gets the port number for the server.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port	Sets the port number for the server.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns the list of accounts for the server.
	 */
	public ArrayList<AccountVO> getAccounts() {
		synchronized (this) {
			return accounts;
		}
	}


	public AccountVO getAccount(String accno) 
	{
		for(int i=0;i<this.getAccounts().size();i++)
		{
			if(this.getAccounts().get(i).getAccountNumber().compareToIgnoreCase(accno)==0)
			{
				return this.getAccounts().get(i);
			}
		}
		System.out.println("error");
		return null;
	}


	/**
	 * @param accounts	Sets all the accounts for the server.
	 */
	public void setAccounts(ArrayList<AccountVO> accounts) {
		this.accounts = accounts;
	}

	/**
	 * Returns the server name.
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName	Set the server name.
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Returns the hash map for the accounts.
	 */
	public HashMap<String, String> getAccountsMap() {
		return accountsMap;
	}

	/**
	 * @param accountsMap	Sets the accounts hash map.
	 */
	public void setAccountsMap(HashMap<String, String> accountsMap) {
		this.accountsMap = accountsMap;
	}

	/**
	 * Returns the hash map for the hosts.
	 */
	public HashMap<String, String> getHostsMap() {
		return hostsMap;
	}

	/**
	 * @param hostsMap	Sets the hosts hash map.
	 */
	public void setHostsMap(HashMap<String, String> hostsMap) {
		this.hostsMap = hostsMap;
	}

	/**
	 * Returns the host name of the server.
	 */
	public String getHostLocalName() {
		return hostLocalName;
	}

	/**
	 * @param hostLocalName	Sets the host name for a server.
	 */
	public void setHostLocalName(String hostLocalName) {
		this.hostLocalName = hostLocalName;
	}

	/**
	 * Returns a map that contains the output stream for every connection.
	 */
	public HashMap<String, DataOutputStream> getConnectedMap() {
		return connectedMap;
	}

	/**
	 * @param key	Searches the map using the key and returns the output stream.
	 */
	public DataOutputStream searchConnectedMap(String key) {
		return this.connectedMap.get(key);

	}

	/**
	 * @param connectedMap	Sets the map with the key and output stream.
	 */
	public void setConnectedMap(HashMap<String, DataOutputStream> connectedMap) {

		this.connectedMap = connectedMap;

	}

	/**
	 * Returns the processes ID.
	 */
	public int getProcessId() {
		return processId;
	}

	/**
	 * @param processId	 Sets the processID for a process.
	 */
	public void setProcessId(int processId) {
		this.processId = processId;
	}

	/**
	 * Records the local state of the system by getting its account number and balance.
	 */
	public void recordLocalState(int[] vec)
	{
		synchronized (this) {
			StringBuilder xx = new StringBuilder();
			for(int i=0;i<this.getAccounts().size();)
			{
				AccountVO accountVO = this.getAccounts().get(i);
				xx.append(accountVO.getAccountNumber());
				xx.append(" ");
				xx.append(accountVO.getAmount());
				xx.append(" ");
				accountVO.setVec(vec);
				//System.out.println(accountVO.getHost()+" "+ vec);
				xx.append(accountVO.getVec());
				xx.append(" ");
				xx.append(accountVO.getHost());
				i++;
				if(i<this.getAccounts().size())
				{
					xx.append(" ");
				}

			}

			this.setLocalState(xx.toString());
		}

	}

	/**
	 * Returns the current value of the vector clock.
	 */
	public VectorClockVO getVectorClock() {
		synchronized (this) {
			return vectorClock;
		}
	}

	/**
	 * Gets the local state of the system.
	 */
	public String getLocalState() {
		return localState;
	}

	/**
	 * @param localState	 Sets the local state of the system with the current value.
	 */
	public void setLocalState(String localState) {
		this.localState = localState;
	}

	/**
	 * @param vectorClock	 Sets the vector clock with the current value.
	 */
	public void setVectorClock(VectorClockVO vectorClock) {
		this.vectorClock = vectorClock;
	}

	/**
	 * Returs a boolean telling if a snapshot has been recorded or not.
	 */
	public boolean isLocalSnapshotRecorded() {
		return localSnapshotRecorded;
	}

	/**
	 * @param localSnapshotRecorded	 Sets a boolean saying that the snapshot has been recorded.
	 */
	public void setLocalSnapshotRecorded(boolean localSnapshotRecorded) {
		this.localSnapshotRecorded = localSnapshotRecorded;
	}

	/**
	 * Returns the snapshot markers.
	 */
	public ArrayList<String> getMarkers() {
		return this.markers;
	}

	/**
	 * @param inp	 Adds the marker to a markers list.
	 */
	public void updateMarkers(String inp)
	{
		ArrayList<String> markers = this.getMarkers();
		markers.add(inp);
		this.setMarkers(markers);
		//System.out.println("length of new makers" + this.getMarkers().size());
	}

	/**
	 * @param inp	 Removes the marker from the markers list.
	 */
	public void removeMark(String inp)
	{
		ArrayList<String> markers = this.getMarkers();
		markers.remove(inp);
		this.setMarkers(markers);
		//System.out.println("length of new makers" + this.getMarkers().size());
	}

	/**
	 * @param inp	 Sets the markers.
	 */
	public void setMarkers(ArrayList<String> markers) {

		this.markers = markers;
	}

	/*
	 * Prints the global snapshot of the system.
	 */
	public void printGlobalSnapshot()
	{
		System.out.println("*********************************** ");
		ArrayList<AccountVO> globalstate = this.getGlobalstate();
		Double bankMoney = 0.0;
		for(int i=0;i<globalstate.size();i++)
		{
			AccountVO account = new AccountVO();
			account=globalstate.get(i);
			System.out.println("AccountNumber : "+account.getAccountNumber() +"  Amount : "+account.getAmount()+"  Host  "+account.getHost()+"  VectorClock  "+account.getVec());
			bankMoney+=account.getAmount();
		}
		System.out.println("*******************************");
		System.out.println("Bank total Money is : " +bankMoney);
		System.out.println("*******************************");
	}

	/**
	 * @param inp	 Adds the marker to a markers list.
	 */
	public String prepareGlobalSnapshot()
	{
		synchronized (this) {
			StringBuffer sd = new StringBuffer();
			ArrayList<AccountVO> acc = this.getGlobalstate();
			double  bankMoney = 0.0;
			LinkedHashMap<String, String> xx = new LinkedHashMap<String, String>();
			for(int i=0;i<globalstate.size();i++)
			{
				AccountVO account = new AccountVO();
				account = acc.get(i);
				sd.append("AccountNumber  "+account.getAccountNumber() +"  Amount  "+account.getAmount()+":");
				bankMoney+=account.getAmount();
				if(account.getVec().compareToIgnoreCase("skip")!=0)
				{
					if(!xx.containsKey(account.getHost().toUpperCase()))
					{
						xx.put(account.getHost().toUpperCase(), account.getVec());
					}
				}

			}
			sd.append("#######################################################################################:");
			sd.append("#######Bank total Money is  " +bankMoney+" ###################################################:");
			sd.append("#######################################################################################:");
			sd.append("****Global Clock is  ******************************************************************:");

			for (Map.Entry<String, String> entry : xx.entrySet())
			{
				sd.append("VectorClock of "+entry.getKey()+" is  "+entry.getValue()+":");
				//System.out.println(entry.getKey() + "/" + entry.getValue());
			}
			return sd.toString();
		}

	}

	/**
	 * A boolean return that tells if a snapshot is in progress or not.
	 */
	public boolean isInProgressSnapshot() {
		return inProgressSnapshot;
	}

	/**
	 * @param inProgressSnapshot	 Sets the boolean when a snapshot starts or ends.
	 */
	public void setInProgressSnapshot(boolean inProgressSnapshot) {

		this.inProgressSnapshot = inProgressSnapshot;

	}

	/**
	 * @param Returns an array list that contains the global snapshot.
	 */
	public ArrayList<AccountVO> getGlobalstate() {
		Collections.sort(globalstate);
		return globalstate;
	}

	/**
	 * @param globalstate Adds the global state of a system to an arraylist.
	 */
	public void setGlobalstate(ArrayList<AccountVO> globalstate) {
		Collections.sort(globalstate);
		this.globalstate = globalstate;

	}

	/**
	 * @param mes 
	 */
	public void updateGlobalstate(String mes)
	{
		synchronized (this) {
			
				ArrayList<AccountVO> acc = this.getGlobalstate();

				String[] lol = mes.split(" ");

				//System.out.println("#####"+ mes);
				logger.debug("val #################### " + mes + lol.length);

				if(lol[0].contains("TransitMessageto"))
				{
					System.out.println("only transit");
					for(int k=0;k<lol.length;k=k+2)
					{

						AccountVO account= new AccountVO();
						account.setAccountNumber(lol[0]);
						account.setAmount(Double.parseDouble(lol[1]));
						account.setVec("skip");
						acc.add(account);
					}
				}
				else
				{
					if(lol.length==2)
					{
						AccountVO account= new AccountVO();
						account.setAccountNumber(lol[0]);
						account.setAmount(Double.parseDouble(lol[1]));
						account.setVec("skip");
						acc.add(account);
					}
					else
					{
						for(int k=0;k<lol.length;k=k+4)
						{
							AccountVO account= new AccountVO();
							account.setAccountNumber(lol[k]);
							account.setAmount(Double.parseDouble(lol[k+1]));
							account.setVec(lol[k+2]);
							account.setHost(lol[k+3]);
							acc.add(account);
						}
					}
				}



				Collections.sort(acc);
				this.setGlobalstate(acc);   
			

		}

	}

	/**
	 * @param Copies an array list of strings to another list and returns the new list.
	 */
	public ArrayList<String> arrayListCopy()
	{
		ArrayList<String> newArr = new ArrayList<String>();
		Iterator<String> itr = this.getMarkers().iterator();
		while(itr.hasNext())
		{
			newArr.add((String)itr.next());
		}
		return newArr;
	}

	/**
	 * Returns a boolean to check if the owner initiated the process or not.
	 */
	public boolean isOwner() {
		return owner;
	}

	/**
	 * @param sets a boolean to true to find who initiated the process.
	 */
	public void setOwner(boolean owner) {

		this.owner = owner;


	}

	/**
	 * Clears the global state to get it ready for another snapshot. 
	 */
	public void clearGlobalState() {
		this.getGlobalstate().clear();
	}

	public char getColor() {

		return color;


	}

	public void setColor(char color) {
		lock.lock();
		{
			this.color = color;
		}
		lock.unlock();

	}
	public int getCountMarkers() {
		return countMarkers;
	}
	public void decCountMarkers()
	{

		int count = this.getCountMarkers()-1;
		this.setCountMarkers(count);

	}

	public void setCountMarkers(int countMarkers) {
		this.countMarkers = countMarkers;
	}
	private int port;
	private volatile ArrayList<AccountVO> accounts = new ArrayList<AccountVO>();
	private ArrayList<String> previousServers = new ArrayList<String>();
	private HashMap<String, String> accountsMap = new HashMap<String, String>();
	private HashMap<String, String> hostsMap = new HashMap<String, String>();
	private HashMap<String, DataOutputStream> connectedMap = new HashMap<String, DataOutputStream>();
	private String hostName = "";
	private String hostLocalName = "";
	private int processId=0;
	private volatile VectorClockVO vectorClock ;
	private volatile String localState = "";
	private volatile boolean localSnapshotRecorded = false;
	private ArrayList<String> markers = new ArrayList<String>();
	private boolean owner = false;
	private volatile boolean inProgressSnapshot = false;
	private volatile ArrayList<AccountVO> globalstate = new  ArrayList<AccountVO>();
	private volatile char color = 'W';
	private volatile int countMarkers = 0;
	private volatile String intransit ="";

	// private LocalStateVO localStateVO = new LocalStateVO();

}