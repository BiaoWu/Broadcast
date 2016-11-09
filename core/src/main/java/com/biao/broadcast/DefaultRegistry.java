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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Default Registry of subscribers.
 *
 * @author biaowu.
 */
final class DefaultRegistry implements Registry {

  private static final Map<Class<?>, List<SubscribeMethod>> subscriberMethodsCache =
      new WeakHashMap<>();
  private static final Map<Class<?>, List<Class<?>>> eventTypeClassCache = new WeakHashMap<>();

  private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers =
      new ConcurrentHashMap<>();

  @Override
  public void register(Object listener) {
    Map<Class<?>, Set<Subscriber>> allSubscribers = findAllSubscribers(listener);

    for (Map.Entry<Class<?>, Set<Subscriber>> entry : allSubscribers.entrySet()) {
      Class<?> eventType = entry.getKey();
      CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);

      if (eventSubscribers == null) {
        eventSubscribers = new CopyOnWriteArraySet<>();
        subscribers.put(eventType, eventSubscribers);
      }
      eventSubscribers.addAll(entry.getValue());
    }
  }

  @Override
  public void unregister(Object listener) {
    Map<Class<?>, Set<Subscriber>> allSubscribers = findAllSubscribers(listener);
    for (Map.Entry<Class<?>, Set<Subscriber>> entry : allSubscribers.entrySet()) {
      Class<?> eventType = entry.getKey();

      CopyOnWriteArraySet<Subscriber> currentSubscribers = this.subscribers.get(eventType);

      if (currentSubscribers == null || !currentSubscribers.removeAll(entry.getValue())) {
        throw new IllegalArgumentException("missing event subscriber in " + listener);
      }
    }
  }

  @Override
  public List<Subscriber> getSubscribers(Object event) {
    List<Class<?>> eventTypes = getEventTypes(event.getClass());
    CopyOnWriteArrayList<Subscriber> subscribers = null;
    for (Class<?> eventType : eventTypes) {
      CopyOnWriteArraySet<Subscriber> typeSubscribers = this.subscribers.get(eventType);
      if (typeSubscribers != null && typeSubscribers.size() > 0) {
        if (subscribers == null) {
          subscribers = new CopyOnWriteArrayList<>();
        }
        subscribers.addAllAbsent(typeSubscribers);
      }
    }
    return subscribers == null ? Collections.<Subscriber>emptyList()
        : Collections.unmodifiableList(new ArrayList<>(subscribers));
  }

  /* package for test */ List<Subscriber> getSubscribers(Class<?> eventType) {
    CopyOnWriteArraySet<Subscriber> subscribers = this.subscribers.get(eventType);
    return subscribers == null || subscribers.size() == 0 ? Collections.<Subscriber>emptyList()
        : Collections.unmodifiableList(new ArrayList<>(subscribers));
  }

  private Map<Class<?>, Set<Subscriber>> findAllSubscribers(Object listener) {
    Map<Class<?>, Set<Subscriber>> all = new HashMap<>();
    Class<?> clazz = listener.getClass();
    for (SubscribeMethod subscribeMethod : getAnnotatedMethods(clazz)) {
      Class<?> eventType = subscribeMethod.eventType;

      Set<Subscriber> subscribers = all.get(eventType);
      if (subscribers == null) {
        subscribers = new HashSet<>();
        all.put(eventType, subscribers);
      }
      subscribers.add(new Subscriber(listener, subscribeMethod));
    }
    return all;
  }

  private List<SubscribeMethod> getAnnotatedMethods(Class<?> clazz) {
    List<SubscribeMethod> methods = subscriberMethodsCache.get(clazz);
    if (methods != null) {
      return methods;
    }

    methods = getAnnotatedMethodsNotCached(clazz);
    if (methods.size() > 0) {
      subscriberMethodsCache.put(clazz, methods);
    }
    return methods;
  }

  private List<SubscribeMethod> getAnnotatedMethodsNotCached(final Class<?> original) {
    List<Class<?>> supertypes = getSuperTypes(original);

    List<SubscribeMethod> subscriberMethods = new ArrayList<>();
    List<MethodIdentifier> skipMethods = new ArrayList<>();

    for (Class<?> superType : supertypes) {
      for (Method method : superType.getDeclaredMethods()) {
        Subscribe subscribe = method.getAnnotation(Subscribe.class);
        if (subscribe != null && !method.isSynthetic()) {
          Class<?>[] parameterTypes = method.getParameterTypes();
          if (parameterTypes.length != 1) {
            throw new IllegalArgumentException("Subscriber methods must have exactly 1 parameter.");
          }

          MethodIdentifier identifier = new MethodIdentifier(method);
          if (skipMethods.contains(identifier)) {
            continue;
          }

          SubscribeInfo subscribeInfo = annotationInfo(subscribe);
          if (subscribeInfo.override) {
            skipMethods.add(identifier);
          }
          subscriberMethods.add(new SubscribeMethod(method, parameterTypes[0], subscribeInfo));
        }
      }
    }
    return subscriberMethods.size() > 0 ? Collections.unmodifiableList(subscriberMethods)
        : Collections.<SubscribeMethod>emptyList();
  }

  private SubscribeInfo annotationInfo(Subscribe subscribe) {
    return new SubscribeInfo(subscribe.override(), subscribe.dispatcher());
  }

  private List<Class<?>> getEventTypes(Class<?> eventType) {
    List<Class<?>> eventTypes = eventTypeClassCache.get(eventType);
    if (eventTypes != null) {
      return eventTypes;
    }

    eventTypes = getSuperTypes(eventType);
    if (eventTypes.size() > 0) {
      eventTypeClassCache.put(eventType, eventTypes);
    }
    return eventTypes;
  }

  private List<Class<?>> getSuperTypes(Class<?> original) {
    List<Class<?>> types = new ArrayList<>();
    Class<?> currentClass = original;
    while (currentClass != null) {
      types.add(currentClass);
      currentClass = currentClass.getSuperclass();
    }
    return types.size() > 0 ? Collections.unmodifiableList(types)
        : Collections.<Class<?>>emptyList();
  }

  private static final class MethodIdentifier {

    private final String name;
    private final List<Class<?>> parameterTypes;

    MethodIdentifier(Method method) {
      this.name = method.getName();
      Class<?>[] parameterTypes = method.getParameterTypes();
      this.parameterTypes = Arrays.asList(parameterTypes);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(new Object[] { name, parameterTypes });
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof MethodIdentifier) {
        MethodIdentifier identifier = (MethodIdentifier) o;
        return name.equals(identifier.name) && parameterTypes.equals(identifier.parameterTypes);
      }
      return false;
    }
  }
}
