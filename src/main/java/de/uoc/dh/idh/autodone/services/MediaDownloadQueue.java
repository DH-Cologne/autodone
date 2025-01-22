package de.uoc.dh.idh.autodone.services;

import org.springframework.stereotype.Component;

import java.util.concurrent.PriorityBlockingQueue;

@Component
public class MediaDownloadQueue {
    private final PriorityBlockingQueue<MediaDownloadTask> queue = new PriorityBlockingQueue<>();

    public void addTask(MediaDownloadTask task) {
        queue.add(task);
    }

    public MediaDownloadTask takeTask() throws InterruptedException {
        MediaDownloadTask task = queue.take();
        return task;
    }

    public void clear() {
        queue.clear();
    }

    public PriorityBlockingQueue<MediaDownloadTask> getQueue() {
        return queue;
    }
}
