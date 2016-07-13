package org.greenrobot.eventbus;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by pool on 2016/7/12.
 */
public interface ExtraEventProvider {


    boolean interesting(Object event);


    boolean interesting(SubscriberMethod subscriberMethod);

    void subscribe(Object subscriber, SubscriberMethod subscriberMethod);


    boolean unsubscribe(Object subscriber);

    CopyOnWriteArrayList<Subscription> getSubscription(Object object,Class<?> eventClass);


    Class<?> getClass(Object event);

    Object getEvent(Object event);

}
