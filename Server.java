import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

//Andy Martinez Reyes
//Homework
//CS 4504

public class Server {

	// building each ip section & port for easy access
	static String routerIP = "";
	static String thisIP;

	// ports are randomized and adjusted later, for now we use these defaults
	static int tempPort = 5557;
	static int thisPort = 5559;

	public static void main(String args[]) throws IOException, InterruptedException {
		new Server();
	}
//reused the beginning of TCPServer from Semester project part 2

	public Server() throws IOException, InterruptedException {
		// important to have both for flavor, but the router and client IP's should be
		// the same
		// due to localized data

		Socket generatedConnectionSocket = null;
		Socket socketToClient = null;
		thisIP = InetAddress.getLocalHost().getHostAddress();
		routerIP = thisIP;
		Scanner scan = new Scanner(System.in); // will be used for user to select the data type and also to send
		// building the Client->Router connection, don't need to send anything from
		// Router back.
		PrintWriter out = new PrintWriter(new Socket(routerIP, 5556).getOutputStream(), true);

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

		// Wait for request, then connecting to Router2 which hands over target IP
		ServerSocket tempSock = new ServerSocket(tempPort);
		System.out.println("Waiting for request from Client on port 5557");
		generatedConnectionSocket = tempSock.accept();
		tempSock.close();
		// read input from router
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(generatedConnectionSocket.getInputStream()));
		} catch (IOException e) {
			System.err.println("I/O Connetion failed for: " + routerIP);
			System.exit(1);
		}

		String clientIPFound = "";
		try {
			clientIPFound = in.readLine();
		} catch (IOException e) {
			System.err.println("Couldn't read from router");
			System.exit(1);
		}

		generatedConnectionSocket.close();

		// connecting to the client
		try {
			socketToClient = new Socket(clientIPFound, thisPort);
			System.out.println("FINALLY CONNECTED to Client!");
		} catch (IOException e) {
			System.err.println("Client not found.");
			System.exit(1);
		}

		// Receiving the string

		// this receives the string and then shoots it back as an upper case version
		String str = "";
		try {
			in = new BufferedReader(new InputStreamReader(socketToClient.getInputStream()));
//			str = in.readLine();
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: " + routerIP);
			System.exit(1);
		}

