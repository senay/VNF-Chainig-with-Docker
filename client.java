import java.io.*;
import java.net.*;



class client
{
	public void sendBestRoute(pathAndDistance pD, int port)
	{
		try{
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName("localhost");
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(pD.path);
			byte [] message = bos.toByteArray();

			DatagramPacket sendPacket = new DatagramPacket(message, message.length, IPAddress, port);
			clientSocket.send(sendPacket);
			System.out.println("Message sent to the car: " + pD.path);

			clientSocket.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
