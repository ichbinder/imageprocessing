package ue02;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

//authors: 	André Vallentin
//			Jakob Warnow
public class SequentialLabeling {

	public enum FillMode { DEPTHFIRST, BREADTHFIRST, SEQUENTIAL};
	
	/**Konvertiert ein Binärbild so um, das schwarze Pixel als Vordergrund (1) 
	 * und weiße Pixel als Hintergrund (0) ausgegeben werden.
	 * 
	 * @param src Array mit Binärpixeln (0: Schwarz, 255: Weiß)
	 * @param width Breite des Bildes
	 * @param height Höhe des Bildes
	 * @return ein Array mit 1 als Vordergrund und 0 als Hintergrund*/	
	private int [][] prepareBinaryImage(int [] src, int width, int height){
		
		int [][] pixels = new int [height][width];
		int i = 0;
		
		for(int h = 0; h < height; h++){
			
			for(int w = 0; w < width; w++){
				
				//Sollte Wert 0 (Schwarz) sein, trage 1 als Vordergrund ein, sonst 0 für Hintergrund (Weiß)
				pixels[h][w] = src[i] == 0 ? 1 : 0;
				i++;
			}
		}		
		return pixels;
	}

	/**Markiert Bildregionen */
	public void SequentialLabeling(int []pixels, int width, int height){
		
		int [][] labledPixels = prepareBinaryImage(pixels, width, height);

		ArrayList<Set> collisions = new ArrayList();
		int m = 2;
		m = AssignIntialLabels(labledPixels, m, collisions);

		Set [] resolvedLabels = ResolveLabelCollisions(m, collisions);
		int [] colors = getRGBColorsRandom(resolvedLabels);
	
		RelabelTheImage(labledPixels, resolvedLabels, colors);
		PrintPicture(labledPixels, width, height, "out_color" );

		int outputPixels[] = new int [width * height];
		int i = 0;
		for(int h = 0; h < height; h++){
			for(int w = 0; w < width; w++){
				outputPixels[i] = labledPixels[h][w];
				i++;
			}
		}
		System.arraycopy(outputPixels, 0, pixels, 0, pixels.length);
	}

	
	private int AssignIntialLabels(int [][] pixels, int m, ArrayList<Set> collisions){
		
		for(int u = 0; u < pixels.length; u++){
			
			for(int v = 0; v < pixels[u].length; v++){
				
				if(pixels[u][v] == 1){

					ArrayList neighbors = checkNeighbors(pixels, u, v);

					//Wenn alle Nachbarn Hintergrund sind
					if(neighbors.size() == 0){
						pixels[u][v] = m;
						m++;
					}
					//Wenn genau ein Nachbar gefunden wurde
					else if(neighbors.size() == 1){
						pixels[u][v] = (int) neighbors.get(0);					
					}
					//Wenn mehrere Nachbarn gefunden wurden nehme kleinsten Wert
					else{
						
						int minimum = findMinimum(neighbors);						
						pixels[u][v] = minimum;

						//Sammle Kollisionen
						Set set = new TreeSet();							

						for(int t = 0; t < neighbors.size(); t++){								
							set.add((int) neighbors.get(t)); // m	
						}
						if(!collisions.contains(set)){
							collisions.add(set);
						}
					}
				}
			}
		}//Ende Labeling		
		return m;
	}

	private int [] getRGBColors(Set [] set){

		int colorCounter = 0;
		for(int i = 2; i < set.length; i++){
			if(!set[i].isEmpty()) colorCounter++;
		}
		
		int [] colors = new int [set.length];
				
		Color.HSBtoRGB(1.0f, 1.0f, 1.0f);
		float diff = 1.0f / (colorCounter);
		
		for(int i = 2; i < set.length; i++){
			if(!set[i].isEmpty()){
				colors[i] = Color.HSBtoRGB(1.0f - diff * i, 1.0f, 1.0f);
			}
		}		
		return colors;
	}
	
	private int [] getRGBColorsRandom(Set [] set){
		
		int [] colors = new int [set.length];
		
		for(int i = 2; i < set.length; i++){
			if(!set[i].isEmpty()){
				colors[i] = Color.HSBtoRGB( (float) Math.random(), 1.0f, 1.0f);
			}
		}
		return colors;
	}
	
	
	private int [] getRGBColors(int max){

		int [] colors = new int [max];
				
		Color.HSBtoRGB(1.0f, 1.0f, 1.0f);
		float diff = 1.0f / (max -2);
		
		for(int i = 2; i < max; i++){
			colors[i] = Color.HSBtoRGB(1.0f - diff * i, 1.0f, 1.0f);
		}
		return colors;
	}
	
