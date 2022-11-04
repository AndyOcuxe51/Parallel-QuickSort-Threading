import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

//Andy Martinez Reyes
//Homework
//CS 4504

public class HomeworkClient {
	// CS 4306
	// Andy Martinez Reyes

	
	static String connectionIP = "";
	static String thisIP;

	// once again random later, but manual for simple testing
	static int recieverPort = 5559;

	// Reused code from CS 4306 class for the sorting algorithm implementation
	public static void main(String args[]) throws IOException, InterruptedException {
		new HomeworkClient();
	}

	// reused the beginning code of TCPServer from Semester project part 2
	public HomeworkClient() throws IOException, InterruptedException {

		// important to have both for flavor, but the router and client IP's should be
		// the same
		// due to localized data
		Scanner scan = new Scanner(System.in); // will be used for user to select the data type and also to send
		Socket generatedConnectionSocket = null;
		thisIP = InetAddress.getLocalHost().getHostAddress();
		connectionIP = thisIP;

		// building the Client->Router connection, don't need to send anything from
		// Router back.
		PrintWriter out = new PrintWriter(new Socket(connectionIP, 5555).getOutputStream(), true);

		// THE ONLY WAY TO FIX THIS IS TO PARSE DATA IN THE THREAD!
		// The client needs to be linear in this order:
		/*
		 * Become built in the router / logged in the table. C->R1 Then request to see
		 * the server. C->R1->R2->S, S->C Finally a communication loop between these
		 * two. S->C over and over
		 */

		// this section sends the data to the router.
		out.println("LogMe");
		out.println(thisIP);

		// Request B connects to me, then wait for B to connect to me
		// once registered we can ask the router to push us over to the other client
		out.println("ClientToRouter1");
		out.println(thisIP);

		// This piece is the waiting piece, we find the Server by it polling and accept
		// it.
		try {
			ServerSocket serverSocket = new ServerSocket(recieverPort);
			System.out.println("Waiting on socket connection from Server");
			generatedConnectionSocket = serverSocket.accept();
			serverSocket.close();
			System.out.println("Socket connection from Server created");
		} catch (IOException e) {
			System.err.println("Client Server Socket Failed.");
			System.exit(1);
		}

		// this will establish everything, now we just need to communicate with the
		// other client.
		// we now assign the output and input to the new connection
		// The generatedConnectionSocket will be the socket between C->S

		// sample of sending a message, simulation of a echo server
//		String clientMessage = "Howdy Howdy";

		try {
			out = new PrintWriter(generatedConnectionSocket.getOutputStream(), true);
//			out.println(clientMessage);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: " + connectionIP);
			System.exit(1);
		}

		// then receiving a message
		String temp = "";
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(generatedConnectionSocket.getInputStream()));
			temp = in.readLine();
		} catch (IOException e) {
			System.err.println("No I/O For: " + connectionIP);
			System.exit(1);
		}

		// Followed logic and pseudocode from lecture slides in algorithm analysis
//		// original array
		// used to switch between the provided numbers and random numbers based on size
//		int[] Ar = { 45, 23, 5, 15, 78, 56, 43, 12, 90, 44 }; //used for testing
		int[] Ar = new int[10000];
		Random rand = new Random();
		// used tips for syntax at
		// https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
		for (int i = 0; i < Ar.length; i++) {
			Ar[i] = rand.nextInt((100 - 5) + 1) + 5;
		}
		int arraysize = Ar.length;

		if (arraysize <= 10) {
			System.out.print("Original array: ");
			for (int i = 0; i < Ar.length; i++) {
				System.out.print(Ar[i] + " , ");
			}
		}
		System.out.println("");
		// make sure to log times, first when sending file
		System.out.println("how many threads would you like to use to sort the array??");
		System.out.println("1) one thread");
		System.out.println("2) two thread");
		System.out.println("3) four thread");
		System.out.println("4) eight thread");
		System.out.println("5) sixteen thread");
		System.out.println("6) Close Sockets");

		System.out.print("Enter Amount: ");
		int UserSelection = scan.nextInt();
		out.write(UserSelection);
		long time3 = System.nanoTime();
		if (UserSelection == 1) {
			Threading oneCore = new Threading(Ar, 0, arraysize - 1);
			oneCore.start();
			oneCore.join();
			long time4 = System.nanoTime();
			System.out.println("Run/Execution time:  " + (time4 - time3) + "ns");
			System.out.print("New array: ");
			if (arraysize <= 10) {
				for (int i = 0; i < Ar.length; i++) {
					System.out.print(Ar[i] + " , ");
				}
			}

		} else {
			System.out.println("");
			// System.out.println("Pushing the file through OutStream");
			if (arraysize >= 100) {
				out.println("Bad");
				int HowMuchMore = arraysize % 100;

				if (HowMuchMore == 0) {
					out.println("even");
					int loopamount = arraysize / 100;
					out.write(loopamount);
					for (int i = 0; i <= loopamount; i++) {
						out.write(100);
						out.flush();
					}

				} else {
					out.println("odd");
					out.write(100);
					out.flush();
					out.write(HowMuchMore);
					out.flush();
				}

			} else {

				out.println("Good");
				out.write(arraysize);
				out.flush();
			}
			long time1 = System.currentTimeMillis();
			for (int i = 0; i < arraysize; i++) {
				out.write(Ar[i]); // bing bing the server is written to
//			System.out.print("Array#: " + Ar[i]);
				out.flush();
			}
			long time2 = System.currentTimeMillis();
			System.out.println("Time to Send took: " + (time2 - time1) + "ms");
			//

			int[] Ar2 = null;
			int arraysize2 = 0;
			String sizeGood;

			sizeGood = in.readLine();

			if (sizeGood.equals("Good")) {
				arraysize2 = in.read();
				System.out.println("The Size recieved is: " + arraysize);
				Ar2 = new int[arraysize2];
				for (int i = 0; i < arraysize2; i++) {
					Ar2[i] = in.read();
					// System.out.print("Array#: " + i); //used to check if the values were correct
					// System.out.println(" : " + Ar[i]);
				}
			} else {
				String oddEven = in.readLine();
				if (oddEven.equals("even")) {
					int loopAmount = in.read();
					for (int i = 0; i < loopAmount; i++) {
						arraysize2 += in.read();
					}
					Ar2 = new int[arraysize2];
					for (int i = 0; i < arraysize2; i++) {
						Ar2[i] = in.read();
						// System.out.print("Array#: " + i);//used to check if the values were correct
						// System.out.println(" : " + Ar[i]);
					}

				} else {
					arraysize2 = in.read();
					arraysize2 += in.read();
					Ar2 = new int[arraysize2];
					for (int i = 0; i < arraysize2; i++) {
						Ar2[i] = in.read();
						// System.out.print("Array#: " + i);//used to check if the values were correct
						// System.out.println(" : " + Ar[i]);
					}
				}

			}

			System.out.println("The array size is: " + arraysize2);
			if (arraysize <= 10) {
				for (int i = 0; i < arraysize2; i++) {
					System.out.print(Ar2[i] + ", ");
				}
			}
		}
		generatedConnectionSocket.close();
		in.close();
		out.close();
		scan.close();
	}

}
