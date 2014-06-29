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

public class HotelSystem implements Runnable {

	private HashMap<Integer, String> callLog;
	private SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm:ss");
	private String wakeUpMessage = "This is your automatic wake up call for room number: ";
	private String nextCall;
	private Thread thread;
	private String currentTime;

	public HotelSystem() {
		callLog = new LinkedHashMap<Integer, String>();
		this.thread = new Thread(this);
		this.thread.start();
		setAlarmTime(4, "16:20:01");
		setAlarmTime(5, "16:25:01");
	}

	@Override
	public void run() {
		while (true) {
			currentTime = getCurrentTime();
			if (callLog.containsValue(currentTime)) {
				for (Entry<Integer, String> entry : callLog.entrySet()) {
		            if (entry.getValue().equals(currentTime)) {
		                sendAlarmCall(entry.getKey());
		            }
		        }
			}
			
			// rest for 5 minutes to conserve memory
			try {
				Thread.sleep(300000);
				System.out.println("Thread is sleeping");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Thread is awake");
		}
	}

	// sends a generic alarm call message followed by the room number
	public void sendAlarmCall(int roomNumber) {
		System.out.println(wakeUpMessage + roomNumber);
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
			return null;
		}
	}

	public String getCurrentTime() {
		Date time = Calendar.getInstance().getTime();
		String currentTIme = timeFormat.format(time);
		return currentTIme;
	}

	public void setAlarmTime(int roomNumber, String time) {
		Date alarmDate;
		try {
			alarmDate = timeFormat.parse(time);
			String alarmTime = timeFormat.format(alarmDate);
			callLog.put(roomNumber, alarmTime);
			System.out.println("You entered room number: " + roomNumber + " and wake up time of: " + alarmTime);
		} catch (ParseException e) {
			System.out.println("Please ensure you seperate the time with colons eg 07:30:00.");
		}

	}

	public void cancelAlarm(int roomNumber) {
		if (callLog.containsKey(roomNumber)) {
			callLog.remove(roomNumber);
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

	public static void main(String[] args) {
		new HotelSystem();
	}
}
