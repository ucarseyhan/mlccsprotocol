package lifiReceiver;


public class TestLifiReceiver 
{
	public static void main(String[] args) 
	{
		try 
		{
			//Start lifi receiver on default port number
			LifiReceiver lifiReceiver = new LifiReceiver();
			lifiReceiver.start();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
