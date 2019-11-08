package helpers;

public class Resources {
    private Resource r0;
    private Resource r1;
    private Resource r2;
    private Resource r3;

    public Resources() {
        r0 = new Resource(0, 1);
        r1 = new Resource(1, 1);
        r2 = new Resource(2, 2);
        r3 = new Resource(3, 3);
    }

    public Resource getResource(Integer resource) {
        if(resource == 0)
            return r0;
        if(resource == 1)
            return r1;
        if(resource == 2)
            return r2;
        if(resource == 3)
            return r3;
        return null;
    }

    public Process unblock() {
        Process unblocked = r0.unblockProcesses();
        if(unblocked == null)
            unblocked = r1.unblockProcesses();
        if(unblocked == null)
            unblocked = r2.unblockProcesses();
        if(unblocked == null)
            unblocked = r3.unblockProcesses();
        return unblocked;
    }

    public void print() {
        System.out.println("SYSTEM RESOURCES");
        System.out.println("Rescource 0: " + String.valueOf(r0.getState()));
        r0.printWaitlist();
        System.out.println("Rescource 1: " + String.valueOf(r1.getState()));
        r1.printWaitlist();
        System.out.println("Rescource 2: " + String.valueOf(r2.getState()));
        r2.printWaitlist();
        System.out.println("Rescource 3: " + String.valueOf(r3.getState()));
        r3.printWaitlist();
    }
}