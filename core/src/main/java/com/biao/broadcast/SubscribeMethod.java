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
 * A method wrapper.
 *
 * @author biaowu.
 */
final class SubscribeMethod {
  final Method method;
  final Class<?> eventType;
  final SubscribeInfo info;

  SubscribeMethod(Method method, Class<?> eventType, SubscribeInfo info) {
    this.method = method;
    this.eventType = eventType;
    this.info = info;
  }

  @Override
  public final int hashCode() {
    return method.hashCode();
  }

  @Override
  public final boolean equals(Object obj) {
    if (obj instanceof SubscribeMethod) {
      SubscribeMethod that = (SubscribeMethod) obj;
      return method == that.method;
    }
    return false;
  }
}
