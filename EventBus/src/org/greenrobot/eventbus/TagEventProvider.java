package org.greenrobot.eventbus;


import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by pool on 2016/7/12.
 */
public class TagEventProvider implements ExtraEventProvider {
    public static final String TAG = EventBus.TAG;

    private final Map<TagEventKey, CopyOnWriteArrayList<Subscription>> tagSubscriptionsByEventType;
    private final Map<Object, List<TagEventKey>> tagTypesBySubscriber;
    private final Map<TagEventKey, TagEvent> stickyEvents;


    TagEventProvider() {
        tagSubscriptionsByEventType = new HashMap<>();
        tagTypesBySubscriber = new HashMap<>();
        stickyEvents = new HashMap<>();
    }

    @Override
    public boolean interesting(Object event) {
        boolean interesting = event != null && TagEvent.class == event.getClass();
        return interesting;
    }

    @Override
    public boolean interesting(SubscriberMethod subscriberMethod) {
        return subscriberMethod.tag != null && !subscriberMethod.tag.equals("");
    }


    @Override
    public void subscribe(Object subscriber, SubscriberMethod subscriberMethod) {
        List<TagEventKey> types = tagTypesBySubscriber.get(subscriber);
        TagEventKey eventKey = new TagEventKey(subscriberMethod.tag, subscriberMethod.eventType);
        if (types == null) {
            types = new ArrayList<>();
            types.add(eventKey);
            tagTypesBySubscriber.put(subscriber, types);
        } else {
            if (types.contains(eventKey)) {
                //already register
            } else {
                types.add(eventKey);
            }
        }
        CopyOnWriteArrayList<Subscription> list = tagSubscriptionsByEventType.get(eventKey);
        if (list == null) {
            list = new CopyOnWriteArrayList<>();
            tagSubscriptionsByEventType.put(eventKey, list);
        }
        list.add(new Subscription(subscriber, subscriberMethod));
    }

    @Override
    public boolean unsubscribe(Object subscriber) {
        List<TagEventKey> keys = tagTypesBySubscriber.get(subscriber);
        if (keys != null) {
            Subscription subscription;
            TagEventKey key;
            CopyOnWriteArrayList<Subscription> subscriptions;
            int size;
            for (int i = 0; i < keys.size(); i++) {
                key = keys.get(i);
                subscriptions = tagSubscriptionsByEventType.get(key);
                size = subscriptions.size();
                for (int j = 0; j < size; j++) {
                    subscription = subscriptions.get(j);
                    if (subscription.subscriber == subscriber) {
                        subscription.active = false;
                        subscriptions.remove(j);
                        j--;
                        size--;
                    }
                }
                if (subscriptions.isEmpty())
                    tagSubscriptionsByEventType.remove(key);
            }
            tagTypesBySubscriber.remove(subscriber);
            return true;
        }
        return false;
    }

    @Override
    public CopyOnWriteArrayList<Subscription> getSubscription(Object object, Class<?> eventClass) {
        TagEvent tagEvent = (TagEvent) object;
        TagEventKey key = new TagEventKey(tagEvent.tag, eventClass);
        return tagSubscriptionsByEventType.get(key);
    }

    @Override
    public Class<?> getClass(Object event) {
        TagEvent tagEvent = (TagEvent) event;
        if (tagEvent.sticky) {
            stickyEvents.put(new TagEventKey(tagEvent.tag, tagEvent.event == null ? null : tagEvent.event.getClass()), tagEvent);
            Log.d(TAG, "add new sticky TagEvent " + (tagEvent.event != null ? tagEvent.event.toString() : " null event"));
        }
        return tagEvent.event != null ? tagEvent.event.getClass() : null;
    }

    @Override
    public Object getEvent(Object event) {
        return ((TagEvent) event).event;
    }

    @Override
    public List<?> getStickyEvent(SubscriberMethod subscriberMethod, boolean eventInheritance) {
        if (!subscriberMethod.sticky)
            return null;
        List<Object> result = new ArrayList<>();
        TagEvent stickyEvent;
        Class<?> eventType = subscriberMethod.eventType;
        if (eventInheritance) {
            Set<Map.Entry<TagEventKey, TagEvent>> entries = stickyEvents.entrySet();
            for (Map.Entry<TagEventKey, TagEvent> entry : entries) {
                TagEventKey candidateEventType = entry.getKey();
                if ((subscriberMethod.tag != null ? subscriberMethod.tag.equals(candidateEventType.tag) : candidateEventType.tag == null)
                        && ((candidateEventType.eventType != null && eventType != null) ? eventType.isAssignableFrom(candidateEventType.eventType) : candidateEventType.eventType == eventType)) {
                    stickyEvent = entry.getValue();
                    result.add(stickyEvent.event);
                }
            }
        } else {
            stickyEvent = stickyEvents.get(new TagEventKey(subscriberMethod.tag, subscriberMethod.eventType));
            result.add(stickyEvent.event);
        }
        return result;
    }


    private static class TagEventKey {
        final String tag;
        final Class<?> eventType;

        TagEventKey(String tag, Class<?> eventType) {
            this.tag = tag;
            this.eventType = eventType;
        }

        @Override
        public int hashCode() {
            int hashCode = tag.hashCode();
            if (eventType != null)
                hashCode ^= eventType.hashCode();
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj instanceof TagEventKey) {
                TagEventKey anObject = (TagEventKey) obj;
                return tag.equals(anObject.tag) && eventType == anObject.eventType;
            }
            return false;
        }
    }


}
