package com.biao.broadcast;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author biaowu.
 */
public class BroadcastTest {
  private Broadcast broadcast = new Broadcast.Builder().build();

  private final ConcurrentLinkedQueue<String> dispatchedSubscribers = new ConcurrentLinkedQueue<>();

  @Test
  public void test_post_event() {
    ImmediateListener listener = new ImmediateListener();
    broadcast.register(listener);
    broadcast.post("Hello");
    broadcast.post("Bill");
    assertEquals(2, dispatchedSubscribers.size());
    assertEquals("Hello", dispatchedSubscribers.poll());
    assertEquals("Bill", dispatchedSubscribers.poll());
  }

  @Test
  public void test_post_a_dead_event() {
    ImmediateListener listener = new ImmediateListener();
    broadcast.register(listener);
    broadcast.post("Hello");
    assertEquals(1, dispatchedSubscribers.size());
    assertEquals("Hello", dispatchedSubscribers.poll());

    broadcast.unregister(listener);
    broadcast.post("Bill");
    assertEquals(0, dispatchedSubscribers.size());
    // TODO: 2016/11/15 exception handler
  }

  private class ImmediateListener {
    @Subscribe
    void listen(String message) {
      dispatchedSubscribers.add(message);
    }
  }
}