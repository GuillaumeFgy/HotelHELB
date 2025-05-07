package com.example;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class HotelController {

    private Hotel hotel;
    private HotelView view;
    private final String configurationFile = "src/main/java/com/example/configuration.hconfig";
    private final String reservationFile = "src/main/java/com/example/reservation.csv";
    private List<Reservation> reservations;
    private List<AssignmentRequest> assignments = new ArrayList<AssignmentRequest>();
    private final Map<String, AssignmentStrategy> strategies = new LinkedHashMap<>();

    public HotelController(HotelView view)
    {
        this.view = view;
        ConfigurationParser configurationParser = new ConfigurationParser(configurationFile);
        hotel = new Hotel(configurationParser.getNumberOfFloors(), configurationParser.getFloorLayout(), this.view);
        initStrategies();
        view.initView(hotel, strategies);
        setActions();
        startSimulation();
    }

    private void displayUI()
    {
        view.showReservations(assignments);
    }

    private void setActions()
    {
        floorSelectionAction();
        refreshButtonAction();
        sortSelectorAction();
    }

    private void floorSelectionAction()
    {
        view.getFloorSelector().setOnAction(event -> {
            int selectedIndex = view.getFloorSelector().getSelectionModel().getSelectedIndex();
            view.updateFloorView(hotel, selectedIndex);
        });
    }

    private void refreshButtonAction() {
        VBox reservationList = view.getReservationList();
    
        for (javafx.scene.Node node : reservationList.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
    
                Object userData = hbox.getUserData();
                if (!(userData instanceof String)) continue;
    
                String roomName = (String) userData;
    
                for (AssignmentRequest request : assignments) {
                    if (request.room.getName().equals(roomName)) {
                        final AssignmentRequest matchingRequest = request;
    
                        javafx.scene.Node last = hbox.getChildren().get(hbox.getChildren().size() - 1);
                        if (last instanceof Button) {
                            Button refreshButton = (Button) last;
                            refreshButton.setOnAction(e -> reassignReservation(matchingRequest));
                        }
                        break;
                    }
                }
            }
        }
    }

    private void sortSelectorAction() {
        view.getSortSelector().setOnAction(event -> {
            String selected = view.getSortSelector().getValue();
    
            switch (selected) {
                case "Sort by : Name":
                    assignments.sort((a1, a2) -> a1.reservation.getLastName().compareToIgnoreCase(a2.reservation.getLastName()));
                    break;
                    case "Sort by : Room":
                        assignments.sort((a1, a2) -> {
                            String r1 = a1.room.getName();
                            String r2 = a2.room.getName();
                    
                            // Exemple : A12L → étage = A, numéro = 12
                            char floor1 = r1.charAt(0);
                            char floor2 = r2.charAt(0);
                    
                            int floorIndex1 = floor1 - 'A';
                            int floorIndex2 = floor2 - 'A';
                    
                            // Extraire le numéro (en ignorant la première lettre et le dernier caractère)
                            int num1 = extractRoomNumber(r1);
                            int num2 = extractRoomNumber(r2);
                    
                            if (floorIndex1 != floorIndex2) {
                                return Integer.compare(floorIndex1, floorIndex2);
                            } else {
                                return Integer.compare(num1, num2);
                            }
                        });
                        break;
                
                default:
                    break;
            }
    
            view.showReservations(assignments);
            refreshButtonAction(); // toujours réappliquer les handlers
        });
    }

    private int extractRoomNumber(String roomName) {
        // Ex : A12L → extrait "12"
        String digits = roomName.substring(1, roomName.length() - 1);
        return Integer.parseInt(digits);
    }
    
    

    private void initStrategies() {
        strategies.put("Random Assignment", new RandomAssignment());
        strategies.put("Quiet Zone", new QuietZoneAssignment(hotel));
        strategies.put("Stay Purpose", new StayPurposeAssignment());
        strategies.put("Sequential Assignment", new SequentialAssignment(hotel));
    }

    private AssignmentStrategy getSelectedStrategy() {
        String selected = view.getStrategySelector().getValue();
        return strategies.get(selected);
    }

    public AssignmentRequest assignReservation(Reservation reservation) {
        AssignmentRequest request = getSelectedStrategy().createAssignmentRequest(hotel.getAvailableRooms(), reservation);
        hotel.reserveRoom(request);
        assignments.add(request);
        return request;
    }

    public void reassignReservation(AssignmentRequest request) 
    {
        int index = -1;
        for (int i = 0; i < assignments.size(); i++) {
            if (assignments.get(i).room.getName().equals(request.room.getName())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            hotel.freeRoom(request.room.getName());
            assignments.remove(index);
            if (!hotel.getAvailableRooms().isEmpty()) {
                AssignmentRequest newRequest = assignReservation(request.reservation);
                assignments.remove(newRequest);
                assignments.add(index, newRequest);
            }
        }
    }


    public void updateReservationList()
    {
        for (Reservation res : reservations) {
            System.err.println(res.getFirstName());
            if (hotel.getAvailableRooms().size() > 0)
            {
                assignReservation(res);
            }
        }
    }

    private void startSimulation()
    {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event)
            {
                pollForNewReservations();
                displayUI();
                refreshButtonAction();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();   
    }

    private void pollForNewReservations() {
        List<Reservation> latest = ReservationParser.parseFile(reservationFile);
    
        for (Reservation res : latest) {
            if (!hotel.getAvailableRooms().isEmpty()) {
                assignReservation(res);
            }
        }
    }

    
    
    
}
