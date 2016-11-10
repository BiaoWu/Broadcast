package com.biao.broadcast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author biaowu.
 */
public class BroadcastTest {

  Broadcast broadcast;

  @Mock Registry registry;
  @Mock DispatchCenter dispatchCenter;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    broadcast = new Broadcast.Builder().registry(registry).dispatchCenter(dispatchCenter).build();
  }

  @Test
  public void testRegister() throws Exception {
    broadcast.register(this);
    verify(registry).register(this);
  }

  @Test
  public void testUnregister() throws Exception {
    broadcast.unregister(this);
    verify(registry).unregister(this);
  }

  @Test
  public void testPostADeadEvent() throws Exception {
    Object event = this;

    List<Subscriber> subscribers = Collections.emptyList();
    when(registry.getSubscribers(event)).thenReturn(subscribers);

    broadcast.post(event);

    verify(registry).getSubscribers(event);
    verify(dispatchCenter, never()).dispatch(event, subscribers);
  }

  @Test
  public void testPost() throws Exception {
    Object event = this;

    List<Subscriber> subscribers = new ArrayList<>();
    subscribers.add(new Subscriber(null, null));
    when(registry.getSubscribers(event)).thenReturn(subscribers);

    broadcast.post(event);

    verify(registry).getSubscribers(event);
    verify(dispatchCenter).dispatch(event, subscribers);
  }
}