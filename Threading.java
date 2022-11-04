
//Andy Martinez Reyes
//Homework
//CS 4504

public class Threading extends Thread {
	int[] BArry;
	int Left;
	int p;

	Threading(int[] nBArry, int nLeft, int np) {
		BArry = nBArry;
		Left = nLeft;
		p = np;
	}

	@Override
	public void start() {

		quickSort(BArry, Left, p);

//		System.out.println("");
////		for (int i = 0; i < BArry.length; i++) {
////			System.out.print(BArry[i] + ", "); //used to debug and check values
////
////		}
//		System.out.println("");
	}

	public int[] getBArry() {
		return BArry;
	}

	public void setBArry(int[] bArry) {
		BArry = bArry;
	}

//quick sort used from lecture slides and assignment from Algorithm Analysis Lecture slides
	public static void quickSort(int[] N, int L, int R) {
		int LP = L;
		int RP = R;
		int[] nN = N;

		// Used psuedocode from the slide in chapter 7
		if (LP < RP) {
			// selects the pivot
			int pivotP = pNtition(nN, LP, RP);
			// does the runs the left side of pivot
			if (LP < pivotP - 1) {
				quickSort(nN, LP, pivotP - 1);
			}
			// runs the right side of pivot
			if (RP > pivotP + 1) {
				quickSort(nN, pivotP + 1, RP);
			}
		}

	}

	public static int pNtition(int[] N, int L, int R) {

		int pivotP = L;

		int PL = L;
		int PR = R;
		if (PL == PR) {

			return PL;
		}

		// While loop found at the powerpoint slides
		while (PL <= PR) {

			if (N[PL] > N[pivotP] && N[PR] <= N[pivotP]) {
				// swaps the left and right
				int tempA = N[PL];
				N[PL] = N[PR];
				N[PR] = tempA;

			}

			if (N[PL] <= N[pivotP]) {
				PL++;

			}
			if (N[PR] >= N[pivotP]) {
				PR--;

			}

		}

		// Saves the right to add it back to the left
		int tempA = N[PR];
		N[PR] = N[pivotP];
		N[pivotP] = tempA;
//
		return PR;

	}

	public int firstpart() {
		return pNtition(BArry, Left, p);

	}

	public int getLeft() {
		return Left;
	}

	public void setLeft(int left) {
		Left = left;
	}

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
	}

}
