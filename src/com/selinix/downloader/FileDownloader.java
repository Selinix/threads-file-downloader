package com.selinix.downloader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class FileDownloader {

    private String filePath;
    private int permits;
    private Semaphore SEMAPHORE;

    public FileDownloader(String filePath) {
        this.filePath = filePath;
        this.SEMAPHORE = null;
    }

    public FileDownloader(String filePath, int permits) {
        this(filePath);
        this.permits = permits;
        this.SEMAPHORE = new Semaphore(permits, true);
    }

    public void download() {
        //Список запущенных потоков
        List<DownloadThread> dtList = new ArrayList<>();
        int numDownload = 0;
        try {
            for (String strURL : readURLs()) {
                dtList.add(new DownloadThread(SEMAPHORE, "Поток #" + ++numDownload, new URL(strURL.trim())));
            }
            //Ожидание завершения всех потоков
            for (DownloadThread dt : dtList) dt.thrd.join();
        } catch (InterruptedException | MalformedURLException exc) {
            System.out.println(exc.getMessage());
        }
        System.out.println("Все потоки загрузок завершены.");
    }

    private List<String> readURLs() {
        String strURL;
        List<String> result = new ArrayList<>();
        if (filePath != null && !filePath.isEmpty()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "CP1251"))) {
                while ((strURL = br.readLine()) != null) {
                    result.add(strURL);
                }
            } catch (IOException exc) {
                System.out.println(exc.getMessage());
            }
        }
        return result;
    }
}

