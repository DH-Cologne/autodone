package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMG_FORMAT;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMG_SIZE_X;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMG_SIZE_Y;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_DOWNLOADRATELIMIT;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_DOWNLOADTHREADPOOL;


import java.util.concurrent.PriorityBlockingQueue;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.awt.Image.SCALE_FAST;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.write;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import de.uoc.dh.idh.autodone.entities.MediaEntity;


@Component
public class MediaDownloadExecutor {
    private final MediaDownloadQueue queue;
    private final ExecutorService executorService;

    @Autowired()
    private MediaService mediaService;


    @Autowired()
    public MediaDownloadExecutor(MediaDownloadQueue queue) {
        this.queue = queue;
        this.executorService = Executors.newFixedThreadPool(AUTODONE_DOWNLOADTHREADPOOL);
        System.out.println("Created executor service with threads: " + AUTODONE_DOWNLOADTHREADPOOL);
    }

    @PostConstruct
    public void start() {
        for (int i = 0; i < ((ThreadPoolExecutor) executorService).getCorePoolSize(); i++) {
            executorService.submit(this::processTasks);
        }
    }

    private void processTasks() {
        try {
            System.out.println("Thread started with id: " + Thread.currentThread().getId());
            while (true) {
                MediaDownloadTask task = queue.takeTask();

                MediaEntity downloadMedia = importMedia(task.getMedia().getUrl());

                MediaEntity media = mediaService.getAny(task.getMedia().getUuid());

                media.file = downloadMedia.file;


                media.contentType = downloadMedia.contentType;
                if (downloadMedia.description != null) {
                    media.description = media.description + " " + downloadMedia.description;
                }
                
                mediaService.save(media);

                TimeUnit.MILLISECONDS.sleep(AUTODONE_DOWNLOADRATELIMIT);

            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        executorService.shutdownNow();
    }

    public MediaEntity importMedia(String url) {
        try {
            var media = new MediaEntity();
            var request = new URL(url).openConnection();

            media.contentType = request.getContentType();

            if (request.getContentLength() < 1024000) {
                media.file = request.getInputStream().readAllBytes();
            } else {
                var buffer = new ByteArrayOutputStream();
                var source = read(request.getInputStream());

                var scaleX = source.getWidth();
                var scaleY = source.getHeight();

                if (scaleX > AUTODONE_IMG_SIZE_X) {
                    scaleX = AUTODONE_IMG_SIZE_X;
                    scaleY = (scaleX * source.getHeight()) / source.getWidth();
                }

                if (scaleY > AUTODONE_IMG_SIZE_Y) {
                    scaleY = AUTODONE_IMG_SIZE_Y;
                    scaleX = (scaleY * source.getWidth()) / source.getHeight();
                }

                var target = new BufferedImage(scaleX, scaleY, TYPE_INT_ARGB);
                var scaled = source.getScaledInstance(scaleX, scaleY, SCALE_FAST);
                target.getGraphics().drawImage(scaled, 0, 0, null, null);
                write(target, AUTODONE_IMG_FORMAT, buffer);

                media.description = "(Scaled down from original)";
                media.file = buffer.toByteArray();
            }

            return media;

        } catch (IOException e) {
            System.out.println("Io Exception" + e.getMessage());
            return null;
        }
    }
}