	/**Passt die schon vorsortieren Labels an und ersetzt Kollisionen mit den gefundenen Werten. 
	 * Pixel werden entsprechend der gefundenen Farben eingefärbt.
	 * Es wird das ganze Bild durchgegangen und mit den vorhandenen Sets (Zuordnungen der Labels) verglichen.
	 * Sollte ein Label gefunden werden in einem der Sets, wird der Farbwert im Bezug zum Array-Index genommen
	 * 
	 * @param labeledPixels Pixel mit vormarkierten Bereichen die vorher Schwarz waren (Labels) beginned mit 2
	 * @param sortedLabels Label-Zuordnungen um Kollisionen aufzulösen
	 * @param colors Array mit Farbwerten um die markierten Bereiche entsprechend einzufärben*/
	private void RelabelTheImage(int [][] labeledPixels, Set [] sortedLabels, int [] colors){
				
		for(int u = 0; u < labeledPixels.length; u++){
			
			for(int v = 0; v < labeledPixels[u].length; v++){
				
				int value = labeledPixels[u][v];
								
				if(value > 1){
									
					for(int i = 2; i < sortedLabels.length; i++){
					
						//Sollte der Wert im jeweiligen Set enthalten sein, hole Farbwert im Bezug zum Set-Index.
						if(sortedLabels[i].contains(value)){
							
							labeledPixels[u][v] = colors[i];
						} 					
					}					
				}else{
					labeledPixels[u][v] = 0xffffffff;
				}
			}
		}		
	}
	
	/**Gibt den minimalsten Wert (int) in einer Liste zurück
	 * @param list Die Liste die untersucht wird
	 * @return den kleinsten Eintrag in der Liste*/
	private int findMinimum(ArrayList list){
		
		int minimum = 0;
		Collections.sort(list);		
		minimum =  (int) list.iterator().next();
		return minimum;
	}
		
	/**Löst Kollisionen zwischen Labels auf und gibt am Ende einen Set-Vektor zurück.
	 * @param maxLabel Die maximal erreichte Labelnummer (m). 
	 * @param collisions Alle gefundenen Kollisionen
	 * @return ein Array mit Sets worin die kollidierten Labels neu zugeordnet wurden.*/
	private Set [] ResolveLabelCollisions(int maxLabel, ArrayList<Set> collisions){
				
		Set[] vec = new Set[maxLabel];
		for(int i = 0; i < vec.length; i++){
			Set set = new TreeSet();
			set.add(i);
			vec[i] = set;			
		}		
				
		for(int i = 0; i < collisions.size(); i++){
			
			Iterator tempIterator = collisions.get(i).iterator();
			int lA = (int) tempIterator.next();
			int lB = (int) tempIterator.next();
			
			Set rA = null;
			Set rB = null;
			
			for(int j = 2; j < vec.length; j++){
				
				if(vec[j].contains(lA)) rA = vec[j];
				if(vec[j].contains(lB)) rB = vec[j];				
			}
						
			if(rA != rB){
				rA.addAll(rB);
				rB.removeAll(rB);
			}
		}		
		return vec;
	}

	
	
	/**Prüft von einer Pixel-Position die Nachbarpixel mit einer 8er Nachbarschaft.
	 * Gesammelt werden hierbei alle Nachbarn die ein Label > 1 besitzen.
	 *
	 * @param pixels: Die zu untersuchenden Pixelwerte mit Labels (0,1,2..m-1).
	 * @param u die vertikale Achsenkoordinate
	 * @param v die horizontale Achenkoordinate
	 * @param eine Liste mit gesammelten Nachbarlabels > 1*/
	private ArrayList checkNeighbors(int [][] pixels, int u, int v){

		ArrayList foundNeighbors = new ArrayList<Integer>();
		int next = 0;
		int neighbor = 0;
		//oben links
		if(u-1 >= 0 && v-1 >= 0){
			if(pixels[u-1][v-1] > 1){
				neighbor = pixels[u-1][v-1];
				if (!foundNeighbors.contains(neighbor)) foundNeighbors.add(neighbor);
			}
		}
		
		//oben
		if(u-1 >= 0){
			if(pixels[u-1][v] > 1){
				neighbor = pixels[u-1][v];	
				if (!foundNeighbors.contains(neighbor)) foundNeighbors.add(neighbor);
			}
		}
		//oben rechts
		if(u-1 >= 0 && v+1 < pixels[u].length){
			if(pixels[u-1][v+1] > 1){
				neighbor = pixels[u-1][v+1];	
				if (!foundNeighbors.contains(neighbor)) foundNeighbors.add(neighbor);
			}
		}
		//links
		if(v-1 >= 0){
			if(pixels[u][v-1] > 1){
				neighbor = pixels[u][v-1];					
				if (!foundNeighbors.contains(neighbor)) foundNeighbors.add(neighbor);
			}
		}
		return foundNeighbors;
	}
	
	//#----------------------------------- Zusatz
	
	/**Sichert eine Bilddatei.*/
	private void PrintPicture(int [][] pixels, int width, int height, String name){
		
		BufferedImage imgOutput = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
	    for(int i=0; i < height; i++)
	        for(int j=0; j < width; j++)
	        	imgOutput.setRGB(j, i, pixels[i][j]);	    
		try {
			File f = new File("src/ue02/META-INF/" + name + ".png");
			ImageIO.write(imgOutput, "png", f);
			System.out.println("Saved Picture");
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
	}
	
	/**Gibt die markierten Pixel-Bereiche aus.
	 * @param pixels Ein 2 dimensionales Array mit markierten Bereichen*/
	private void printResults(int [][] pixels){
		
		for(int u = 0; u < pixels.length; u++){
			
			for(int v = 0; v < pixels[u].length; v++){
				
				System.out.print(pixels[u][v] + " ");
			}
			System.out.print("\n");
		}
	}	
}
