package org.greenrobot.eventbus;

/**
 * Created by pool on 2016/7/11.
 */
public final class TagEvent {
    final String tag;
    final Object event;

    public TagEvent(String tag, Object event) {
        this.event = event;
        this.tag = tag;
    }
}
