package org.mobicents.media.server.impl.resource.mediaplayer;

import org.mobicents.media.server.impl.resource.mediaplayer.audio.CachedRemoteStreamProvider;
import org.mobicents.media.server.impl.resource.mediaplayer.audio.RemoteStreamProvider;
import org.mobicents.media.server.impl.resource.mediaplayer.audio.wav.WavTrackImpl;
import org.mobicents.media.server.spi.memory.Frame;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by hamsterksu on 05.12.16.
 */
public class MemTest {

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, InterruptedException {

        System.out.println("Press to start");
        Scanner in = new Scanner(System.in);

        Executor executor = Executors.newFixedThreadPool(50);
        final RemoteStreamProvider cache = new CachedRemoteStreamProvider(100);

        while (true) {
            System.out.println("Wait for option:");
            int option = in.nextInt();
            switch (option) {
                case 1:
                    test(executor, cache);
                    break;
                default:
                    return;
            }
        }
    }

    private static void test(Executor executor, final RemoteStreamProvider cache) throws InterruptedException {
        int cacheSize = 1;
        double fileSize = 61712d;
        int iteration = (int) Math.floor(cacheSize * 1024d * 1024d / fileSize) - 1;

        final URLStreamHandler handler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(final URL arg0) throws IOException {
                return new URLConnection(arg0) {

                    @Override
                    public void connect() throws IOException {

                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return new FileInputStream("/home/hamsterksu/work/restcomm/mediaserver/resources/mediaplayer/src/test/resources/demo-prompt.wav");
                    }
                };
            }
        };
        int size = 100000 * iteration;
        final CountDownLatch doneSignal = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            final int j = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        URL url = new URL(null, "http://test" + j + ".wav", handler);
                        WavTrackImpl track = new WavTrackImpl(url, cache);
                        System.out.println(j + ":" + track.getFormat().getName() + ": " + track.getDuration());
                        play(track);
                    } catch (UnsupportedAudioFileException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    doneSignal.countDown();
                }
            });
        }
        doneSignal.await();
    }

    private static void play(WavTrackImpl track) throws IOException {
        long readCount = 0;
        long overallDelay = 0;
        long timestamp = 0;
        int sn = 0;
        long duration = track.getDuration();
        int j = 0;
        while (true) {
            j++;
            sn++;
            //readCount++;
            Frame frame = track.process(timestamp);
            if (frame == null) {
                System.out.println("Exit frame is null");
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
            //delivering data to the other party.
            /*if (mediaSink != null) {
                mediaSink.perform(frame);
            }*/
            if (frame.isEOM()) {
                return;
            }

        }
    }
}
