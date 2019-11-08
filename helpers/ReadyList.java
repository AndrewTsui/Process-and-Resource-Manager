package helpers;
import java.util.*;

public class ReadyList {
	private ArrayList<Process> list2;
	private ArrayList<Process> list1;
	private ArrayList<Process> list0;

	public ReadyList() {
		list2 = new ArrayList<>();
		list1 = new ArrayList<>();
		list0 = new ArrayList<>();
	}

	public void addProcess(Integer priority, Process newProcess) {
		if(priority == 2)
			list2.add(newProcess);
		else if(priority == 1)
			list1.add(newProcess);
		else
			list0.add(newProcess);
	}

	public void removeProcess(Integer priority, Process process) {
		if(priority == 2)
			list2.remove(process);
		else if(priority == 1)
			list1.remove(process);
	}

	public void destroyProcess(Process process) {
		for(int i = 0; i < process.getChildren().size(); i++) {
			removeProcess(process.getChildren().get(i).getPriority(), process.getChildren().get(i));
		}
		removeProcess(process.getPriority(), process);
	}

	public void addProcessToFront(Integer priority, Process process) {
		if(priority == 2)
			list2.add(0, process);
		if(priority == 1)
			list1.add(0, process);
		if(priority == 0)
			list0.add(0, process);
	}

	public Process getNextProcess() {
		if(!list2.isEmpty())
			return list2.remove(0);
		if(!list1.isEmpty())
			return list1.remove(0);
		return list0.remove(0);
	}

	public Process findNextProcess() {
		if(!list2.isEmpty())
			return list2.get(0);
		if(!list1.isEmpty())
			return list1.get(0);
		return list0.get(0);
	}

	public boolean isEmpty() {
		return list2.isEmpty() && list1.isEmpty() && list0.isEmpty();
	}

	public void print() {
		System.out.println("Priority 2: ");
		for(int i = 0; i < list2.size(); i++) {
			System.out.print(String.valueOf(list2.get(i).getPid()) + " ");
		}
		System.out.println("\nPriority 1: ");
		for(int i = 0; i < list1.size(); i++) {
			System.out.print(String.valueOf(list1.get(i).getPid()) + " ");
		}
		System.out.println("\nPriority 0: ");
		for(int i = 0; i < list0.size(); i++) {
			System.out.print(String.valueOf(list0.get(i).getPid()) + " ");
		}
		System.out.println();
	}
}
