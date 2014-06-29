package hotel.main.mkodo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class HotelSystem extends Thread {

    private HashMap<Integer, String> callLog;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm:ss");
    private final String wakeUpMessage = "This is your automatic wake up call for room number: ";
    private String nextCall;
    private Thread thread;
    private String currentTime;
    private JTextArea viewPanel;

    public HotelSystem(JTextArea viewPanel) {
        this.viewPanel = viewPanel;
        callLog = new LinkedHashMap<Integer, String>();
    }

    @Override
    public void run() {
        while (true) {
            currentTime = getCurrentTime();
            if (callLog.containsValue(currentTime)) {
                StringBuilder numbers = new StringBuilder();
                for (Entry<Integer, String> entry : callLog.entrySet()) {
                    if (entry.getValue().equals(currentTime)) {
                        System.out.println(entry.getKey());
                        sendAlarmCall(entry.getKey());
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    // sends a generic alarm call message followed by the room number
    private void sendAlarmCall(int numbers) {
        viewPanel.append(wakeUpMessage + numbers + "\n");
    }

    // uses the iterator to get to first key:value pair in the hashmap
    public String getNextCallRequest() {
        try {
            Object key = callLog.keySet().iterator().next();
            Object value = callLog.get(key);
            return "Next wake up call for room number: " + key + " at: " + value;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public String getCurrentTime() {
        Date time = Calendar.getInstance().getTime();
        String currentTIme = timeFormat.format(time);
        return currentTIme;
    }

    public void setAlarmTime(int roomNumber, String time) {
        try {
            Date alarmDate = timeFormat.parse(time);
            String alarmTime = timeFormat.format(alarmDate);
            callLog.put(roomNumber, alarmTime);
        } catch (ParseException e) {
            viewPanel.append("Please ensure you seperate the time with colons eg 07:30:00\n");
        }
    }

    public void cancelAlarm(int roomNumber) {
        if (callLog.containsKey(roomNumber)) {
            callLog.remove(roomNumber);
            viewPanel.append("Alarm cancelled for room number:" + roomNumber + "\n");
        } else {
            viewPanel.append("No alarm exists for that room number.\n");
        }
    }

    public void printHashMap() {
        Iterator it = callLog.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            nextCall = "Room Number: " + pairs.getKey() + " - Time: " + pairs.getValue();
            System.out.println(nextCall);
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
}
