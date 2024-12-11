import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.NoSuchElementException;

/**
 * Creates a set of mappings of an AAC that has two levels, one for categories and then within each
 * category, it has images that have associated text to be spoken. This class provides the methods
 * for interacting with the categories and updating the set of images that would be shown and
 * handling an interactions.
 * 
 * @author Catie Baker & Alex Pollock
 *
 */
public class AACMappings implements AACPage {
	AACCategory categoryCurrent; // The current category
	String catName; // The current categories name.
	AssociativeArray<String, String> overaching; // The "list" of images and assosiated words
	AssociativeArray<String, AACCategory> cats; // The categories
	AACCategory home; // The home category

	/**Attention
	 * Creates a set of mappings for the AAC based on the provided file. The file is read in to create
	 * categories and fill each of the categories with initial items. The file is formatted as the
	 * text location of the category followed by the text name of the category and then one line per
	 * item in the category that starts with > and then has the file name and text of that image
	 * 
	 * for instance: img/food/plate.png food >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing and food has french fries and
	 * watermelon and clothing has a collared shirt
	 * 
	 * @param filename the name of the file that stores the mapping information
	 */
	public AACMappings(String filename) {
		try {
			BufferedReader eyes = new BufferedReader(new FileReader(filename));
			String line;

			cats = new AssociativeArray<String,AACCategory>();
			home = new AACCategory("");
			overaching = new AssociativeArray<String,String>();
			categoryCurrent = home;

			while ((line = eyes.readLine()) != null) {
				String lineSplit[] = line.split(" ", 2);
				if (lineSplit[0].charAt(0) != '>') {
					catName = lineSplit[0];
					overaching.set(catName, lineSplit[1]);
					cats.set(catName, new AACCategory(lineSplit[1]));
					home.addItem(catName, lineSplit[1]);
					categoryCurrent = cats.get(catName);
				} else {
					categoryCurrent.addItem(lineSplit[0].substring(1), lineSplit[1]);
				} // if/else
			} // while
			eyes.close();
		} catch (Exception e) {
			System.err.println("File Not Found");
		} // try/catch
		categoryCurrent = home;
	} // AACMappings(String)

	/**
	 * Given the image location selected, it determines the action to be taken. This can be updating
	 * the information that should be displayed or returning text to be spoken. If the image provided
	 * is a category, it updates the AAC's current category to be the category associated with that
	 * image and returns the empty string. If the AAC is currently in a category and the image
	 * provided is in that category, it returns the text to be spoken.
	 * 
	 * @param imageLoc the location where the image is stored
	 * @return if there is text to be spoken, it returns that information, otherwise it returns the
	 *         empty string
	 * @throws NoSuchElementException if the image provided is not in the current category
	 */
	public String select(String imageLoc) {
		try {
			if (categoryCurrent.hasImage(imageLoc)){
				if (categoryCurrent.equals(home)){
					categoryCurrent = cats.get(imageLoc);
					return "";
				} // if
				return categoryCurrent.select(imageLoc);
			} // if
		} catch (Exception e) {
			throw new NoSuchElementException();
		} // try/catch
		throw new NoSuchElementException();
	} // select(String)

	/**
	 * Provides an array of all the images in the current category
	 * 
	 * @return the array of images in the current category; if there are no images, it should return
	 *         an empty array
	 */
	public String[] getImageLocs() {
		return categoryCurrent.getImageLocs();
	} // getImageLocs()

	/**
	 * Resets the current category of the AAC back to the default category
	 */
	public void reset() {
		categoryCurrent = home;
	} // reset()


	/**
	 * Writes the ACC mappings stored to a file. The file is formatted as the text location of the
	 * category followed by the text name of the category and then one line per item in the category
	 * that starts with > and then has the file name and text of that image
	 * 
	 * for instance: img/food/plate.png food >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing and food has french fries and
	 * watermelon and clothing has a collared shirt
	 * 
	 * @param filename the name of the file to write the AAC mapping to
	 */
	public void writeToFile(String filename) {
		try {
			FileWriter pen = new FileWriter(filename);
			String[] bigNames = home.getImageLocs();

			for (int i = 0; i<bigNames.length; i++){
				String line = bigNames[i] + overaching.get(bigNames[i])+"\n";
				pen.write(line);
				this.select(bigNames[i]);
				String[] smallNames = this.getImageLocs();
				for (int n = 0; n<smallNames.length;n++){
					line = smallNames[n] + overaching.get(smallNames[n]) + "\n";
					pen.write(line);
				} // for
			} // for

			pen.close();
		} catch (Exception e) {
			System.err.println("Failed to write in file: " + filename);
		} // try/catch
	} // writeToFile(String)

	/**
	 * Adds the mapping to the current category (or the default category if that is the current
	 * category)
	 * 
	 * @param imageLoc the location of the image
	 * @param text the text associated with the image
	 */
	public void addItem(String imageLoc, String text) {
		if (categoryCurrent == home){
			AACCategory newCat = new AACCategory(text);
			try {
				cats.set(imageLoc, newCat);
			} catch (Exception e) {
				System.err.println("Failed to add item");
			} // try/catch
		} // if
		categoryCurrent.addItem(imageLoc, text);
	} // addItem(String,String)


	/**
	 * Gets the name of the current category
	 * 
	 * @return returns the current category or the empty string if on the default category
	 */
	public String getCategory() {
		if (this.categoryCurrent == this.home) {
			return "";
		} // if
		return this.categoryCurrent.name;
	} // getCategory()


	/**
	 * Determines if the provided image is in the set of images that can be displayed and false
	 * otherwise
	 * 
	 * @param imageLoc the location of the category
	 * @return true if it is in the set of images that can be displayed, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		return this.overaching.hasKey(imageLoc);
	} // hasImage(String)

	public boolean hasCategory(String imageloc){
		return cats.hasKey(imageloc);
	} // hasCategory(String)
} // class AACMappings
