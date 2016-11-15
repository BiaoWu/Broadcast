package com.biao.broadcast;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author biaowu.
 */
public class SubscriberRegistryTest {

  private SubscriberRegistry registry;

  @Before
  public void setUp() throws Exception {
    Broadcast broadcast = new Broadcast.Builder().build();
    registry = broadcast.registry;
  }

  @Test
  public void testRegister() throws Exception {
    assertEquals(0, registry.getSubscribers(String.class).size());
    registry.register(new StringSubscriber());
    assertEquals(1, registry.getSubscribers(String.class).size());
    registry.register(new StringSubscriber());
    assertEquals(2, registry.getSubscribers(String.class).size());

    registry.register(new ObjectSubscriber());
    assertEquals(2, registry.getSubscribers(String.class).size());
    assertEquals(1, registry.getSubscribers(Object.class).size());
  }

  @Test
  public void testUnregister() throws Exception {
    StringSubscriber s1 = new StringSubscriber();
    StringSubscriber s2 = new StringSubscriber();

    registry.register(s1);
    registry.register(s2);

    registry.unregister(s1);
    assertEquals(1, registry.getSubscribers(String.class).size());

    registry.unregister(s2);
    assertTrue(registry.getSubscribers(String.class).isEmpty());
  }

  @Test
  public void testUnregister_notRegistered() throws Exception {
    try {
      registry.unregister(new StringSubscriber());
      fail();
    } catch (IllegalArgumentException e) {
    }

    StringSubscriber s1 = new StringSubscriber();
    registry.register(s1);
    try {
      registry.unregister(new StringSubscriber());
      fail();
    } catch (IllegalArgumentException e) {
    }

    registry.unregister(s1);
    try {
      registry.unregister(s1);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testGetSubscribers() throws Exception {
    assertEquals(0, registry.getSubscribers("").size());
    registry.register(new StringSubscriber());
    assertEquals(1, registry.getSubscribers("").size());
    registry.register(new StringSubscriber());
    assertEquals(2, registry.getSubscribers("").size());

    registry.register(new ObjectSubscriber());
    assertEquals(3, registry.getSubscribers("").size());
    assertEquals(1, registry.getSubscribers(new Object()).size());
    assertEquals(1, registry.getSubscribers(1).size());

    registry.register(new IntegerSubscriber());
    assertEquals(3, registry.getSubscribers("").size());
    assertEquals(1, registry.getSubscribers(new Object()).size());
    assertEquals(2, registry.getSubscribers(1).size());
  }

  @Test
  public void testGetSubscribers_order() throws Exception {
    StringSubscriber s1 = new StringSubscriber();
    StringSubscriber s2 = new StringSubscriber();
    ObjectSubscriber o = new ObjectSubscriber();

    List<Subscriber> empty = registry.getSubscribers("");
    assertTrue(empty.isEmpty());

    registry.register(s1);
    List<Subscriber> one = registry.getSubscribers("");
    assertEquals(1, one.size());
    assertEquals(s1, one.get(0).listener);

    registry.register(s2);
    registry.register(o);
    List<Subscriber> three = registry.getSubscribers("");
    assertEquals(3, three.size());

    registry.unregister(s2);

    assertEquals(s1, three.get(0).listener);
    assertEquals(s2, three.get(1).listener);
    assertEquals(o, three.get(2).listener);

    List<Subscriber> two = registry.getSubscribers("");
    assertEquals(2, two.size());
    assertEquals(s1, two.get(0).listener);
    assertEquals(o, two.get(1).listener);

    assertTrue(empty.isEmpty());
    assertEquals(1, one.size());
  }

  @Test
  public void testChildSubscriber_Override_True() {
    StringSubscriber s1 = new StringSubscriber();
    OverrideStringSubscriber s2 = new OverrideStringSubscriber();

    registry.register(s1);
    assertEquals(1, registry.getSubscribers("").size());

    registry.register(s2);
    assertEquals(2, registry.getSubscribers("").size());
  }

  @Test
  public void testChildSubscriber_Override_False() {
    StringSubscriber s1 = new StringSubscriber();
    OverrideStringFalseSubscriber s2 = new OverrideStringFalseSubscriber();

    registry.register(s1);
    assertEquals(1, registry.getSubscribers("").size());

    registry.register(s2);
    assertEquals(3, registry.getSubscribers("").size());
  }

  @Test
  public void testChildSubscriber_Override_ButNotTheSameIdentifier() {
    ObjectSubscriber o1 = new ObjectSubscriber();
    ObjectChildSubscriber o2 = new ObjectChildSubscriber();

    registry.register(o1);
    assertEquals(1, registry.getSubscribers("").size());

    registry.register(o2);
    assertEquals(3, registry.getSubscribers("").size());
  }

  private static class StringSubscriber {
    @Subscribe
    void listen(String s) {
    }
  }

  private static class ObjectSubscriber {
    @Subscribe
    void listen(Object o) {
    }
  }

  private static class IntegerSubscriber {
    @Subscribe
    void listen(Integer i) {
    }
  }

  private static class OverrideStringSubscriber extends StringSubscriber {
    @Subscribe
    void listen(String s) {
    }
  }

  private static class OverrideStringFalseSubscriber extends StringSubscriber {
    @Subscribe(override = false)
    void listen(String s) {
    }
  }

  private static class ObjectChildSubscriber extends ObjectSubscriber {
    @Subscribe(override = true)
    void listen(String s) {
    }
  }
}