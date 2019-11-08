import helpers.Resources;
import helpers.Resource;
import helpers.ReadyList;
import helpers.Process;
import java.io.*;
import java.util.*;

public class Scheduler {
	public static Integer PCB[];
	public static Integer RCB[];
	public static ReadyList readylist;
	public static HashMap<Integer, Process> processes;
	public static Resources resources;
	public static Process currentProcess;
	public static FileOutputStream out;

	public static void outputToFile(FileOutputStream file, String out) {
		try {
			file.write(out.getBytes());
		}
		catch(IOException e) {
			System.out.println("IOException");
			System.exit(1);
		}
	}

	public static void init() {
		PCB = new Integer[16];
		Arrays.fill(PCB, -1);
		RCB = new Integer[4];
		processes = new HashMap<Integer, Process>();
		resources = new Resources();
		readylist = new ReadyList();
		currentProcess = null;
		PCB[0] = 0;
		Process init = new Process(0, 0, 0, null);
		readylist.addProcess(0, init);
		processes.put(0, init);
		currentProcess = readylist.getNextProcess();
		outputToFile(out, String.valueOf(currentProcess.getPid()) + " ");
	}

	public static void create(Integer priority) {
		// allocate new PCB[j]
		int index;
		for(index = 0; index < 16; index++) {
			if(PCB[index] == -1)
				break;
		}
		PCB[index] = 0;
		// sets state, parent, priority, children = null, resources = null
		Process newProcess = new Process(index, 0, priority, currentProcess);
		// inserts j into children of i
		currentProcess.addChild(newProcess);
		// inserts j into RL
		readylist.addProcess(newProcess.getPriority(), newProcess);
		// adds process to all running processes
		processes.put(index, newProcess);
		System.out.println("Process " + String.valueOf(newProcess.getPid()) + " created and is child of " + String.valueOf(currentProcess.getPid()));
		for(int i = 0; i < currentProcess.getChildren().size(); i++) {
			System.out.print(currentProcess.getChildren().get(i).getPid());
		}
	}

	public static void destroy(Process parent, Process child) {
		System.out.println("DELETEING PROCESS " + child.getPid() + " WHO IS CHILD OF " + parent.getPid());
		// remove child from parent's children
		parent.removeChild(child);
		// remove child and all grandchildren from RL
		readylist.destroyProcess(child);
		// remove child from all running processes
		processes.remove(child.getPid());
		// release resources
		HashMap<Integer, Integer> resourceMap = child.getResources();
		for(int i = 0; i < 4; i++) {
			if(resourceMap.get(i) != 0) {
				release(child, i, resourceMap.get(i));
			}
		}
		// free PCB index
		PCB[child.getPid()] = -1;
		for(int i = 0; i < child.getChildren().size(); i++) {
			destroy(child, child.getChildren().get(i));
			i--;
		}
	}

	public static void request(Integer r, Integer units) {
		// get resource requested
		Resource resource = resources.getResource(r);
		// if number of units in resource >= number of units requested
		if(resource.getState() >= units) {
			// add resource to process' resources
			currentProcess.addResource(resource, units);
			// set resource's inventory = inventory - units requested
			resource.setState(resource.getState() - units);
			System.out.println("Resource " + resource.getRid() + " allocated");
			scheduler(false);
		}
		else {
			// remove process from readylist
			readylist.removeProcess(currentProcess.getPriority(), currentProcess);
			currentProcess.setState(1);
			// add process to resource waitlist
			resource.addProcess(currentProcess);
			// add process and units to requested data structure
			resource.addRequested(currentProcess, units);
			System.out.println("Process " + currentProcess.getPid() + " blocked");
			scheduler(true);
		}
		
	}

