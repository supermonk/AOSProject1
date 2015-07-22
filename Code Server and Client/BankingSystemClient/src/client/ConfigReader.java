package client;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import vo.HostVO;

public class ConfigReader 
{
    static Properties log = new Properties();
    static final Logger logger = Logger.getLogger(ConfigReader.class);
    
    public ConfigReader() 
    {
        try
        {
            log.load(new FileInputStream("log4jClient.properties"));
            PropertyConfigurator.configure(log);
        }
        catch(Exception e)
        {
            logger.error("Error during logging @ ConfigReader.");
        }
    }
    
    /**
     * Creates an array of hosts.
     */
    public ArrayList<HostVO> loadHosts(String args) 
    {
        ArrayList<HostVO> hosts = new ArrayList<HostVO>();
        Properties props = new Properties();
        FileInputStream fis = null;
        try 
        {
            fis = new FileInputStream(args);
            props.load(fis);
            int noOfHosts = Integer.parseInt(props.getProperty("noOfHosts"));
            for (int j = 1; j <= noOfHosts; j++) 
            {
                HostVO hostVO = new HostVO();
                hostVO.setHost(props.getProperty("host" + j));
                hostVO.setPort(Integer.parseInt(props.getProperty("port")));
                hosts.add(hostVO);
            }
            fis.close();
        } 
        catch (Exception e) 
        {
            logger.debug("Error in parsing file" + fis);
        }
        return hosts;
    }
    
    /**
     * Checks on which server the account is present and returns the server name along with the port number.  
     */
    public String isAccount(String fileName, String argument) 
    {
        Properties props = new Properties();
        File f = new File(fileName);
        if(f.exists()) 
        {
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
                    logger.debug("Error in accessing file: "+fis);
                    return "invalid";
                }
            }
            else
            {
                System.out.println("No config file : "+fileName );
                return "invalid";
            }
            return "invalid";
    }
}