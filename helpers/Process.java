package helpers;
import java.util.*;

public class Process {
	private Integer pid;
	private Integer state;
	private Integer priority;
	private Process parent;
	private ArrayList<Process> children;
	private HashMap<Integer, Integer> resources;

	public Process(Integer newPid, Integer newState, Integer newPriority, Process newParent) {
		pid = newPid;
		state = newState;
		priority = newPriority;
		parent = newParent;
		children = new ArrayList<>();
		resources = new HashMap<Integer, Integer>();
		resources.put(0, 0);
		resources.put(1, 0);
		resources.put(2, 0);
		resources.put(3, 0);
	}

	public Integer getPid() {
		return pid;
	}
	
	public Integer getState() {
		return state;
	}
	
	public Process getParent() {
		return parent;
	}

	public Integer getPriority() {
		return priority;
	}

	public ArrayList<Process> getChildren() {
		return children;
	}

	public void setPid(Integer newPid) {
		pid = newPid;
	}

	public void setState(Integer newState) {
		state = newState;
	}

	public void addChild(Process child) {
		children.add(child);
	}

	public void removeChild(Process child) {
		children.remove(child);
	}

	public void addResource(Resource resource, Integer units) {
		resources.put(resource.getRid(), resources.get(resource.getRid()) + units);
	}	

	public Integer getUnitsHeld(Resource resource) {
		return resources.get(resource.getRid());
	}

	public void removeResource(Resource resource, Integer units) {
		resources.put(resource.getRid(), resources.get(resource.getRid()) - units);
	}

	public HashMap<Integer, Integer> getResources() {
		return resources;
	}

	public void printResources() {
		System.out.println(String.valueOf(getPid()));
		System.out.println("Rescource 0: " + String.valueOf(resources.get(0)));
		System.out.println("Rescource 1: " + String.valueOf(resources.get(1)));
		System.out.println("Rescource 2: " + String.valueOf(resources.get(2)));
		System.out.println("Rescource 3: " + String.valueOf(resources.get(3)));
	}
}