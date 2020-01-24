package com.selinix.downloader;

public class Run {
    public static void main(String[] args) {
        FileDownloader fd = new FileDownloader("URLs.txt", 2);
        fd.download();
    }
}
