package lab4.sectionB01.daniellovegrove.logic;

import lab4.sectionB01.daniellovegrove.entity.Event;

public interface EventList
{
    public boolean add(Event e);
    public Event removeNext();
    public String toString();
    public Event removeTimer(int entity);
    public double getLastPacketTime(int entityTo);
}
