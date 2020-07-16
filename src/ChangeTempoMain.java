import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ChangeTempoMain {
	private static final String ORIG_FILE_PATH = "sample.wav";

	public static void main(String[] args) {
		File sourceFile 	 = new File(ORIG_FILE_PATH);
		File destinationFile = new File("result-fast.wav");
		ChangeTempo obj 	 = new ChangeTempo();
		try {
			AudioInputStream in1 = AudioSystem.getAudioInputStream(sourceFile);
			AudioInputStream in2 = obj.getAcceleratedStream(in1, 50);
			AudioSystem.write(in2, AudioFileFormat.Type.WAVE, destinationFile);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch (UnsupportedAudioFileException uafe) {
			uafe.printStackTrace();
		} finally {
			System.out.println("DONE!!");
		}
	}
}
