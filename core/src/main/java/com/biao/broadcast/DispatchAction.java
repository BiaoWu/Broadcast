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

import java.lang.reflect.InvocationTargetException;

/**
 * @author biaowu.
 */
class DispatchAction implements Runnable {
  final Subscriber subscriber;
  final Object event;

  DispatchAction(Subscriber subscriber, Object event) {
    this.subscriber = subscriber;
    this.event = event;
  }

  @Override
  public void run() {
    try {
      subscriber.dispatchEvent(event);
    } catch (InvocationTargetException e) {
      // TODO: 2016/11/9
    }
  }
}
