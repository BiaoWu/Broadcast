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

import java.util.List;

/**
 * Dispatches events to listeners.
 *
 * @author biaowu.
 */
public class Broadcast {

  private final Registry registry;
  private final DispatchCenter dispatchCenter;

  Broadcast(Registry registry, DispatchCenter dispatchCenter) {
    this.registry = registry;
    this.dispatchCenter = dispatchCenter;
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
}
