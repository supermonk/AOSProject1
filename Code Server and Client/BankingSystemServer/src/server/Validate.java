package server;

import java.io.FileInputStream;
import java.util.ArrayList;
import vo.AccountVO;
import vo.ServerVO;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Checks if the account is present or not.
 */
public class Validate 
{
    static Properties log = new Properties();
    static final Logger logger = Logger.getLogger(Validate.class);
    public Validate() 
    {
        try
        {
            log.load(new FileInputStream("log4jServer.properties"));
            PropertyConfigurator.configure(log);
        }
        catch(Exception e)
        {
            logger.error("Error in logging");
            System.out.println(" Error in logging");
        }
    }
    public AccountVO isValid(ServerVO servers, String message) 
    {
        boolean flag = false;
        AccountVO account = new AccountVO();
        account.setValid(false);
        ArrayList<AccountVO> accounts = servers.getAccounts();
        for (int j = 0; j < accounts.size() && (flag == false); j++) 
        {
            account = accounts.get(j);
            if (account.getAccountNumber().compareTo(message) == 0 && (flag == false)) 
            {
                account.setValid(true);
                flag = true;
            }
        }
        return account;
    }
}