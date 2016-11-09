package com.biao.broadcast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author biaowu.
 */
public class DefaultDispatchCenterTest {

  private final IntegerSubscriber i1 = new IntegerSubscriber("i1");
  private final IntegerSubscriber i2 = new IntegerSubscriber("i2");
  private final IntegerSubscriber i3 = new IntegerSubscriber("i3");
  private final List<Subscriber> integerSubscribers = new ArrayList<>();

  private final ConcurrentLinkedQueue<Object> dispatchedSubscribers = new ConcurrentLinkedQueue<>();

  private ImmediateDispatcher immediateDispatcher = new ImmediateDispatcher();
  private DefaultDispatchCenter dispatcherCenterDefault;

  @Before
  public void setUp() throws Exception {
    Dispatcher[] dispatchers = {
        immediateDispatcher,
    };
    dispatcherCenterDefault = new DefaultDispatchCenter(dispatchers);

    String methodName = "handleInteger";
    Class<?> eventType = Integer.class;
    integerSubscribers.add(
        createSubscriber(i1, methodName, eventType, immediateDispatcher.identifier()));
    integerSubscribers.add(
        createSubscriber(i2, methodName, eventType, immediateDispatcher.identifier()));
    integerSubscribers.add(
        createSubscriber(i3, methodName, eventType, immediateDispatcher.identifier()));
  }

  @Test
  public void testDispatch_dispatcher_not_found() throws Exception {
    final int no_register_dispatcher_id = 110;
    final String event = "1";

    List<Subscriber> eventSubscribers = new ArrayList<>();
    Object listener = new Object() {
      public void listen(String s) {
        assertEquals(s, event);
      }
    };
    eventSubscribers.add(
        createSubscriber(listener, "listen", String.class, no_register_dispatcher_id));

    try {
      dispatcherCenterDefault.dispatch(event, eventSubscribers);
      fail();
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("dispatcher"));
    }
  }

  @Test
  public void testDispatch() throws Exception {
    dispatcherCenterDefault.dispatch(110, integerSubscribers);

    assertEquals(3, dispatchedSubscribers.size());
    assertEquals(i1, dispatchedSubscribers.poll());
    assertEquals(i2, dispatchedSubscribers.poll());
    assertEquals(i3, dispatchedSubscribers.poll());
  }

  private static Subscriber createSubscriber(Object listener, String methodName, Class<?> eventType,
      int dispatcherId) {
    try {
      return new Subscriber(listener,
          new SubscribeMethod(listener.getClass().getMethod(methodName, eventType), eventType,
              new SubscribeInfo(true, dispatcherId)));
    } catch (NoSuchMethodException e) {
      fail(e.getMessage());
      return null;
    }
  }

  private final class IntegerSubscriber {
    private final String name;

    public IntegerSubscriber(String name) {
      this.name = name;
    }

    @Subscribe
    public void handleInteger(Integer integer) {
      dispatchedSubscribers.add(this);
    }

    @Override
    public String toString() {
      return name;
    }
  }
}