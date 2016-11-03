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
 * Registry of subscribers.
 *
 * @author biaowu.
 */
public interface Registry {
  /**
   * Registers all methods that annotated by {@link Subscribe} to receive events.
   */
  void register(Object listener);

  /**
   * Unregisters all methods that annotated by {@link Subscribe}.
   */
  void unregister(Object listener);

  /**
   * Get all subscribers which listen the given event.
   */
  List<Subscriber> getSubscribers(Object event);
}
