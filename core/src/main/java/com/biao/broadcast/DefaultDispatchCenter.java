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
 * Default dispatch center.
 *
 * @author biaowu.
 */
class DefaultDispatchCenter implements DispatchCenter {
  private final Dispatcher[] dispatchers;

  DefaultDispatchCenter(Dispatcher[] dispatchers) {
    this.dispatchers = dispatchers;
  }

  @Override
  public void dispatch(Object event, List<Subscriber> eventSubscribers) {
    for (Subscriber subscriber : eventSubscribers) {
      Dispatcher dispatcher = findDispatcher(subscriber.subscribeMethod.info.dispatcher);
      if (dispatcher != null) {
        dispatcher.dispatch(new DispatchAction(subscriber, event));
      } else {
        throw new RuntimeException("dispatcher not found!");
      }
    }
  }

  private Dispatcher findDispatcher(int id) {
    for (Dispatcher dispatcher : dispatchers) {
      if (dispatcher.identifier() == id) {
        return dispatcher;
      }
    }
    return null;
  }
}
