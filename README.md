EventBus
============================
add tag support.

**Usage**

Base on EventBus3.0.
TagEvent support APT.

```java

@Subscribe(tag = "tag_you_want")
public void onEvent(){
     //support empty parameter
     //EventBus.getDefault().postTagEvent("tag_you_want");
} 

@Subscribe(tag = "tag_you_want")
public void onEvent(Event event){
    // EventBus.getDefault().postTagEvent("tag_you_want",event);
}

```