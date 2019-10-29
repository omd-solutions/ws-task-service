package com.omd.ws.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.core.MessageSendingOperations;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Sends messages in batches with a temporal buffer
 * <p>
 * Buffer of messages is a Set such that only the latest message for
 * an equal instance of T is sent
 *
 * @param <T>
 */
public class BatchedMessageSender<T> {

    private int messagesPerSecond;
    private String destination;
    private MessageSendingOperations<String> messageSender;
    private BlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private Thread senderThread = new Thread(this::monitorQueue);
    private Set<T> buffer = new HashSet<>();

    private static final Logger LOG = LoggerFactory.getLogger(BatchedMessageSender.class);

    public BatchedMessageSender(int messagesPerSecond, String destination, MessageSendingOperations<String> messageSender) {
        this.messagesPerSecond = messagesPerSecond;
        this.destination = destination;
        this.messageSender = messageSender;
        senderThread.setName("BatchedMessageSender-queue-monitor");
        senderThread.start();
    }

    public void convertAndSend(T message) {
        queue.add(message);
    }

    void monitorQueue() {
        long millisBuffer = 1000 / messagesPerSecond;
        long clock = System.currentTimeMillis();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                T msg;
                while (null != (msg = queue.poll(millisBuffer, TimeUnit.MILLISECONDS))) {
                    if (System.currentTimeMillis() - clock > millisBuffer) {
                        sendBuffer();
                        clock = System.currentTimeMillis();
                    }
                    buffer.add(msg);
                }
                sendBuffer();
            } catch (InterruptedException e) {
                LOG.info("Queue monitor interrupted, shutting down");
            }
        }
        LOG.info("Queue monitor shutdown");
    }

    void sendBuffer() {
        if (!buffer.isEmpty()) {
            messageSender.convertAndSend(destination, buffer);
            buffer.clear();
        }
    }

    void shutdown() {
        senderThread.interrupt();
    }
}
