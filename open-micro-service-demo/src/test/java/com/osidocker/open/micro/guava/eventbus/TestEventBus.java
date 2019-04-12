package com.osidocker.open.micro.guava.eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.Test;

/**
 * @author Administrator
 * @creato 2019-04-09 22:02
 */
public class TestEventBus {

    @Test
    public void testEventBus(){
        EventBus event = new EventBus("draw");
        EventListener listener = new EventListener();
        EventListener listener1 = new EventListener();
        event.register(listener);
        event.register(listener1);
        event.post(new TestEvent(100));
        event.post(new TestEvent(200));
        event.post(new TestEvent(300));
    }

    public class EventListener {
        public int lastMessage = 0;

        @Subscribe
        public void listen(TestEvent event) {
            if( event.message == 300 ){
                lastMessage = event.getMessage();
                System.out.println("Message:"+lastMessage);
            }
        }

        @Subscribe
        public void have(TestEvent event){
            if( event.message==100 ){
                System.out.println(event.message);
            }
        }

        public int getLastMessage() {
            return lastMessage;
        }
    }

    public class TestEvent {
        private final int message;
        public TestEvent(int message) {
            this.message = message;
        }
        public int getMessage() {
            return message;
        }
    }
}
