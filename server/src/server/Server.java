package server;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

	// Config
	private int port = 12345;
	
	private ServerSocket serverSocket;
	//private Scanner stdIn;
	private List<ConnectedUser> connectedUsers;
	
	public Server() throws IOException
	{
		serverSocket = new ServerSocket(port);
		connectedUsers = new ArrayList<ConnectedUser>();
		
		serverSocket.setSoTimeout(5 * 1000);
		//stdIn = new Scanner(System.in);
	}
	
	public void listenForConnections() throws IOException
	{
		while (true)
		{
			println("Listening for connections...");
			try
			{
				Socket s = serverSocket.accept();
				println("Connection made");
				ConnectedUser c = new ConnectedUser(s);
				connectedUsers.add(c);
				new Thread(c.task).start();
			}
			catch (InterruptedIOException e)
			{
				System.out.println("No connection, cleaning up list...");
				cleanConnections();
			}
		}
	}
	
	private void cleanConnections()
	{
		for (int i = connectedUsers.size() - 1; i >= 0; i--)
		{
			ConnectedUser c = connectedUsers.get(i);
			if (c.isFinished())
			{
				c.quit();
				connectedUsers.remove(i);
			}
		}
	}
	
	public void quit()
	{
		try {
			if (serverSocket != null)
				serverSocket.close();
			connectedUsers.stream().forEach((c) -> { c.quit(); });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void println(Object o)
	{
		System.out.println(o);
	}
	
	public class ConnectedUser
	{
		public Socket socket;
		public Scanner in;
		public Writer out;
		
		public ConnectedUser(Socket s) throws IllegalArgumentException, IOException
		{
			if (s == null)
				throw new IllegalArgumentException("Socket parameter 's' is null.");
			
			socket = s;
			in = new Scanner(s.getInputStream());
			out = new OutputStreamWriter(s.getOutputStream());
		}
		
		public Runnable task = () -> {
			try
			{
				while (true)
				{
					String input = in.nextLine();
					out.write("Recevied: " + input);
					out.write('\n');
					out.flush();
					
					if (input.equals("quit"))
						return;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		};
		
		public boolean isFinished()
		{
			return socket.isClosed();
		}
		
		public void quit()
		{
			try
			{
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args)
	{
	    // Temporary useless comment.
	    // Starts the server, and listens for connections.
	    // Upon accepting a connection, the server will start a new thread to handle that connection.
	    // This is a completely different comment, but it will cause a conflict
		Server s = null;
		try
		{
			s = new Server();
			s.listenForConnections();
			s.quit();
		}
		catch (IOException e)
		{
			if (s != null)
				s.quit();
			e.printStackTrace();
		}
	}
}
