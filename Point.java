package lab3;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Brute force solution. To run: java brute.java < input.txt
 *
 * @author Magnus Nielsen Largely based on existing C++-laborations by Tommy
 *         Olsson and Filip Strömbäck.
 */
public class Fast {
	/**
	 * Clear the window and paint all the Points in the plane.
	 *
	 * @param frame  - The window / frame.
	 * @param points - The points to render.
	 */
	private static void render(JFrame frame, ArrayList<Point> points) {
		frame.removeAll();
		frame.setVisible(true);
		for (Point p : points) {
			p.paintComponent(frame.getGraphics(), frame.getWidth(), frame.getHeight());
		}
	}

	/**
	 * Draw a line between two points in the window / frame.
	 *
	 * @param frame - The frame / window in which you wish to draw the line.
	 * @param p1    - The first Point.
	 * @param p2    - The second Point.
	 */
	private static void renderLine(JFrame frame, Point p1, Point p2) {
		p1.lineTo(p2, frame.getGraphics(), frame.getWidth(), frame.getHeight());
	}

	/**
	 * Read all the points from the buffer in the input scanner.
	 *
	 * @param input - Scanner containing a buffer from which to read the points.
	 * @return ArrayList<Point> containing all points defined in the file / buffer.
	 */
	private static ArrayList<Point> getPoints(Scanner input) {
		int count = input.nextInt();
		ArrayList<Point> res = new ArrayList<>();
		for (int i = 0; i < count; ++i) {
			res.add(new Point(input.nextInt(), input.nextInt()));
		}
		return res;
	}

	public static void main(String[] args) throws InterruptedException {
		  JFrame frame;
	        Scanner input = null;
	        File f;
	        ArrayList<Point> points;

	        if (args.length != 1) {
	            System.out.println("Usage: java Brute <input.txt>\n" +
	                    "Replace <input.txt> with your input file of preference, and possibly the path.\n" +
	                    "Ex: java Brute data/input1000.txt");
	            System.exit(0);
	        }

	        // Opening the file containing the points.
	        f = new File(args[0]);
	        try {
	            input = new Scanner(f);
	        } catch (FileNotFoundException e) {
	            System.err.println("Failed to open file. Try giving a correct file / file path.");
	        }

	        // Creating frame for painting.
	        frame = new JFrame();
	        frame.setMinimumSize(new Dimension(512, 512));
	        frame.setPreferredSize(new Dimension(512,512));

	        // Getting the points and painting them in the window.
	        points = getPoints(input);
	        render(frame, points);

		// Sorting points by natural order (lexicographic order). Makes finding end points of line segments easy.
	        Collections.sort(points, new NaturalOrderComparator());

	        long start = System.currentTimeMillis();

		// Kolla igenom varje punkt p i listan
		for (Point p : points) {
			
			// Instansiera en ny HashMap för varje punkt p. HashMapen hashar från en slope (double) till en lista
			// av punkter som delar samma slope från punkten p.
			HashMap<Double, ArrayList<Point>> slopes = new HashMap<Double, ArrayList<Point>>();
			
			// En for loop som iterar över samma lista som låter oss jämföra varje punkt q med p.
			for (Point q : points) {
				
				// Hämta slope värdet för p till q.
				double slope = p.slopeTo(q);
				
				// Om p har samma koordinater som q är det samma punkt och därför meningslöst att gå vidare i for loopen.
				if (slope == Double.NEGATIVE_INFINITY)
					continue;
				
				// Denna if-sats låter oss spara tid via att undvika onödiga iterationer. Om vi redan jämför p med q
				// behöver vi inte jämföra q med p i en senare iteration
				if (p.getX() > q.getX())
					continue;
				
				// Om slopes finns i mappen vill vi hämta korresponderande lista och stoppa in q.
				if (slopes.containsKey(slope)) {
					ArrayList<Point> list = slopes.get(slope);
					list.add(q);
					slopes.put(slope, list); 
				}
				
				// Annars vill vi skapa en ny lista och placera q där istället.
				else {
					ArrayList<Point> list = new ArrayList<Point>();
					list.add(q);
					slopes.put(slope, list);
				}
			}
			
			// Efter vi jämfört alla punkter med p, så vill vi gå igenom alla slopes i hashmapen och kolla om det finns
			// listor med 3 eller fler punkter i sig. Om det stämmer ritar vi ett streck från punkten p som är vårt minsta värde 
			// till det sista värdet i listan som då också är det största värdet.
			for (double s : slopes.keySet()) {
				ArrayList<Point> list = slopes.get(s);
				if(list.size() >= 3) {
					renderLine(frame, p, list.get(list.size()-1));
				}
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Computing all the line segments took: " + (end - start) + " milliseconds.");
		System.out.println("Computing all the line segments took: " + (end - start)/1000 + " seconds.");
	}

	/**
	 * Comparator class. Used to tell Collections.sort how to compare objects of a
	 * non standard class.
	 */
	private static class NaturalOrderComparator implements Comparator<Point> {
		public int compare(Point a, Point b) {
			if (a.greaterThan(b))
				return 1;
			return -1;
		}
	}
}
