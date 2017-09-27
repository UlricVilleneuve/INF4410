package ca.polymtl.inf4410.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ca.polymtl.inf4410.tp1.shared.ServerInterface;
import ca.polymtl.inf4410.tp1.shared.ServerInterface.Command;

import java.util.*;

public class Client {
	public static Vector<String> commands = new Vector<String>();
	public static void main(String[] args) {
		String distantHostname = null;
		String x = "";
		if (args.length > 0) {
			distantHostname = args[0];
			if (args.length > 1) {
				x = args[1];
			}
		}
		commands.add("LIST");
		commands.add("GET");
		commands.add("LOCK");
		commands.add("CREATE");
		commands.add("PUSH");
		commands.add("SYNCLOCALDIR");

		Client client = new Client(distantHostname);
		//x = client.clamp(x);		
		//int size = (int) Math.pow(10, x);
		//byte[] var = new byte[size];
		//client.run(var);
		int command = convertStringCommandToInt(x);
		client.runCommand(command);
	}

	FakeServer localServer = null; // Pour tester la latence d'un appel de
									// fonction normal.
	private ServerInterface localServerStub = null;
	private ServerInterface distantServerStub = null;

	public Client(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		localServer = new FakeServer();
		localServerStub = loadServerStub("127.0.0.1");

		if (distantServerHostname != null) {
			distantServerStub = loadServerStub(distantServerHostname);
		}
	}

	private void runCommand(int command) {
		//appelNormal(command);

		if (localServerStub != null) {
			appelRMILocal(command);
		}

		//if (distantServerStub != null) {
		//	appelRMIDistant(command);
		//}
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

	private void appelNormal(int command) {
		long start = System.nanoTime();
		localServer.executeCommand(command);
		long end = System.nanoTime();

		System.out.println("Temps écoulé appel normal: " + (end - start)
				+ " ns");
	}

	private void appelRMILocal(int command) {
		try {
			long start = System.nanoTime();
			localServerStub.executeCommand(command);
			long end = System.nanoTime();

			System.out.println("Temps écoulé appel RMI local: " + (end - start)
					+ " ns");
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}

	private void appelRMIDistant(int command) {
		try {
			long start = System.nanoTime();
			distantServerStub.executeCommand(command);
			long end = System.nanoTime();

			System.out.println("Temps écoulé appel RMI distant: "
					+ (end - start) + " ns");
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}
	private static int convertStringCommandToInt(String command)
	{
		String upperCaseCommand = command.toUpperCase();
		if(commands.contains(upperCaseCommand))
		{
			return commands.indexOf(upperCaseCommand);
		}
		return -1;
	} 
}
