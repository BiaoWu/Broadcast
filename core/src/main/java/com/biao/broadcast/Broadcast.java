/*
 * Copyright (C) 2016 BiaoWu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.biao.broadcast;

import java.util.ArrayList;
import java.util.List;

/**
 * Dispatches events to listeners.
 *
 * create instance with {@link Builder}
 *
 * @author biaowu.
 */
public class Broadcast {
  /* package for test*/ final SubscriberRegistry registry;
  /* package for test*/ final DispatchCenter dispatchCenter;

  private Broadcast(Dispatcher[] dispatchers) {
    this.registry = new SubscriberRegistry(this);
    this.dispatchCenter = new DispatchCenter(this, dispatchers);
  }

  /**
   * Registers all methods that annotated by {@link Subscribe} to receive events.
   */
  public void register(Object listener) {
    registry.register(listener);
  }

  /**
   * Unregisters all methods that annotated by {@link Subscribe}.
   */
  public void unregister(Object listener) {
    registry.unregister(listener);
  }

  /**
   * Posts an event to all registered listeners.
   */
  public void post(Object event) {
    List<Subscriber> eventSubscribers = registry.getSubscribers(event);
    if (eventSubscribers.size() > 0) {
      dispatchCenter.dispatch(event, eventSubscribers);
    } else {
      // TODO: 2016/11/3
    }
  }

  /**
   * {@link Broadcast} Builder.
   */
  public static class Builder {
    private List<Dispatcher> dispatcherList;

    public Builder() {
      dispatcherList = new ArrayList<>();
      dispatcherList.add(new ImmediateDispatcher());
    }

    public Builder dispatcher(Dispatcher dispatcher) {
      dispatcherList.add(dispatcher);
      return this;
    }

    public Broadcast build() {
      checkDispatchers();

      return new Broadcast(dispatcherList.toArray(new Dispatcher[dispatcherList.size()]));
    }

    private void checkDispatchers() {
      List<Integer> ids = new ArrayList<>();
      for (Dispatcher dispatcher : dispatcherList) {
        int id = dispatcher.identifier();
        if (ids.contains(id)) {
          throw new IllegalArgumentException("Dispatcher must use unique identifier."
              + dispatcher.getClass().getCanonicalName()
              + "has the same identifier with others, "
              + "please change "
              + id
              + " to any other value.");
        } else {
          ids.add(id);
        }
      }
    }
  }
}
