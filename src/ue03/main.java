package ue03;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	

	/**Bildet ein 1-dimensionales in ein 2-dimensionales Pixel-Array
	 * @param src Pixeldaten (1 dim)
	 * @param width Bildbreite
	 * @param height Bildhöhe
	 * @return 2-dimensionales Pixel-Array*/
	private int [][] prepareBinaryImage(int [] src, int width, int height){
		
		int [][] pixels = new int [height][width];
		int i = 0;
		
		for(int h = 0; h < height; h++){
			
			for(int w = 0; w < width; w++){
				
				pixels[h][w] = src[i];
				i++;
			}
		}		
		return pixels;
	}
	
	public void RegionLabeling(int [] input, int width, int height) {
		int m = 2;

		int[][] pixels = prepareBinaryImage(input, width, height);

		long timeStart = System.nanoTime();
		
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				if ((pixels[h][w] & 0x00ffffff) == 0) {
//					pixels = this.BreadthFirst8Without(pixels, h, w, m);
					m = m + 1;
//					System.out.println(pixels[h][w]);
				}
			}
		}
		
		int outputPixels[] = new int [width * height];
		int i = 0;
		for(int h = 0; h < height; h++){
			for(int w = 0; w < width; w++){
				
				
				outputPixels[i] = pixels[h][w];
				i++;
			}
		}
		System.arraycopy(outputPixels, 0, input, 0, input.length);
		
		long timeStop = System.nanoTime();
		
		System.out.println("Calc Time: " + (timeStop - timeStart));
//		System.out.println("Stack max length: " + lengthOfS);		
	}
	
	public void potrace(int[][] pixels, int height, int width) {
		
	}

}
