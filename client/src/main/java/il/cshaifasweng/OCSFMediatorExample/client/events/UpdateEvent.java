package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.MoveInfo;

/** A move was applied; carries the cell, mark and whose turn is next. */
public class UpdateEvent {
    private final MoveInfo info;

    public UpdateEvent(MoveInfo info) {
        this.info = info;
    }

    public MoveInfo getInfo() {
        return info;
    }
}
