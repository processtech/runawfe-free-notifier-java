package ru.runa.notifier.util;

import java.io.InputStream;
import java.io.BufferedInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AePlayWave extends Thread {
    private static final Log log = LogFactory.getLog(AePlayWave.class);
    private String wavFile;
    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb

    public AePlayWave(String wavFile) {
        this.wavFile = wavFile;
    }

    public void run() {
        SourceDataLine auline = null;
        try {
            InputStream audioSrc = getClass().getResourceAsStream(wavFile);
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
            auline.start();
            int nBytesRead = 0;
            byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
            while (nBytesRead != -1) {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0)
                    auline.write(abData, 0, nBytesRead);
            }
        } catch (Exception e) {
            log.error("Unable to play wav file " + wavFile, e);
        } finally {
            if (auline != null) {
                auline.drain();
                auline.close();
            }
        }
    }
    
    public static void playNotification(String wavFile) {
        if (ResourcesManager.isSoundsEnabled()) {
            new AePlayWave(wavFile).start();
        }
    }
}