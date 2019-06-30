package com.selinix.downloader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class FileDownloader {
    private final Semaphore SEMAPHORE;

    private String filePath;

    public FileDownloader(String filePath, int permits) {
        this.filePath = filePath;
        SEMAPHORE = new Semaphore(permits, true);
        download();
    }
    private void download (){
        //Список запущенных потоков
        ArrayList<DownloadThread> dtList = new ArrayList<>();

        String strURL;
        int numDownload = 0;
        if (filePath != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "CP1251"))) {
                while ((strURL = br.readLine()) != null) {

                    //Создание потока скачивания файла и занесение его в лист
                    dtList.add(new DownloadThread("Поток #" + ++numDownload, new URL(strURL.trim())));
                }
            } catch (IOException exc) {
                System.out.println(exc.getMessage());
            }
        }
        else System.out.println("Не указано имя файла.");
        try {

            //Ожидание завершения всех потоков
            for (DownloadThread dt : dtList) dt.thrd.join();

        } catch (InterruptedException exc) {
            System.out.println(exc.getClass());
        }
        System.out.println("Все потоки загрузок завершены.");
    }

    public static void main(String[] args) {
        FileDownloader fd = new FileDownloader("URLs.txt", 2);
    }

    class DownloadThread implements Runnable {
        Thread thrd;
        private URL url;
        DownloadThread (String name, URL url) {
            thrd = new Thread(this, name);
            this.url = url;
            thrd.start();
        }
        @Override
        public void run() {
            try {
                SEMAPHORE.acquire();
                try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream("Downloads" +  url.getPath())){
                    fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                }
                catch (MalformedURLException exc) {
                    System.out.println("Не корректный URL.");
                }
                catch (IOException exc) {
                    System.out.println(exc.getMessage());
                }
                SEMAPHORE.release();
                System.out.println("Завершение " + thrd.getName() + ". Файл " + url + " загружен.");
            }
            catch (InterruptedException exc) {
                System.out.println(exc.getClass());
            }
        }
    }
}

