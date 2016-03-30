package lifiTransmitter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.keyczar.Crypter;

import model.Constant;
import model.ExitPacket;
import model.HelloPacket;
import model.Packet;
/**
 * LifiTransmitter is the class that represents the 
 * pure Lifi Li-1st device. It has start method which 
 * starts the communication process.
 * 
 * @author seyhan
 *
 */
public class LifiTransmitter
{
	//Attributes
	private int IRPortNumber 		  = 5005;
	private int VLCReceiverPortNumber = 5004;
	/**
	 * Default Constructor
	 */
	public LifiTransmitter()
	{

	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	public void start() 
	{
		DatagramSocket socketVLCTransmitter;
		/**
		 * Change dimming level before packet transmission
		 */
		changeDimming();
		////////////////////////////////////////////////////
		for (int i = 0; i < Constant.NUMBER_OF_PACKET; i++) 
		{
			try 
			{
				//////////////////////////////////////////////////////////////////////////////////////////////////
				/**
				 * Infra-red Communication part
				 */
				
				byte[] incomingIRData = new byte[1024];
				Packet packetIR = null;
				DatagramSocket socketIRReceiver = new DatagramSocket(IRPortNumber);
				//System.out.println("IR is starting on port number:"+IRPortNumber);
				while(true)
				{
					DatagramPacket incomingPacket = new DatagramPacket(incomingIRData, incomingIRData.length);
					socketIRReceiver.receive(incomingPacket);
					byte[] data = incomingPacket.getData();
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					ObjectInputStream is = new ObjectInputStream(in);
					//Packet contains the KEY
					packetIR = (Packet)is.readObject();
					break;
				}
				socketIRReceiver.close();
				//Get the current key and write to file for encryption
				File keysFile = new File(Constant.KEY_LOCATION);
				FileWriter keysFileWriter = new FileWriter(keysFile, false); 
				keysFileWriter.write(packetIR.getKey());
				keysFileWriter.close();
				
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////
				socketVLCTransmitter = new DatagramSocket();
				InetAddress IPAddress = InetAddress.getByName(Constant.RECEIVER_IP);
				Packet packetVLC = null;
				
				NTPUDPClient timeClient = new NTPUDPClient();
				InetAddress inetAddress = InetAddress.getByName(Constant.TRANSMITTER_IP);
				TimeInfo timeInfo = timeClient.getTime(inetAddress);
				long transmitterDate = timeInfo.getReturnTime();
				////////////////////////////////////////////////////////////////////////////////////////////////////////////
				if(i == Constant.NUMBER_OF_PACKET) packetVLC = new ExitPacket();
				//Set transmit time
				else packetVLC =  new HelloPacket(transmitterDate, 0, i, Constant.HELLO_PACKET);
				//=========================================================================================================
				/**
				 * Generate the random string and hide the 
				 * message on the encrypted message
				 */
				String message = generateRandomString();
				String secureString = encryptMessage(message);
				//String secureString = message;
				packetVLC.setMessage(secureString);
				//========================================================================================================
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(outputStream);
				os.writeObject(packetVLC);
				byte[] data = outputStream.toByteArray();
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, VLCReceiverPortNumber);
				socketVLCTransmitter.send(sendPacket);
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				System.out.println("Sequence:"+i+" Message:"+message+" Encrypted:"+secureString+"\n");
				Thread.sleep(1000);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * changeDimming is used for connecting the Ceiling Unit and change
	 * the dim level to static value.
	 */
	public  void changeDimming() 
	{
		try 
		{
			/**
			 * Important point to mention
			 */
			/**Absolute path is needed.*/ //For error: 2 no file found exception.
			/**If any chmod u+x lifictl*/ //For error: 13 permission denied.
			/**lifictl -l5 192.168.0.1 : set dimming level to 5 on 192.168.0.1*/
			String dimLevel = "-l9";
			String[] command = new String[]{"/home/seyhan/workspaceSE/Transmitter/lifictl",dimLevel, Constant.LOCAL_IP};
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			try 
			{
				while ((line = br.readLine()) != null) 
				{
					System.out.println(line);
				}
			} catch (Exception ex) 
			{
				ex.printStackTrace();
			}
			br.close();
			//System.out.println("Li-1st Device dimming level is set to: "+dimLevel);

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Generate a String message that represents the 
	 * @return
	 */
	public String generateRandomString()
	{
		String message = null;
		try 
		{
			message = UUID.randomUUID().toString().substring(0,10);
			String newMessage = UUID.randomUUID().toString();
			message = message+newMessage;
			message = message+newMessage;
			message = message+newMessage;
//			message = message+newMessage;
			//System.out.println("Message = " + message);	
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return message;
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Encrypt the message based on previous step received keys 
	 * and returned to the user.
	 * @param message
	 * @return
	 */
	public String encryptMessage(String message)
	{
		String ciphertext = null;
		try 
		{
			Crypter crypter = new Crypter("keys");
			ciphertext = crypter.encrypt(message);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return ciphertext;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





}
