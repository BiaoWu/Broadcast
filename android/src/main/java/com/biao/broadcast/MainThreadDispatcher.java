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

import android.os.Looper;

/**
 * Just dispatch on Android Main Thread.
 *
 * @author biaowu.
 */
public class MainThreadDispatcher extends HandlerDispatcher {
  public static final int ID_MAIN_THREAD = 665201314;

  public MainThreadDispatcher() {
    super(Looper.getMainLooper());
  }

  @Override
  public int identifier() {
    return ID_MAIN_THREAD;
  }
}
