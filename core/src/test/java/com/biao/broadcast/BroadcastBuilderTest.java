package com.biao.broadcast;

import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author biaowu.
 */
public class BroadcastBuilderTest {
  @Test
  public void test_all_default() throws Exception {
    new Broadcast.Builder().build();
  }

  @Test
  public void test_custom_dispatchCenter() throws Exception {
    DispatchCenter dispatchCenter = new DispatchCenter() {
      @Override
      public void dispatch(Object event, List<Subscriber> eventSubscribers) {

      }
    };

    new Broadcast.Builder().dispatchCenter(dispatchCenter).build();

    try {
      new Broadcast.Builder().dispatchCenter(dispatchCenter)
          .dispatcher(new ImmediateDispatcher())
          .build();
      fail();
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("Dispatcher"));
    }

    try {
      new Broadcast.Builder().dispatcher(new ImmediateDispatcher())
          .dispatchCenter(dispatchCenter)
          .build();
      fail();
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("Dispatcher"));
    }
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