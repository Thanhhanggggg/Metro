package main;

import java.util.ArrayList;
import java.util.List;

import Metro.*;
import controller.PassengerController;
import view.PassengerView;

public class PassengerMain {
    public static void main(String[] args) {
        // ==========================
        // Tạo tuyến Metro
        // ==========================
        MetroLine line1 = new MetroLine("L1","Ben Thanh - Suoi Tien");
        Station s1 = new Station("S01","Ben Thanh",line1,500);
        Station s2 = new Station("S02","Nha Hat TP", line1, 400);
        Station s3 = new Station( "S03","Ba Son",line1,300);
        Station s4 = new Station( "S04", "Van Thanh",line1, 350);
        Station s5 = new Station("S05", "Suoi Tien", line1,600);

        line1.addStation(s1);
        line1.addStation(s2);
        line1.addStation(s3);
        line1.addStation(s4);
        line1.addStation(s5);

        // ==========================
        // Danh sách tuyến
        // ==========================
        List<MetroLine> lines = new ArrayList<>();
        lines.add(line1);
        // ==========================
        // Danh sách ga
        // ==========================
        List<Station> stations = new ArrayList<>();

        stations.add(s1);
        stations.add(s2);
        stations.add(s3);
        stations.add(s4);
        stations.add(s5);

        // ==========================
        // Hành khách
        // ==========================
        Passenger passenger = new Passenger("P001","Nguyen Van A",PassengerType.NORMAL,"123456789",500000);
//        Passenger passenger1 = new Passenger("P002","Nguyen Van B",PassengerType.STUDENT,"123456780",100000);
        // ==========================
        // View
        // ==========================
        PassengerView view = new PassengerView();
//        PassengerView view1 = new PassengerView();
        // ==========================
        // Controller
        // ==========================
        PassengerController controller = new PassengerController(passenger,view);
        view.setController(controller);
        controller.loadData(lines,stations);
//        PassengerController controller1 = new PassengerController(passenger1,view1);
//        view1.setController(controller1);
//        controller1.loadData(lines,stations);
        // ==========================
        // Frame chính
        // ==========================
        javax.swing.JFrame frame = new javax.swing.JFrame("Metro Ticket System");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.add(view);
//        frame.add(view1);
        frame.setVisible(true);

    }
}