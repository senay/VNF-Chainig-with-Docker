import java.io.*;
import java.net.*;
import org.jgrapht.*;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.*;
import org.jgrapht.Graph.*;
import org.jgrapht.Graphs.*;


import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import javax.swing.*;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random ;
import java.util.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.net.InetAddress;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ByteOrder;

import java.util.concurrent.TimeUnit;
 

public class alltogether
{	DatagramSocket datagramSocket = null;
	List<DefaultWeightedEdge> edgeList ;
	DijkstraShortestPath path;
	int port = 4000;	
	byte[] raw;
	byte[] receiveData = new byte[1024];
	byte[] sendData = new byte[2048];

	public alltogether(){
		try
		{
			datagramSocket = new DatagramSocket(port);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		while(true){
			try{
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		  		datagramSocket.receive(receivePacket);

				ClientServiceThread cliThread = new ClientServiceThread(receivePacket);
		        	cliThread.start(); 
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}			
	}

	public static void main (String[] args) 
	{ 
		new alltogether();        
	}

	class ClientServiceThread extends Thread{
		DatagramPacket receivePacketThread;

		public ClientServiceThread() 
		{ 
			super(); 
		} 
	 
		ClientServiceThread(DatagramPacket receivePacket) 
		{ 
			receivePacketThread = receivePacket; 
		//}
		
		//public void run(){
		
			try
			{	
				TimeUnit.SECONDS.sleep(1);				
				ByteArrayInputStream bais= new ByteArrayInputStream(receivePacketThread.getData());
				
				ObjectInputStream is = new ObjectInputStream(bais);
				keyDataPair kd = null; 
				kd = (keyDataPair) is.readObject();
				System.out.println("Object received from car is "+kd.encryptedData);


				//decrype the message for processing
				String returnMessage;	
				byte[] symKey = kd.key;
				byte[] eData = kd.encryptedData;
				byte[] dbyte = decrypt(kd.key, kd.encryptedData);
				
				//////////////////////

				SimpleWeightedGraph<String, DefaultWeightedEdge>  graph; 
				graph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class); 
				int numberVertices = 4 ;
				DefaultWeightedEdge e;
				for(int i=1; i<=numberVertices*numberVertices; i++)
					graph.addVertex(Integer.toString(i)) ;

				Random ran = new Random();
				for(int i=1; i<=numberVertices*numberVertices; i++){
					int weight = (i%numberVertices) * 50 ;
					if(i%numberVertices != 0 ){
						e = graph.addEdge(Integer.toString(i), Integer.toString(i + 1)) ;
						graph.setEdgeWeight(e, weight); 
					}
			
					weight = (int) Math.floor((i-1)/numberVertices)*100;
					if(numberVertices*numberVertices - i >= numberVertices){			
						e = graph.addEdge(Integer.toString(i), Integer.toString(i + numberVertices)) ;
						graph.setEdgeWeight(e, weight);
					}
				}

				IntBuffer intBuf =
				   ByteBuffer.wrap(dbyte)
				     .order(ByteOrder.BIG_ENDIAN)
				     .asIntBuffer();
				 int[] startEndCoor = new int[intBuf.remaining()];
				 intBuf.get(startEndCoor);
				
				path = new DijkstraShortestPath(graph, Integer.toString(startEndCoor[0]), Integer.toString(startEndCoor[1]));
				edgeList = path.getPathEdgeList();
				double length;
				length = path.getPathLength();
				System.out.println("weighted length is " + length);
				
				pathAndDistance pD = new pathAndDistance();
				pD.path = edgeList.toString();
				pD.distance = length;

				client cl = new client();
				cl.sendBestRoute(pD,kd.port);



				////////////////////////		
				
				
			}
			catch(Exception e){}
			

		} 

	} 
	
	public static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}	

}
