package observers;

import jade.GameObject;
import observers.events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {
    private static ThreadLocal<List<Observer>>  observers = new ThreadLocal<>(){
        @Override
        protected List<Observer> initialValue()
        {
            return new ArrayList<>();
        }
    };

    public static void addObserver(Observer observer) {
        observers.get().add(observer);
    }

    public static void notify(GameObject obj, Event event) {
        for (Observer observer : observers.get()) {
            observer.onNotify(obj, event);
        }
    }
}
