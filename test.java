package org.mobicents.media.server.impl.resource.mediaplayer;

import org.mobicents.media.server.impl.resource.mediaplayer.audio.CachedRemoteStreamProvider;
import org.mobicents.media.server.impl.resource.mediaplayer.audio.wav.WavTrackImpl;
import org.mobicents.media.server.spi.memory.Frame;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Created by hamsterksu on 05.12.16.
 */
public class MemTest {

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        int cacheSize = 1;
        double fileSize = 61712d;
        int iteration = (int) Math.floor(cacheSize * 1024d * 1024d / fileSize) - 1;

        URLStreamHandler handler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(final URL arg0) throws IOException {
                return new URLConnection(arg0) {

                    @Override
                    public void connect() throws IOException {

                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return new FileInputStream("src/test/resources/demo-prompt.wav");
                    }
                };
            }
        };

        CachedRemoteStreamProvider cache = new CachedRemoteStreamProvider(1);
        URL url = new URL(null, "http://test" + 1 + ".wav", handler);
        WavTrackImpl track = new WavTrackImpl(url, cache);
        System.out.println(track.getFormat().getName() + ": " + track.getDuration());
        play(track);

        /*for (int j = 0; j < 10; j++) {
            for (int i = 0; i < iteration; i++) {
                URL url = new URL(null, "http://test" + i + ".wav", handler);
                WavTrackImpl track = new WavTrackImpl(url, cache);
                System.out.println(track.getFormat().getName() + ": " + track.getDuration());
                play(track);
            }
        }
        for (int i = iteration; i < 2 * iteration; i++) {
            URL url = new URL(null, "http://test" + i + ".wav", handler);
            WavTrackImpl track = new WavTrackImpl(url, cache);
            System.out.println(track.getFormat().getName() + ": " + track.getDuration());
            play(track);
        }*/
    }

    private static void play(WavTrackImpl track) throws IOException {
        long readCount = 0;
        long overallDelay = 0;
        long timestamp = 0;
        int sn = 0;
        long duration = track.getDuration();
        int j = 0;
        while (overallDelay < 20000000L) {
            j++;
            System.out.println("J = " + j);
            sn++;
            readCount++;
            Frame frame = track.process(timestamp);
            if (frame == null) {
                if (readCount == 1) {
                    //stop if frame was not generated
                    return;
                } else {
                    //frame was generated so continue
                    return;
                }
            }

            //mark frame with media time and sequence number
            frame.setTimestamp(timestamp);
            frame.setSequenceNumber(sn);

            //update media time and sequence number for the next frame
            timestamp += frame.getDuration();
            overallDelay += frame.getDuration();
            sn = (sn == Long.MAX_VALUE) ? 0 : sn + 1;

            //set end_of_media flag if stream has reached the end
            if (duration > 0 && timestamp >= duration) {
                frame.setEOM(true);
            }

            long frameDuration = frame.getDuration();
            boolean isEOM = frame.isEOM();
            long length = frame.getLength();

            //delivering data to the other party.
            /*if (mediaSink != null) {
                mediaSink.perform(frame);
            }*/


        }
    }
}
