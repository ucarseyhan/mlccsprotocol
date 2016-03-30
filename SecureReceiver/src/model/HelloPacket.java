package model;
/**
 * System Hello Packet
 * 
 * @author seyhan
 *
 */
public class HelloPacket extends Packet 
{
	private static final long serialVersionUID = 1L;
	private long ntpDelayDifference;
	
	/**
	 * Default COnstructor.
	 */
	public HelloPacket()
	{
		super();
	}

	/**
	 * Parameterized COnstructor
	 * @param transmitTime
	 * @param receiveTime
	 * @param sequenceNumber
	 * @param packetType
	 */
	public HelloPacket(long transmitTime, long receiveTime, int sequenceNumber, int packetType) 
	{
		super(transmitTime, receiveTime, sequenceNumber, packetType);
	}

	/**
	 * @return the ntpDelayDifference
	 */
	public long getNtpDelayDifference() {
		return ntpDelayDifference;
	}

	/**
	 * @param ntpDelayDifference the ntpDelayDifference to set
	 */
	public void setNtpDelayDifference(long ntpDelayDifference) {
		this.ntpDelayDifference = ntpDelayDifference;
	}
	
	

	
	
	

}
