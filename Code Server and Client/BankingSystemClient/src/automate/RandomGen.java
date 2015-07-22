package automate;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class RandomGen 
{
    static Properties log = new Properties();
    static final Logger logger = Logger.getLogger(RandomGen.class);
    public RandomGen() 
    {
        try
        {
            log.load(new FileInputStream("log4jClient.properties"));
            PropertyConfigurator.configure(log);
        }
        catch(Exception e)
        {
            logger.debug(" Error in logging in Random generations");
        }
    }
    
    /**
     * Getters and setters for random generation
     */
    
    public long getFraction() 
    {
		return fraction;
    }
    
    public void setFraction(long fraction) 
    {
		this.fraction = fraction;
    }
    
    public int getRandom() 
    {
		return random;
    }
	
    public void setRandom(int random) 
    {
		this.random = random;
    }
	
    public static Random getaRandom() 
    {
		return aRandom;
    }
	
    public static void setaRandom(Random aRandom) 
    {
		RandomGen.aRandom = aRandom;
    }
	
    public long getRange() 
    {
		return range;
    }
	
    public void setRange(long range) 
    {
		this.range = range;	
    }
	
    static Random aRandom = new Random();
	
    public int getMax() 
    {
		return max;
    }
	
    public void setMax(int max) 
    {
		this.max = max;
    }
	
    public int getMin() 
    {
		return min;
    }
	
    public void setMin(int min) 
    {
		this.min = min;
    }
	
    private int max = 0;
    private int min =0;
    private long range =0;
    private long fraction =0;
    private int random =0;
	
    public  RandomGen(int aStart, int aEnd)
    {
        if ( aStart > aEnd ) 
        {
            System.out.println("Start cannot exceed end" + aStart +" : "+aEnd);
        }
        else
        {
            this.max = aStart;
            this.min = aEnd;
            this.range = (long)this.max - (long)this.min + 1;
            
        }
    }
	
    public int  RandomFirst()
    {
	    
        this.fraction = (long)(range * aRandom.nextDouble());
        this.setRandom((int)(fraction + this.min));    
        return this.getRandom();
    }
}
