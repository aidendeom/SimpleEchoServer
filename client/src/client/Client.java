package client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	
	// Config
	private String host = "localhost";
	private int port = 12345;
	
	private Socket socket;
	private Scanner stdin;
	private Scanner in;
	private Writer out;
	
	public Client() throws UnknownHostException, IOException
	{
		socket = new Socket(host, port);
		stdin = new Scanner(System.in);
		in = new Scanner(socket.getInputStream());
		out = new OutputStreamWriter(socket.getOutputStream());
		System.out.println("Connection successful!");
	}
	
	public void start() throws IOException
	{
		while (true)
		{
			String input = stdin.nextLine();
			out.write(input);
			out.write('\n');
			out.flush();
			
			String recv = in.nextLine();
			System.out.println("Recevied back: " + recv);
			
			if (input.equals("quit"))
				return;
		}
	}
	
	public void quit()
	{
		System.out.println("Shutting down...");
		try
		{
			if (socket != null)
				socket.close();
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
	
	public static void main(String[] args) throws IOException
	{		
		Client c = null;
		try {
			c = new Client();
			c.start();
			c.quit();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			if (c != null)
				c.quit();
		} catch (IOException e) {
			e.printStackTrace();
			if (c != null)
				c.quit();
		}
	}
}
