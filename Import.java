package programming;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Import {

	// generates a BufferedRedaer from a file
	public static BufferedReader brFromFile(String dir, String name) throws IOException {
		// converts a file into a bufferedReader object
		FileReader fr = new FileReader(dir + name);
		BufferedReader br = new BufferedReader(fr);
		return br;
	}

	// from Buffered Reader , makes copies of all restaurant isolating the
	// catergories
	public static ArrayList<DataPoint> createArr(BufferedReader br) throws NumberFormatException, IOException {

		ArrayList<DataPoint> masterarr = new ArrayList<DataPoint>();
		String line;

		while ((line = br.readLine()) != null) {
			// analyse the line
			Scanner s = new Scanner(line);
			// get the wanted values

			s.useDelimiter(",");

			// System.out.println(line);

			String product = s.next();
			String profile = s.next();

			// closing the scanner
			s.close();

			masterarr.add(new DataPoint(product, profile));

		}
		return masterarr;
	}

	public static HashMap<String, HashMap<String, Integer>> analyze(ArrayList<DataPoint> input) {

		List<String> product_act = new ArrayList<String>();
		List<String> profile_act = new ArrayList<String>();

		// create lists for all products
		for (DataPoint point : input) {

			product_act.add(point.product);
			profile_act.add(point.profile);

		}

		// delete duplicates
		Set<String> profile = new HashSet<String>();

		// now have unique list of profiles
		profile.addAll(profile_act);

		// System.out.println(profile);

		// now have product/profile = uniques only, and 'act' for OG data
		// list of interactions
		HashMap<String, ArrayList<String>> propro = new HashMap<String, ArrayList<String>>();

		// loops over all uniques profiles
		for (String ID1 : profile) {

			ArrayList<String> interactions = new ArrayList<String>();

			// look at input, for specific profile
			for (DataPoint point : input) {
				String ID2 = point.profile;

				// System.out.println(point.profile + ID1);

				if (ID1.equals(ID2)) {
					// System.out.print("added");
					interactions.add(point.product);
				}
			}
			// System.out.println(ID1);
			// add list of interactions to propro
			propro.put(ID1, interactions);
			// System.out.println(propro);
		}

		// we now have a HashMap of a profile and its interactions

		// make HashMap of HashMap of integers, first K is profile ID sencond K is
		// profile of interatees

		HashMap<String, HashMap<String, Integer>> fin = new HashMap<String, HashMap<String, Integer>>();

		int count = 0;

		for (String ID1 : profile) {
			// System.out.println("LAYER");
			HashMap<String, Integer> in = new HashMap<String, Integer>();
			for (String ID2 : profile) {
				// have list of interactions for one thing and other
				ArrayList<String> x = propro.get(ID1);
				ArrayList<String> y = propro.get(ID2);
				// System.out.print(x);
				// System.out.println(y);
				// need to determine the common factors in lists

				ArrayList<String> comparer = new ArrayList<String>();
				comparer.addAll(x);

				comparer.retainAll(y);
				int common = comparer.size();
				in.put(ID2, common);
			}
			fin.put(ID1, in);

		}
		// System.out.println(fin);
		return fin;
	}
	//
	// Outputs Analysis as interaction Matrix
	//
	// public static void produceOutMatrix(HashMap<String, HashMap<String, Integer>>
	// data) throws FileNotFoundException {
	//
	// File file = new File("E:\\JavaFolder\\Matlab output.csv");
	// PrintWriter pw = new PrintWriter(file);
	//
	// // loop through each LINE
	// for (String ID1 : data.keySet()) {
	// HashMap<String, Integer> inter = data.get(ID1);
	// StringBuilder sb = new StringBuilder();
	//
	// sb.append(ID1);
	// sb.append(",");
	//
	// for (String ID2 : data.keySet()) {
	// sb.append(inter.get(ID2));
	// sb.append(",");
	// }
	// sb.append('\n');
	//
	// pw.write(sb.toString());
	// }
	//
	// pw.close();
	// }

	//Outputs ALL interactions
	
	public static void produceOutDirect(HashMap<String, HashMap<String, Integer>> data, String filename, String dir)
			throws FileNotFoundException {

		File file = new File(dir + filename + ".csv");
		PrintWriter pw = new PrintWriter(file);

		// loop through each LINE
		for (String ID1 : data.keySet()) {
			HashMap<String, Integer> inter = data.get(ID1);
			StringBuilder sb = new StringBuilder();

			int i = 0;

			for (String ID2 : inter.keySet()) {
				while (i < inter.get(ID2)) {
					sb.append(ID1);
					sb.append(",");
					sb.append(ID2);
					sb.append("\n");
					pw.write(sb.toString());
					i++;
				}
			}

		}

		pw.close();
	}

	// Produce Interactions to a certain limit
	
	public static void produceOutNumbered(HashMap<String, HashMap<String, Integer>> data, String filename, String dir,
			int above, int limit) throws FileNotFoundException {

		File file = new File(dir + filename + ".csv");
		PrintWriter pw = new PrintWriter(file);
		int count = 0;

		// loop through each LINE
		for (String ID1 : data.keySet()) {
			HashMap<String, Integer> inter = data.get(ID1);
			StringBuilder sb = new StringBuilder();

			for (String ID2 : inter.keySet()) {

				if (limit != 0) {
					while (count < limit) {
						if (inter.get(ID2) > above && !ID1.equals(ID2)) {
							sb.append(ID1);
							sb.append(",");
							sb.append(ID2);
							sb.append("\n");
							pw.write(sb.toString());
							count++;
						}
					}
				} else {
					if (inter.get(ID2) > above && !ID1.equals(ID2)) {
						sb.append(ID1);
						sb.append(",");
						sb.append(ID2);
						sb.append("\n");
						pw.write(sb.toString());
						count++;
					}
				}
			}
		}
		pw.close();

	}

	public static void main(String[] args) throws IOException {

		// Main loops through list of csv files to do analysis
		// If all data is placed in a single csv file, Java runs out of memory
		
		int num = 1;
		String dir = "C:\\Users\\Will Boon\\Desktop\\Scraping Amazon\\Final Data\\Good Data\\";
		// String dir = "C:\\Users\\Will Boon\\Desktop\\Scraping Amazon\\Final Data\\Bad Data\\For Java\\";
		while (num != 0) {
			System.out.println("Importing");
			BufferedReader data = brFromFile(dir, num + ".csv");
			System.out.println("Import done");

			System.out.println("Doing Analysis");
			HashMap<String, HashMap<String, Integer>> wow = analyze(createArr(data));

			System.out.println("Exporting results");
			produceOutNumbered(wow, num + " out", dir, 2, 0);
			System.out.println("Done");
			num--;
		}

		System.out.println("actually done!");
	}

}
