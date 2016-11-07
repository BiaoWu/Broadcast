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

import java.lang.reflect.Method;

/**
 * A Subscriber method on a specific object.
 *
 * @author biaowu.
 */
class Subscriber {
  final Object listener;
  final Method method;

  Subscriber(Object listener, Method method) {
    this.listener = listener;
    this.method = method;
  }

  @Override
  public final int hashCode() {
    return (31 + method.hashCode()) * 31 + System.identityHashCode(listener);
  }

  @Override
  public final boolean equals(Object obj) {
    if (obj instanceof Subscriber) {
      Subscriber that = (Subscriber) obj;
      return listener == that.listener && method.equals(that.method);
    }
    return false;
  }
}
