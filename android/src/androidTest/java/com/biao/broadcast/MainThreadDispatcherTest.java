package com.biao.broadcast;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author biaowu.
 */
public class MainThreadDispatcherTest {
  private Broadcast broadcast;
  private ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue<>();

  @Before
  public void setUp() throws Exception {
    broadcast = new Broadcast.Builder().dispatcher(new MainThreadDispatcher()).build();
  }

  @Test
  public void dispatch() throws Exception {
    MainThreadListener l1 = new MainThreadListener();
    MainThreadListener l2 = new MainThreadListener();
    MainThreadListener l3 = new MainThreadListener();
    broadcast.register(l1);
    broadcast.register(l2);
    broadcast.register(l3);

    broadcast.post(110);
    //for delay
    Thread.sleep(1000);
    assertEquals(3, queue.size());
    assertEquals(l1, queue.poll());
    assertEquals(l2, queue.poll());
    assertEquals(l3, queue.poll());
  }

  private final class MainThreadListener {
    @Subscribe(dispatcher = MainThreadDispatcher.ID_MAIN_THREAD)
    public void handleInteger(Integer integer) {
      queue.add(this);
    }
  }
}