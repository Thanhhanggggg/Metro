package Metro;

public class Main {
	public static void main(String[] args) {
		// Tạo vé
        Ticket ticket1 = new Ticket("T001");
        // Tạo cổng vào
        SmartGate gateIn = new SmartGate("G-IN-01", GateType.IN);
        // Tạo cổng ra
        SmartGate gateOut = new SmartGate("G-OUT-01", GateType.OUT);
        System.out.println("===== CHECK IN =====");
        gateIn.scanQRCode(ticket1);
        System.out.println("\nCurrent State:" + ticket1.getState().getClass().getSimpleName());
        System.out.println("\n===== CHECK OUT =====");
        gateOut.scanQRCode(ticket1);
        System.out.println("\nCurrent State:" + ticket1.getState().getClass().getSimpleName());
        System.out.println("\n===== REFUND TEST =====");
        
        //
        ticket1.refund();
        System.out.println("\nCurrent State:" + ticket1.getState().getClass().getSimpleName());
       
        //checkout trước check in
        System.out.println("===========================================================");
        System.out.println("Test the check-in method before check-out");
        Ticket ticket2 = new Ticket("T02");
        ticket2.checkOut();
        	
        // test hoàn vé
        System.out.println("===========================================================");
        System.out.println("test refund methods");
        Ticket ticket3 = new Ticket("T03");
        ticket3.refund();
        System.out.println(ticket3.getState().getClass().getSimpleName());
        ticket3.checkIn();
        
        //test SmartGate lỗi
        System.out.println("===========================================================");
        System.out.println("test SmartGate error");
        SmartGate gate = new SmartGate("G01", GateType.IN);
        gate.disableGate();
        Ticket ticket4 = new Ticket("T04");
        gate.scanQRCode(ticket4);
        
        //test FaultLog
        System.out.println("===========================================================");
        System.out.println("test FaultLog");
        SmartGate gate1 =new SmartGate("G01",GateType.IN);
        gate1.reportFault("Scanner hardware failure");
	}
}
