package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.StartInfo;

/** A game is starting; carries your mark and whether you start. */
public class StartEvent {
    private final StartInfo info;

    public StartEvent(StartInfo info) {
        this.info = info;
    }

    public StartInfo getInfo() {
        return info;
    }
}
