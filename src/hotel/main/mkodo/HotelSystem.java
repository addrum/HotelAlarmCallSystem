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
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class HotelSystem extends Thread {

    private HashMap<Integer, String> callLog;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm:ss");
    private final String wakeUpMessage = "This is your automatic wake up call for room number(s): ";
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
                for (Entry<Integer, String> entry : callLog.entrySet()) {
                    String numbers = "";
                    if (entry.getValue().equals(currentTime)) {
                        if (numbers.equals("")) {
                            numbers += " + " + entry.getKey();
                        } else {
                            numbers = "" + entry.getKey();
                        }
                        sendAlarmCall(numbers);
                        //sendAlarmCall(entry.getKey());
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
    private void sendAlarmCall(String numbers) {
        viewPanel.setText(wakeUpMessage + numbers);
        //callLog.remove(roomNumber);
    }

    // uses the iterator to get to first key:value pair in the hashmap
    public String getNextCallRequest() {
        Iterator it = callLog.entrySet().iterator();
        if (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            nextCall = "Room Number: " + pairs.getKey() + " - Time: " + pairs.getValue();
            it.remove(); // avoids a ConcurrentModificationException
            return nextCall;
        } else {
            nextCall = "null";
            return nextCall;
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
            viewPanel.setText("Please ensure you seperate the time with colons eg 07:30:00");
        }
    }

    public void cancelAlarm(int roomNumber) {
        if (callLog.containsKey(roomNumber)) {
            callLog.remove(roomNumber);
            viewPanel.setText("Alarm cancelled for room number:" + roomNumber);
        } else {
            viewPanel.setText("No alarm exists for that room number.");
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
