package ue02;
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
		Set<int []> c = new TreeSet<int []>();
		
		for(int u = 0; u < pixels.length; u++){
			
			for(int v = 0; v < pixels[u].length; v++){
				
				if(pixels[u][v] == 1){
					
					if(checkNeighbors(pixels, u, v) == 0){
						pixels[u][v] = m;
						m++;						
					}
				}    			
			}
		}
		int b = 0;
		int a = b;
	}
	
	private int checkNeighbors(int [][] pixels, int u, int v){

		int next = 0;
		//oben links
		if(u-1 >= pixels.length && v-1 >= 0){
			if(pixels[u-1][v-1] > 1){
				next = next > pixels[u-1][v-1] ? next : pixels[u-1][v-1];
			}
		}
		
		//oben
		if(u-1 >= pixels.length){
			if(pixels[u-1][v] > 1){
				next = next > pixels[u-1][v] ? next : pixels[u-1][v];	
			}
		}
		//oben rechts
		if(u-1 >= pixels.length && v+1 <= pixels[u].length){
			if(pixels[u-1][v+1] > 1){
				next = next > pixels[u-1][v] ? next : pixels[u-1][v+1];
			}
		}
		//links
		if(v-1 >= 0){
			if(pixels[u][v-1] > 1){
				next = next > pixels[u][v] ? next : pixels[u][v-1];				
			}				
		}
		return next;
	}
	
	public void RegionLabeling(int [] pixels, int width, int height, FillMode mode){

		int [][] labledPixels = prepareBinaryImage(pixels, width, height);
		
		if(mode == FillMode.SEQUENTIAL){
			SequentialLabeling(labledPixels);
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
		
		int [] pixels = {0, 0, 255, 0, 255, 255, 255, 0, 0 , 0 ,255, 255, 255, 0, 0, 0, 255};
		int width = 4;
		int height = 4;
		
		FloodFill fill = new FloodFill();
		fill.RegionLabeling(pixels, width, height, FillMode.SEQUENTIAL);
	}
}
