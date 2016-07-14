package org.greenrobot.eventbus;

/**
 * Created by pool on 2016/7/11.
 */
public final class TagEvent {
    final String tag;
    final Object event;
    boolean sticky = false;

    public TagEvent(String tag, Object event) {
         this(tag,event,false);
    }

    public TagEvent(String tag, Object event,boolean sticky) {
        this.event = event;
        this.tag = tag;
        this.sticky = sticky;
    }
}
