package vo;

/**
 * Contains getters for host requirements such as port and host name.
 * @author Narendra Bidari
 * @author Saranya Suresh
 * @version     %G%
 */

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class HostVO 
{
    
    static Properties log = new Properties();
    static final Logger logger = Logger.getLogger(HostVO.class);
    public HostVO()   {
        
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
            logger.warn("  Error in logging in host vo");
        }
    }
    
    /**
     * @return port	Getter for host port.
     */
    public int getPort() {
        return port;
    }
    
    /**
     * @param port	Sets the port for the host.
     */
    public void setPort(int port) {
        this.port = port;
    }
    
    /**
     * @return host	Getter for host name.
     */
    public String getHost() {
        return host;
    }
    
    /**
     * @param host	Sets the host name.
     */
    public void setHost(String host) {
        this.host = host;
    }
    
    private int port;
    private String host;
}