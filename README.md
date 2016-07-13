EventBus
============================
add tag support.

**Usage**
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

