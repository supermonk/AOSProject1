package vo;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * @author Saranya Suresh
 * @author Narendra Bidari
 */


import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class VectorClockVO 
{
    public int[] v;
    int N;
    int myId;
    static Properties log = new Properties();
    static final Logger logger = Logger.getLogger(ServerVO.class);
    
    public VectorClockVO(int numProc, int myId) 
    {  synchronized (this) 
    	{
        this.myId = myId;
        this.N = numProc;
        this.v = new int [numProc];
        for(int i =0;i<this.N;i++)
        {
            this.v[i] =0;
        }
        this.v[this.myId] =1;
        
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
            logger.warn("  Error in logging in vector vo");
        }
    }
    }
    
    /**
     * Increments its own vector clock.
     */
    public void tick()
    {
    	synchronized (this) 
    	{
        this.v[this.myId]++;
    	}
    }
    
    /**
     * Increments its own vector clock.
     */
    public void sendAction()
    {
        this.v[this.myId]++;
    }
    
    /**
     * @param sentValue   Takes the max of the vector clock  and then increments its own.
     */
    public void receiveAction (int[] sentValue)
    {
    	synchronized (this) 
    	{
        for(int i =0;i<this.N;i++)
        {
            this.v[i] =Math.max(this.v[i], sentValue[i]);
        }
        this.v[this.myId]++;
    	}
    }
    
    /**
     * @param i Returns the vector clock value of its own process.
     */
    public int getValue(int i)
    {
        return this.v[i];
    }
    
    /**
     * Expands the size of the vector clock when the number of servers increase.
     */
    public int[] expand( int size) 
    {
    	synchronized (this) 
    	{
        int[] temp = this.getVal();
        int[] ret = new int[size];
        System.arraycopy(temp, 0, ret, 0, temp.length);
        for(int j = temp.length; j < size; j++)
            ret[j] = 0;
        this.v = ret;
        this.N = size;
        return ret;
    	}
    }
    
    /**
     * Returns the vector clock.
     */
    public int[] getVal() {
        return v;
    }
    
    /**
     * Sets the value of the vector clock.
     */
    public void setVal(int[] v) {
        this.v = v;
    }
    
    /**
     * Returns the entire vector clock.
     */
    public String printVector()
    {
        String ret ="[ ";
        for(int i=0; i<this.N;i++)
        {
            ret += this.v[i]+" ";
        }
        ret += "]";
        return ret;
    }
    
    /**
     * Returns the vector clock for piggy backing.
     */
    public String returnForPiggy()
    {
    	
        String ret ="";
        for(int i=0; i<this.N;i++)
        {
            ret += this.v[i]+" ";
        }
        return ret;
    }
    
    /**
     * Decodes the vector clock received from piggybacking.
     */
    public int[] decodePiggy(String st)
    {
        String[] bb = st.split(" ");
        int[] val = new int[bb.length];
        for(int i=0;i<bb.length;i++)
        {
            val[i] = Integer.parseInt(bb[i]);
        }
        return val;
    }
    
    public int[] convertToVector(String str)
    {
        String[] arrSt = str.split(" ");
        int[] arr = new int[arrSt.length];
        int i=0;
        while(i<arr.length)
        {
            arr[i] = Integer.parseInt(arrSt[i]);
        }
        return arr;
    }
    
    public boolean accept (int[] n1)
    {
    	synchronized (this) 
    	{
        int[] n = this.getVal();
        int j=0;
        if(n.length == n1.length)
        {
            while(j<n.length && (n[j]-n1[j]>=0))
            {
                j++;
            }
        }
        else
        {
          logger.warn("Vector clock input error");
        }
        System.out.println(j==n.length);
        return (j==n.length);
    }
    }
}
