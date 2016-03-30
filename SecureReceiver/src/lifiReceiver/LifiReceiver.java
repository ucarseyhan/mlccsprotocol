package lifiReceiver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import org.keyczar.Crypter;
import model.Constant;
import model.ExitPacket;
import model.HelloPacket;
import model.NTPDate;
import model.Packet;
/*****************************************************************************
 * LifiReceiver is the code that receiver connected PC should run.
 * The Desktop Unit (DU) is waiting data from Ceiling Unit via specified
 * port number. When the data is received then necessary actions are taken such
 * as decoding the packet and writing the data to 
 * 
 * @author seyhan
 *
 *******************************************************************************/
public class LifiReceiver 
{
	private int portNumber = 5004;
	private int IRReceiverPortNumber = 5005;

	public LifiReceiver()
	{
	}

	public LifiReceiver(int port)
	{
		this.portNumber = port;
	}

	/**
	 * @return the portNumber
	 */
	public int getPortNumber() 
	{
		return portNumber;
	}

	/**
	 * @param portNumber the portNumber to set
	 */
	public void setPortNumber(int portNumber) 
	{
		this.portNumber = portNumber;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	public void start()
	{
		byte[] incomingVLCData = new byte[1024];
		try 
		{
			DatagramSocket socketIRTransmitter =  new DatagramSocket();
			DatagramSocket socketVLCReceiver = new DatagramSocket(portNumber);
			while(true)
			{
				//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				Thread.sleep(1000);
				
				/**
				 * Infra-red part Transmit Key
				 */
				
				InetAddress IPAddress = InetAddress.getByName(Constant.TRANSMITTER_IP);
				Packet packetIR = new Packet();
				File keyFile = new File(Constant.KEY_LOCATION);
				Scanner scanner = new Scanner(keyFile);
				String key = scanner.nextLine();
				packetIR.setKey(key);
				scanner.close();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(outputStream);
				os.writeObject(packetIR);
				byte[] dataIR = outputStream.toByteArray();
				DatagramPacket sendPacket = new DatagramPacket(dataIR, dataIR.length, IPAddress, IRReceiverPortNumber);
				socketIRTransmitter.send(sendPacket);
				
				////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				/**
				 * VLC  Receive
				 */
				DatagramPacket incomingPacket = new DatagramPacket(incomingVLCData, incomingVLCData.length);
				socketVLCReceiver.receive(incomingPacket);
				byte[] data = incomingPacket.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				Packet packet = (Packet)is.readObject();
				int length = incomingPacket.getLength();
				decodePacket(packet,length);
				if(packet instanceof ExitPacket) break;
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			}
			//Close the socket.
			socketVLCReceiver.close();
			socketIRTransmitter.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/************************************************************
	 * decodePacket is used for decoding packet content and getting 
	 * the packet informations such that;
	 * -packet sequence number
	 * -packet transmit time
	 * packet receive time
	 * 
	 * @param packet
	 ************************************************************/
	private void decodePacket(Packet packet,int length)
	{
		try 
		{
			if(packet != null)
			{
				//Received packet is Hello Packet
				if(packet instanceof HelloPacket)
				{
					HelloPacket helloPacket = (HelloPacket)packet;

					//Decrypt the coming message.
					Crypter crypter = new Crypter("keys");
					String messageDectypted = crypter.decrypt(helloPacket.getMessage());
					//String messageDectypted = helloPacket.getMessage();
					
					/////////////////////////////////////////////////////////////////////////
					long start  = System.currentTimeMillis();
					long transmitterDate = new NTPDate().getNTPDate();
					long end = System.currentTimeMillis();
					helloPacket.setReceiveTime(transmitterDate);
					/////////////////////////////////////////////////////////////////////////
					
					
					System.out.println(helloPacket.getSequenceNumber()+" "+helloPacket.getTransmitTime()+" "+helloPacket.getReceiveTime()+" "+(end-start)+" "+messageDectypted+" "+length);
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////



}
