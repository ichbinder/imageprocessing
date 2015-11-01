package ue02;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.plaf.synth.Region;

//authors: 	André Vallentin
//			Jakob Warnow
public class FloodFill {

	public enum FillMode { DEPTHFIRST, BREADTHFIRST, SEQUENTIAL};
	
	/**Konvertiert eine Binärbild so um, das schwarze Pixel als Vordergrund (1) 
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

	
	private void SequentialLabeling(int [][] pixels){
		
		int m = 2;
//		Set<Set> c = new TreeSet<Set>();		
		TreeSet c = new TreeSet();
		HashMap coll = new HashMap();
		ArrayList<Set> collisions = new ArrayList();

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
						int minIndex = neighbors.indexOf(Collections.min(neighbors));
						pixels[u][v] = (int) neighbors.get(minIndex);

						//Sammle Kollisionen
						for(int t = 0; t < neighbors.size(); t++){

							if(minIndex != t){
								
								Set set = new TreeSet();
								set.add(m);
								set.add(t);								
								
								if(!collisions.contains(set)){
									collisions.add(set);
								}
								
								if(!coll.containsKey(m)){		
									coll.put(m, t);
								}
								if(!coll.containsKey(t)){
									coll.put(t, m);									
								}
								else{
									
									if(!coll.containsKey(m)){
										coll.put(m, t);
									}
									else{
										coll.put(t, m);										
									}
								}
							}
						}
					}
				}
			}
		}
	}

	
	
	private ArrayList checkNeighbors(int [][] pixels, int u, int v){

		ArrayList foundNeighbors = new ArrayList<Integer>();
		int next = 0;
		int neighbor = 0;
		//oben links
		if(u-1 >= 0 && v-1 >= 0){
			if(pixels[u-1][v-1] > 1){
//				next = next > pixels[u-1][v-1] ? next : pixels[u-1][v-1];
				neighbor = pixels[u-1][v-1];	
				if (!foundNeighbors.contains(neighbor)) foundNeighbors.add(neighbor);
			}
		}
		
		//oben
		if(u-1 >= 0){
			if(pixels[u-1][v] > 1){
				//next = next > pixels[u-1][v] ? next : pixels[u-1][v];
				neighbor = pixels[u-1][v];					
				if (!foundNeighbors.contains(neighbor)) foundNeighbors.add(neighbor);
			}
		}
		//oben rechts
		if(u-1 >= 0 && v+1 < pixels[u].length){
			if(pixels[u-1][v+1] > 1){
//				next = next > pixels[u-1][v] ? next : pixels[u-1][v+1];
				neighbor = pixels[u-1][v+1];					
				if (!foundNeighbors.contains(neighbor)) foundNeighbors.add(neighbor);

			}
		}
		//links
		if(v-1 >= 0){
			if(pixels[u][v-1] > 1){
	//			next = next > pixels[u][v] ? next : pixels[u][v-1];				
				neighbor = pixels[u][v-1];					
				if (!foundNeighbors.contains(neighbor)) foundNeighbors.add(neighbor);
			}				
		}
		return foundNeighbors;
	}
	
	public void RegionLabeling(int [] pixels, int width, int height, FillMode mode){

		int [][] labledPixels = prepareBinaryImage(pixels, width, height);
		
		if(mode == FillMode.SEQUENTIAL){
			SequentialLabeling(labledPixels);
			printResults(labledPixels);
			return;
		}		
		int m = 2;
		
		for(int h = 0; h < height; h++){
			
			for(int w = 0; w < width; w++){
				
    			if(labledPixels[h][w] == 1){
    				
    				if(mode == FillMode.BREADTHFIRST){} 
    				else if(mode == FillMode.DEPTHFIRST){}
    			} 
			}
		}
	}
	
	public static void main(String[] args) {
		
		int [] pixels = {0, 0, 255, 0, 255, 0, 255, 0, 0 , 0 ,255, 255, 255, 0, 0, 0, 255};
		
		int width = 4;
		int height = 4;
		
		FloodFill fill = new FloodFill();
		fill.RegionLabeling(pixels, width, height, FillMode.SEQUENTIAL);
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
