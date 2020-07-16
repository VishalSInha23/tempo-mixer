import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class ChangeTempo {
    public AudioInputStream getAcceleratedStream(AudioInputStream ais, int percentSpeed) {
        AudioFormat format  = ais.getFormat();
        int frameSize       = format.getFrameSize();
        float playBackSpeed = (100f + percentSpeed) / 100f;
        int n 			    = 100 / percentSpeed;

        System.out.println("getAcceleratedStream");
        System.out.println("playBackSpeed : " + playBackSpeed);
        System.out.println("frameSize : " + frameSize);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[2^16];
        int read;
        try {
            while((read = ais.read(b)) > -1) {
                baos.write(b, 0, read);
            }
            baos.close();
        } catch(IOException ioe) {
        	ioe.printStackTrace();
        }

        byte[] b1 		    = baos.toByteArray();
        byte[][] origFrames = this.oneDToTwoD(b1, frameSize);
        final int origSize  = origFrames.length;
        final int newSize   = (int)(origSize / playBackSpeed);
        byte[][] newFrames  = new byte[newSize][frameSize];
        
        int x = 0, y = 0;
        while((x + n) < origSize) {
        	// Copying n samples
        	for(int i = 0; i < n; i++) {
        		for(int j = 0;j < frameSize; j++) {
        			newFrames[y + i][j] = origFrames[x + i][j];
        		}
        	}
            x += n;
            y += n;
            
            // Skipping the next sample
            x++;
        }

        byte[] b2 				  	    = this.twoDToOneD(newFrames);
        ByteArrayInputStream bais 	    = new ByteArrayInputStream(b2);
        AudioInputStream aisAccelerated = new AudioInputStream(bais, format, b2.length);
        return aisAccelerated;
    }
    
    public AudioInputStream getSlowStream(AudioInputStream ais, int percentSlow) {
        AudioFormat format   = ais.getFormat();
        int frameSize        = format.getFrameSize();
        float slowDownSpeed  = (100f + percentSlow) / 100f;
        int n 			     = 100 / percentSlow; // For every n samples in original there will be a new 1

        System.out.println("getSlowStream");
        System.out.println("slowDownSpeed : " + slowDownSpeed);
        System.out.println("frameSize : " + frameSize);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[2^16];
        int read;
        try {
            while((read = ais.read(b)) > -1) {
                baos.write(b, 0, read);
            }
            baos.close();
        } catch(IOException ioe) {
        	ioe.printStackTrace();
        }

        byte[] b1 			= baos.toByteArray();
        byte[][] origFrames = this.oneDToTwoD(b1, frameSize);
        final int origSize  = origFrames.length;
        final int newSize   = (int)(origSize * slowDownSpeed) - 1; 
        byte[][] newFrames  = new byte[newSize][frameSize];
        
        //First n samples will be same
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < frameSize; j++) {
            	newFrames[i][j] = origFrames[i][j];
            }
        }
        
        int x = n, y = n;
        while((x + n) < origSize) {
        	byte[] avg = new byte[frameSize];
        	//The next sample will be the average of samples x and x - 1
        	for(int i = 0; i < frameSize; i++) {
        		avg[i] = (byte)((origFrames[x - 1][i] + origFrames[x][i]) / 2);
        	}
        	
        	//Adding the extra sample
        	for(int i = 0; i < frameSize; i++) {
        		newFrames[y][i] = avg[i];
        	}
        	y++;
        	
        	//The next n samples will be same as next n original samples
            for(int i = 0; i < n; i++) {
                for(int j = 0; j < frameSize; j++) {
                	newFrames[y + i][j] = origFrames[x + i][j];
                }
            }
            x += n;
            y += n;
        }

        byte[] b2 				   = this.twoDToOneD(newFrames);
        ByteArrayInputStream bais  = new ByteArrayInputStream(b2);
        AudioInputStream aisSlowed = new AudioInputStream(bais, format, b2.length);
        return aisSlowed;
    }
    
    private byte[][] oneDToTwoD(byte[] oned, int frameSize) {
    	byte[][] twod = new byte[oned.length / frameSize][frameSize];
    	for(int i = 0; i < twod.length; i++) {
    		for(int j = 0; j < twod[i].length; j++) {
        		twod[i][j] = oned[(i * twod[i].length) + j]; 
    		}
    	}
    	return twod;
    }
    
    private byte[] twoDToOneD(byte[][] twod) {
    	byte[] oned = new byte[twod.length * twod[0].length];
    	for(int i = 0; i < twod.length; i++) {
    		for(int j = 0; j < twod[0].length; j++) {
    			oned[(i * twod[i].length) + j] = twod[i][j];
    		}
    	}
    	return oned;
    }
}
