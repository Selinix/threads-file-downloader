package com.selinix.downloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Semaphore;

public class DownloadThread implements Runnable {
    public Thread thrd;
    private URL url;
    private Semaphore SEMAPHORE;

    DownloadThread (Semaphore SEMAPHORE, String name, URL url) {
        this.thrd = new Thread(this, name);
        this.url = url;
        this.SEMAPHORE = SEMAPHORE;
        this.thrd.start();
    }
    @Override
    public void run() {
        try {
            if(SEMAPHORE != null) {
                SEMAPHORE.acquire();
            }
            try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream("Downloads" +  url.getPath())) {
                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            }
            catch (IOException exc) {
                System.out.println(exc.getMessage());
            }
            if(SEMAPHORE != null) {
                SEMAPHORE.release();
            }
            System.out.println("Завершение " + thrd.getName() + ". Файл " + url + " загружен.");
        }
        catch (InterruptedException exc) {
            System.out.println(exc.getClass());
        }
    }
}
