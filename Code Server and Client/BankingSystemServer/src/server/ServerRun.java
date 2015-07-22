package server;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
/**
 * This method validates the input arguments and triggers the server program
 *
 * @author Saranya Suresh
 * @author Narendra Bidari
 */

public class ServerRun 
{
	
	static Properties log = new Properties();
    static final Logger logger = Logger.getLogger(ServerRun.class); 
    
   
    
    public static void main(String[] args)
    {
        try
        {
        	File directory = new File (".");
        	System.out.println ("Current directory's absolute  path: " + directory.getAbsolutePath());
        	File ff = new File("log4jServer.properties");
        	System.out.println("file exists   " + ff.exists());
        	log.load(new FileInputStream("log4jServer.properties"));
        	PropertyConfigurator.configure(log);
        	//BasicConfigurator.configure();
        	
           
            
            
        }
        catch(Exception e)
        {
            logger.error("Error in logging");
            System.out.println(" Error in logging");
        }
        if(args.length == 1)
        {
        	
            File f = new File(args[0]);
            if(f.exists()) 
            {
                Server server = new Server();
                System.out.println("Server Started" );
                try
                {
                    server.runme(args[0]);
                }
                catch(Exception e)
                {
                    logger.warn("Invalid input args.");
                    System.out.println("Wrong Input Arguments");
                    System.out.println("Type 'java -jar <jar name> <configfile path>' -- (ex: java -jar BankingSystemServer.jar ./BankingConfig.config ))");
                }
            }
            else
            {
                System.out.println("Server : No config file @ "+args[0] );
            }
        }
        else
        {
            System.out.println("Wrong number of arguments");
            System.out.println("Type 'java -jar <jar name> <configfile path>'  -- (ex: java -jar BankingSystemServer.jar ./BankingConfig.config))");
        }
        // server.runme("/Users/narendrabidari/Documents/Net Beans/BankingSystemServer/src/server/BankingConfigFile.config");
    }
}
