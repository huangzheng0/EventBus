EventBus
============================
add tag support.

**Usage**

<code>@Subscribe(tag = "tag_you_want")</code><br/>
<code>public void onEvent(){<br/>
<br/>
     //support empty parameter
     //EventBus.getDefault().postTagEvent("tag_you_want");
<br/>   
}</code>


<code>@Subscribe(tag = "tag_you_want")</code><br/>
<code>public void onEvent(Event event){<br/>
<br/>
    // EventBus.getDefault().postTagEvent("tag_you_want",event);
<br/>   
}</code>

