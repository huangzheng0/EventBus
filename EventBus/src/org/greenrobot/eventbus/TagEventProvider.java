package org.greenrobot.eventbus;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by pool on 2016/7/12.
 */
public class TagEventProvider implements ExtraEventProvider {
    private final Map<TagEventKey, CopyOnWriteArrayList<Subscription>> tagSubscriptionsByEventType;
    private final Map<Object, List<TagEventKey>> tagTypesBySubscriber;

    TagEventProvider() {
        tagSubscriptionsByEventType = new HashMap<>();
        tagTypesBySubscriber = new HashMap<>();
    }

    @Override
    public boolean interesting(Object event) {
        return event != null && TagEvent.class==event.getClass();
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
            tagTypesBySubscriber.put(subscriber,types);
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
        return  tagEvent.event!=null ? tagEvent.event.getClass():null ;
    }

    @Override
    public Object getEvent(Object event) {
        return ((TagEvent) event).event;
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
