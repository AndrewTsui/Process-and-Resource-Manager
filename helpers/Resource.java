package helpers;
import java.util.*;

public class Resource {
	private Integer rid;
	private Integer state;
	private Integer maxInventory;
	private ArrayList<Process> waitlist;
	public HashMap<Process, Integer> requested;

	public Resource(Integer newRid, Integer newInventory) {
		rid = newRid;
		maxInventory = newInventory;
		state = newInventory;
		waitlist = new ArrayList<Process>();
		requested = new HashMap<Process, Integer>();
	}

	public Integer getRid() {
		return rid;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer newState) {
		state = newState;
	}

	public Integer getMaxInventory() {
		return maxInventory;
	}

	public void addProcess(Process p) {
		waitlist.add(p);
	}

	public void addProcessToFront(Process p) {
		waitlist.add(0, p);
	}

	public ArrayList<Process> getWaitlist() {
		return waitlist;
	}

	public boolean hasNextBlockedProcess() {
		return !waitlist.isEmpty();
	}

	public Process getNextBlockedProcess() {
		return waitlist.remove(0);
	}

	public void removeBlockedProcess(Process process) {
		waitlist.remove(process);
	}

	public void addRequested(Process process, Integer units) {
		requested.put(process, units);
	}

	public Integer getRequested(Process process) {
		return requested.get(process);
	}

	public void removeRequested(Process process) {
		requested.remove(process);
	}

	public Process unblockProcesses() {
		if(hasNextBlockedProcess()) {
			if(getRequested(waitlist.get(0)) <= getState()) {
				Process process = getNextBlockedProcess();
				setState(getState() - getRequested(process));
				removeRequested(process);
				return process;
			}
		}
		return null;
	}

	public void printWaitlist() {
		if(!waitlist.isEmpty()) {
			for(int i = 0; i < waitlist.size(); i++) {
				System.out.println(String.valueOf(waitlist.get(i).getPid()) + " wants " + String.valueOf(getRequested(waitlist.get(i)) + " resources"));
			}
		}
		
	}
}