//		System.out.println("Received: " + str);
//		str = str.toUpperCase();

		try {
			out = new PrintWriter(socketToClient.getOutputStream(), true);
			out.println(str);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: " + routerIP);
			System.exit(1);
		}

		int[] Ar = null;
		int arraysize = 0;
		String sizeGood;
		int UserSelection = in.read();
		sizeGood = in.readLine();

		if (sizeGood.equals("Good")) {
			arraysize = in.read();
			System.out.println("The Size recieved is: " + arraysize);
			Ar = new int[arraysize];
			for (int i = 0; i < arraysize; i++) {
				Ar[i] = in.read();
				// System.out.print("Array#: " + i); //used to check if the values were correct
				// System.out.println(" : " + Ar[i]);
			}
		} else {
			String oddEven = in.readLine();
			if (oddEven.equals("even")) {
				int loopAmount = in.read();
				for (int i = 0; i < loopAmount; i++) {
					arraysize += in.read();
				}
				Ar = new int[arraysize];
				for (int i = 0; i < arraysize; i++) {
					Ar[i] = in.read();
					// System.out.print("Array#: " + i);//used to check if the values were correct
					// System.out.println(" : " + Ar[i]);
				}

			} else {
				arraysize = in.read();
				arraysize += in.read();
				Ar = new int[arraysize];
				for (int i = 0; i < arraysize; i++) {
					Ar[i] = in.read();
					// System.out.print("Array#: " + i);//used to check if the values were correct
					// System.out.println(" : " + Ar[i]);
				}
			}

		}

		System.out.println("The array size is: " + arraysize);
		if (arraysize <= 10) {
			for (int i = 0; i < arraysize; i++) {
				System.out.print(Ar[i] + ", ");
			}
		}
		System.out.println("");
		System.out.println("");
		int rightP = arraysize - 1;

		long t1, t2;
		long time1, time2;
		switch (UserSelection) {
		// not needed, as one core is ran in client
		case 1:
//
//			Threading oneT = new Threading(Ar, 0, rightP);
//			time1 = System.nanoTime();
//			oneT.start();
//
//			time2 = System.nanoTime();
//			System.out.println("Run/Execution time: " + (time2 - time1) + "ns");
//			for (int i = 0; i < arraysize; i++) {
//				System.out.print(Ar[i] + ", ");
//			}
			System.out.print("one core is ran in client");
			socketToClient.close();
			in.close();
			out.close();
			scan.close();
			break;

		case 2:
			System.out.print("Two Cores selected!");
//			System.out.println("Afterfirst part"); //used to locate how the partition worked
//			for (int i = 0; i < arraysize; i++) {
//				System.out.print(Ar[i] + ", ");
//			}
			Threading TwoTLeft = new Threading(Ar, 0, rightP);

			int firstPartitionOrganizer = TwoTLeft.firstpart();

			System.out.println("");

			TwoTLeft.setP(firstPartitionOrganizer);
			Threading TwoTRight = new Threading(Ar, firstPartitionOrganizer, rightP);
			TwoTLeft.setLeft(0);
			time1 = System.nanoTime();
			TwoTLeft.start();
			TwoTRight.start();
			TwoTLeft.join();
			TwoTRight.join();
			time2 = System.nanoTime();
			System.out.println("Run/Execution time:  " + (time2 - time1) + "ns");
			if (arraysize <= 10) {
				for (int i = 0; i < Ar.length; i++) {
					System.out.print(Ar[i] + ", ");

					// reuse the code in client to send the Array back to client
				}
			}
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
			System.out.println("");
			t1 = System.currentTimeMillis();
			for (int i = 0; i < arraysize; i++) {
				out.write(Ar[i]);

				out.flush();
			}
			t2 = System.currentTimeMillis();

			System.out.println("Time to Send took: " + (t2 - t1) + "ms");
			socketToClient.close();
			in.close();
			out.close();
			scan.close();
			break;
		case 3: // used if the user want to thread using 4 cores
			System.out.print("Four Cores selected!");

			Threading fourleft = new Threading(Ar, 0, rightP);

			// first you partition the whole array, to get the first splits.
			int firstPartitionLocation = fourleft.firstpart(); // the partion number is stored and saved
			Threading fourright = new Threading(Ar, firstPartitionLocation, rightP);
			int rightPartiionLocaiton = fourright.firstpart();// this finds the next partion to the right side of the
																// previous partition
			fourright.setP(rightPartiionLocaiton);
			Threading fourright2 = new Threading(Ar, rightPartiionLocaiton, rightP); // This thread will do the very
																						// right side of the

			// then find the left side partition
			fourleft.setP(firstPartitionLocation); // this will help only to split the array until the first partition
													// made
			int leftParitionLocation = fourleft.firstpart(); // this will give the left most partition

			Threading fourleft2 = new Threading(Ar, leftParitionLocation, firstPartitionLocation);// Partition
			fourleft.setP(leftParitionLocation);
			// gets the proper time that it takes to run once the array has been
			// properly split
			long time3 = System.nanoTime();
			fourleft.start();

			fourright.start();
			fourleft2.start();
			fourright2.start();

			// used to make sure that the time captured isn't ran before all the threads
			// finish

			// Syntax found at
			// https://stackoverflow.com/questions/4691533/java-wait-for-thread-to-finish
			fourleft.join();
			fourright.join();
			fourleft2.join();
			fourright2.join();

			long time4 = System.nanoTime();
			System.out.println("Run/Execution time:  " + (time4 - time3) + "ns");
			if (arraysize <= 10) {
				for (int i = 0; i < arraysize; i++) {
					System.out.print(Ar[i] + ", ");
				}
			}
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
			System.out.println("");
			t1 = System.currentTimeMillis();
			for (int i = 0; i < arraysize; i++) {
				out.write(Ar[i]);

				out.flush();
			}
			t2 = System.currentTimeMillis();
			System.out.println("Time to Send took: " + (t2 - t1) + "ms");
			socketToClient.close();
			in.close();
			out.close();
			scan.close();
			break;

		case 4: // used if the user want to thread using 4 cores
			System.out.print("Eight Cores selected!");
			long time5;
			Threading FirstEigthT = new Threading(Ar, 0, rightP);

			// first you partition the whole array, to get the first splits.
			int HalfwayP = FirstEigthT.firstpart(); // the partion number is stored and saved
			FirstEigthT.setP(HalfwayP);

			int TwoEightP = FirstEigthT.firstpart();
			FirstEigthT.setP(TwoEightP);

			int FirstEight = FirstEigthT.firstpart();
			FirstEigthT.setP(FirstEight);

			Threading twoEigthT = new Threading(Ar, FirstEight, TwoEightP);

			Threading ThreeEightT = new Threading(Ar, TwoEightP, HalfwayP);
			int ThreeEigth = ThreeEightT.firstpart();
			ThreeEightT.setP(ThreeEigth);

			Threading FourEightT = new Threading(Ar, ThreeEigth, HalfwayP);

			Threading FithEightT = new Threading(Ar, HalfwayP, rightP);
			int SixEigth = FithEightT.firstpart();// this finds the next partion to the right side
			FithEightT.setP(SixEigth);
			int FiveEigth = FithEightT.firstpart();
			FithEightT.setP(FiveEigth);

			Threading SithEightT = new Threading(Ar, FiveEigth, SixEigth);

			Threading SeventhEightT = new Threading(Ar, SixEigth, rightP);
			int sevenEight = SeventhEightT.firstpart();
			SeventhEightT.setP(sevenEight);

			Threading FinalRigthEight = new Threading(Ar, sevenEight, rightP);
			time5 = System.nanoTime();

			FirstEigthT.start();

			twoEigthT.start();
			ThreeEightT.start();
			FourEightT.start();
			FithEightT.start();
			SithEightT.start();
			SeventhEightT.start();
			FinalRigthEight.start();

			long time6 = System.nanoTime();
			System.out.println("Run/Execution time: " + (time6 - time5) + "ns");
			if (arraysize <= 10) {
				for (int i = 0; i < arraysize; i++) {
					System.out.print(Ar[i] + ", ");
				}
			}
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
			System.out.println("");
			t1 = System.nanoTime();
			for (int i = 0; i < arraysize; i++) {
				out.write(Ar[i]);

				out.flush();
			}
			t2 = System.nanoTime();
			System.out.println("Time to Send took: " + (t2 - t1) + "ms");
			socketToClient.close();
			in.close();
			out.close();
			scan.close();
			break;
		case 5:
			// code that threads using 16 cores would go here
			// wasn't able to properly implement this in time
			// int[] ParitionLocationList = new int[15];
//			Threading PartitionMakerThread = new Threading(Ar, 0, rightP); // used to make all the partitions.
//			for (int i = 0; i < ParitionLocationList.length; i++) {
//				ParitionLocationList[i];
//			}

			break;
		case 6:
			System.out.println("goodbye");
			socketToClient.close();
			in.close();
			out.close();
			scan.close();
			// clientSocket.close();
//			serverSocket.close();

		}

	}
}
