package com.biao.broadcast;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author biaowu.
 */
public class BroadcastBuilderTest {
  @Test
  public void test_all_default() throws Exception {
    Broadcast broadcast = new Broadcast.Builder().build();
    assertEquals(broadcast, broadcast.registry.broadcast);
    assertEquals(broadcast, broadcast.dispatchCenter.broadcast);
    assertEquals(1, broadcast.dispatchCenter.dispatchers.length);
  }

  @Test
  public void test_set_the_same_id_dispatcher_with_default() throws Exception {
    try {
      new Broadcast.Builder().dispatcher(new ImmediateDispatcher()).build();
      fail();
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("0"));
    }
  }

  @Test
  public void test_set_the_same_id_dispatcher() throws Exception {
    Dispatcher dispatcher1 = new Dispatcher() {
      @Override
      public void dispatch(DispatchAction dispatchAction) {

      }

      @Override
      public int identifier() {
        return 11;
      }
    };

    Dispatcher dispatcher2 = new Dispatcher() {
      @Override
      public void dispatch(DispatchAction dispatchAction) {

      }

      @Override
      public int identifier() {
        return 11;
      }
    };

    try {
      new Broadcast.Builder().dispatcher(dispatcher1).dispatcher(dispatcher2).build();
      fail();
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("11"));
    }
  }
}