	public static void release(Process process, Integer r, Integer units) {
		// get resource released
		Resource resource = resources.getResource(r);
		// remove resource from current process' resources
		process.removeResource(resource, units);
		// set resource state = state + number of units released
		resource.setState(resource.getState() + units);
		// loop through processes in waitlist
		while(resource.hasNextBlockedProcess()) {
			Process unblockedProcess = resource.getNextBlockedProcess();
			// if number of resources process i requested <= inventory
			if(resource.getRequested(unblockedProcess) <= resource.getState()) {
				// add unblocked process to readylist
				readylist.addProcess(unblockedProcess.getPriority(), unblockedProcess);
				// set unblocked process state to ready
				unblockedProcess.setState(0);
				// add resource to unblocked process' resources
				unblockedProcess.addResource(resource, resource.getRequested(unblockedProcess));
				// set resource state = state - number of units unblocked process requested
				resource.setState(resource.getState() - resource.getRequested(unblockedProcess));
				// remove unblocked process from resource's requested data structure
				resource.removeRequested(unblockedProcess);
			}
			// break if number of requested units > state, don't skip process because FIFO
			else {
				resource.addProcessToFront(unblockedProcess);
				break;
			}
		}
		System.out.println("Resource " + resource.getRid() + " released");
	}

	public static void timeout() {
		readylist.addProcess(currentProcess.getPriority(), currentProcess);
		scheduler(true);
	}

	public static void scheduler(boolean timeout) {
		if(!readylist.isEmpty()) {
			if(timeout)
				currentProcess = readylist.getNextProcess();
			else {
				Process nextAvailableProcess = readylist.getNextProcess();
				if(nextAvailableProcess.getPriority() > currentProcess.getPriority()) {
					readylist.addProcessToFront(currentProcess.getPriority(), currentProcess);
					currentProcess = nextAvailableProcess;
				}
				else
					readylist.addProcessToFront(nextAvailableProcess.getPriority(), nextAvailableProcess);
			}
		}
		outputToFile(out, String.valueOf(currentProcess.getPid()) + " ");
	}	

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File file = new File(args[0]);
		Scanner scanner = new Scanner(file);
		String command;
		Integer commandSupplement1;
		Integer commandSupplement2;
		boolean first = true;
		out = new FileOutputStream(args[1]);
		while(scanner.hasNext()) {
			command = scanner.next();
			switch(command) {
				case "in":
					if(!first)
						outputToFile(out, "\n");
					first = false;
					init();
					break;
				case "cr":
					commandSupplement1 = scanner.nextInt();
					if(commandSupplement1 < 1 || commandSupplement1 > 2 || processes.size() >= 16) {
						outputToFile(out, "-1 ");
						break;
					}
					create(commandSupplement1);
					scheduler(false);
					break;
				case "rl":
					commandSupplement1 = scanner.nextInt();
					commandSupplement2 = scanner.nextInt();
					if(currentProcess.getPriority() == 0 || commandSupplement1 < 0 || commandSupplement1 > 3) {
						outputToFile(out, "-1 ");
						break;
					}
					if(currentProcess.getUnitsHeld(resources.getResource(commandSupplement1)) < commandSupplement2) {
						outputToFile(out, "-1 ");
						break;
					}
					release(currentProcess, commandSupplement1, commandSupplement2);
					scheduler(false);
					break;
				case "rq":
					commandSupplement1 = scanner.nextInt();
					commandSupplement2 = scanner.nextInt();
					if(currentProcess.getPriority() == 0 || commandSupplement2 <= 0 || commandSupplement1 < 0 || commandSupplement1 > 3) {
						outputToFile(out, "-1 ");
						break;
					}
					if(commandSupplement2 + currentProcess.getUnitsHeld(resources.getResource(commandSupplement1)) > resources.getResource(commandSupplement1).getMaxInventory()) {
						outputToFile(out, "-1 ");
						break;
					}
					request(commandSupplement1, commandSupplement2);
					break;
				case "de":
					commandSupplement1 = scanner.nextInt();
					if(!currentProcess.getChildren().contains(processes.get(commandSupplement1))) {
						outputToFile(out, "-1 ");
						break;
					}
					destroy(currentProcess, processes.get(commandSupplement1));
					scheduler(false);
					break;
				case "to":
					timeout();
					break;
			}
			for(int i = 0; i < processes.size(); i++) {
				if(processes.get(i) != null)
					processes.get(i).printResources();
			}
			readylist.print();
			resources.print();
		}
		out.close();
		scanner.close();
	}
}