package model;

import java.io.Serializable;
/**
 * System Packet structure
 * 
 * @author seyhan
 *
 */
public class Packet implements Serializable,Transmittable 
{

	private static final long serialVersionUID = 1L;
	//Key string attribute
	private String key = "";
	private String message = "";
	//Attributes
	private long transmitTime;
	private long receiveTime;
	private int sequenceNumber;
	private int packetType;

	
	
	public Packet() 
	{
	}
	/**
	 * Specialized Packet 
	 * 
	 * @param transmitTime
	 * @param receiveTime
	 * @param sequenceNumber
	 * @param packetType
	 */
	public Packet(long transmitTime, long receiveTime, int sequenceNumber, int packetType) 
	{
		super();
		this.transmitTime = transmitTime;
		this.receiveTime = receiveTime;
		this.sequenceNumber = sequenceNumber;
		this.packetType = packetType;
	}

	/**
	 * @param transmitTime the transmitTime to set
	 */
	public void setTransmitTime(long transmitTime) 
	{
		this.transmitTime = transmitTime;
	}

	/**
	 * @param receiveTime the receiveTime to set
	 */
	public void setReceiveTime(long receiveTime) 
	{
		this.receiveTime = receiveTime;
	}

	/**
	 * @param sequenceNumber the sequenceNumber to set
	 */
	public void setSequenceNumber(int sequenceNumber) 
	{
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * @param packetType the packetType to set
	 */
	public void setPacketType(int packetType) 
	{
		this.packetType = packetType;
	}

	
	
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public long getTransmitTime() 
	{
		return transmitTime;
	}
	@Override
	public long getReceiveTime() 
	{
		return receiveTime;
	}
	@Override
	public int getSequenceNumber() 
	{
		return sequenceNumber;
	}
	@Override
	public int getPacketType() 
	{
		return packetType;
	}
	@Override
	public int getPacketSize() 
	{
		return 0;
	}
	


}
