import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.layout.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

class UserModel {
    public int userId; public String username, fullName, email, phone, role;
    public UserModel(int id, String u, String fn, String em, String ph, String r) {
        userId=id; username=u; fullName=fn; email=em; phone=ph; role=r;
    }
}

class RoomModel {
    private final IntegerProperty roomId = new SimpleIntegerProperty();
    private final StringProperty  roomNumber = new SimpleStringProperty();
    private final StringProperty  category   = new SimpleStringProperty();
    private final IntegerProperty floor      = new SimpleIntegerProperty();
    private final IntegerProperty capacity   = new SimpleIntegerProperty();
    private final StringProperty  amenities  = new SimpleStringProperty();
    private final StringProperty  status     = new SimpleStringProperty();
    private final DoubleProperty  price      = new SimpleDoubleProperty();

    public RoomModel(int id,String num,String cat,int fl,int cap,String am,String st,double pr){
        roomId.set(id); roomNumber.set(num); category.set(cat);
        floor.set(fl); capacity.set(cap); amenities.set(am); status.set(st); price.set(pr);
    }
    public int    getRoomId()     { return roomId.get(); }
    public String getRoomNumber() { return roomNumber.get(); }
    public String getCategory()   { return category.get(); }
    public int    getFloor()      { return floor.get(); }
    public int    getCapacity()   { return capacity.get(); }
    public String getAmenities()  { return amenities.get(); }
    public String getStatus()     { return status.get(); }
    public double getPrice()      { return price.get(); }
    public IntegerProperty roomIdProperty()     { return roomId; }
    public StringProperty  roomNumberProperty() { return roomNumber; }
    public StringProperty  categoryProperty()   { return category; }
    public IntegerProperty floorProperty()      { return floor; }
    public IntegerProperty capacityProperty()   { return capacity; }
    public StringProperty  amenitiesProperty()  { return amenities; }
    public StringProperty  statusProperty()     { return status; }
    public DoubleProperty  priceProperty()      { return price; }
}

class ReservationModel {
    private final IntegerProperty resId     = new SimpleIntegerProperty();
    private final StringProperty  guestName = new SimpleStringProperty();
    private final StringProperty  roomNum   = new SimpleStringProperty();
    private final StringProperty  category  = new SimpleStringProperty();
    private final StringProperty  checkIn   = new SimpleStringProperty();
    private final StringProperty  checkOut  = new SimpleStringProperty();
    private final DoubleProperty  total     = new SimpleDoubleProperty();
    private final StringProperty  status    = new SimpleStringProperty();

    public ReservationModel(int id,String g,String rn,String cat,String ci,String co,double t,String st){
        resId.set(id); guestName.set(g); roomNum.set(rn); category.set(cat);
        checkIn.set(ci); checkOut.set(co); total.set(t); status.set(st);
    }
    public int    getResId()     { return resId.get(); }
    public String getGuestName() { return guestName.get(); }
    public String getRoomNum()   { return roomNum.get(); }
    public String getCategory()  { return category.get(); }
    public String getCheckIn()   { return checkIn.get(); }
    public String getCheckOut()  { return checkOut.get(); }
    public double getTotal()     { return total.get(); }
    public String getStatus()    { return status.get(); }
    public IntegerProperty resIdProperty()     { return resId; }
    public StringProperty  guestNameProperty() { return guestName; }
    public StringProperty  roomNumProperty()   { return roomNum; }
    public StringProperty  categoryProperty()  { return category; }
    public StringProperty  checkInProperty()   { return checkIn; }
    public StringProperty  checkOutProperty()  { return checkOut; }
    public DoubleProperty  totalProperty()     { return total; }
    public StringProperty  statusProperty()    { return status; }
}
public class Controller implements Initializable {

 
    private UserModel currentUser;
    @FXML private StackPane rootStack;

    // Login / Signup
    @FXML private VBox    loginPane, signupPane;
    @FXML private TextField  loginUsername, signupUsername, signupFullName,signupEmail, signupPhone;
    @FXML private PasswordField loginPassword, signupPassword, signupConfirm;
    @FXML private Label   loginError, signupError;

    // Main app shell
    @FXML private HBox mainApp;
    @FXML private Label   lblWelcome, lblUserRole;

    // Sidebar nav buttons
    @FXML private Button  navDashboard, navSearch, navMyBookings,navManageRooms, navManageUsers, navReports, navLogout;
    @FXML private VBox    adminMenuSection;

    // Content pages
    @FXML private StackPane mainContent;
    @FXML private VBox    pageDashboard, pageSearch, pageMyBookings,pageManageRooms, pageManageUsers, pageReports;

    // Dashboard cards
    @FXML private Label cardTotalRooms, cardAvailRooms, cardTotalRes, cardRevenue;

    // Search Page 
    @FXML private ComboBox<String>  searchCategory;
    @FXML private DatePicker        searchCheckIn, searchCheckOut;
    @FXML private TableView<RoomModel>            searchTable;
    @FXML private TableColumn<RoomModel,String>   colSRoomNo, colSCategory, colSAmenities, colSStatus;
    @FXML private TableColumn<RoomModel,Integer>  colSFloor, colSCapacity;
    @FXML private TableColumn<RoomModel,Double>   colSPrice;
    @FXML private Label   bookingStatus;

    // Booking dialog
    @FXML private VBox    bookingPanel;
    @FXML private Label   bookRoomInfo;
    @FXML private DatePicker bookCheckIn, bookCheckOut;
    @FXML private ComboBox<String> bookPayMethod;
    @FXML private Label   bookTotalPrice;

    // My Bookings
    @FXML private TableView<ReservationModel>            myBookingsTable;
    @FXML private TableColumn<ReservationModel,Integer>  colMResId;
    @FXML private TableColumn<ReservationModel,String>   colMRoom, colMCat,colMCheckIn, colMCheckOut, colMStatus;
    @FXML private TableColumn<ReservationModel,Double>   colMTotal;

    // Manage Rooms (Admin) 
    @FXML private TableView<RoomModel>           roomsAdminTable;
    @FXML private TableColumn<RoomModel,String>  colARoomNo, colACategory, colAAmenities, colAStatus;
    @FXML private TableColumn<RoomModel,Integer> colAFloor, colACapacity;
    @FXML private TableColumn<RoomModel,Double>  colAPrice;
    @FXML private TextField  addRoomNumber, addFloor, addCapacity, addAmenities, addPrice;
    @FXML private ComboBox<String> addRoomCategory, addRoomStatus;

    // Manage Users (Admin)
    @FXML private TableView<ReservationModel>           usersReservTable;
    @FXML private TableColumn<ReservationModel,String>  colUGuest, colURoom,colUCheckIn, colUCheckOut, colUStatus;
    @FXML private TableColumn<ReservationModel,Double>  colUTotal;

    // Reports (Admin)
    @FXML private Label reportTotalRes, reportRevenue, reportCancelled, reportOccupancy;

    // Runtime data
    private RoomModel selectedRoom;
    private final ObservableList<RoomModel>        roomList = FXCollections.observableArrayList();
    private final ObservableList<ReservationModel> resList  = FXCollections.observableArrayList();
    private final ObservableList<RoomModel>        allRooms = FXCollections.observableArrayList();
    private final ObservableList<ReservationModel> allRes   = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showLoginPane();
        initTableColumns();
        initComboBoxes();
    }

    // TABLE COLUMN SETUP
   private void initTableColumns() {
    // Search Table
    colSRoomNo.setCellValueFactory(c -> c.getValue().roomNumberProperty());
    colSCategory.setCellValueFactory(c -> c.getValue().categoryProperty());
    colSFloor.setCellValueFactory(c -> c.getValue().floorProperty().asObject());
    colSCapacity.setCellValueFactory(c -> c.getValue().capacityProperty().asObject());
    colSAmenities.setCellValueFactory(c -> c.getValue().amenitiesProperty());
    colSPrice.setCellValueFactory(c -> c.getValue().priceProperty().asObject());
    colSStatus.setCellValueFactory(c -> c.getValue().statusProperty());
    searchTable.setItems(roomList);

    // My Bookings
    colMResId.setCellValueFactory(c -> c.getValue().resIdProperty().asObject());
    colMRoom.setCellValueFactory(c -> c.getValue().roomNumProperty());
    colMCat.setCellValueFactory(c -> c.getValue().categoryProperty());
    colMCheckIn.setCellValueFactory(c -> c.getValue().checkInProperty());
    colMCheckOut.setCellValueFactory(c -> c.getValue().checkOutProperty());
    colMTotal.setCellValueFactory(c -> c.getValue().totalProperty().asObject());
    colMStatus.setCellValueFactory(c -> c.getValue().statusProperty());
    myBookingsTable.setItems(resList);

    // Admin Manage Rooms
    colARoomNo.setCellValueFactory(c -> c.getValue().roomNumberProperty());
    colACategory.setCellValueFactory(c -> c.getValue().categoryProperty());
    colAFloor.setCellValueFactory(c -> c.getValue().floorProperty().asObject());
    colACapacity.setCellValueFactory(c -> c.getValue().capacityProperty().asObject());
    colAAmenities.setCellValueFactory(c -> c.getValue().amenitiesProperty());
    colAPrice.setCellValueFactory(c -> c.getValue().priceProperty().asObject());
    colAStatus.setCellValueFactory(c -> c.getValue().statusProperty());
    roomsAdminTable.setItems(allRooms);

    // Admin All Reservations
    colUGuest.setCellValueFactory(c -> c.getValue().guestNameProperty());
    colURoom.setCellValueFactory(c -> c.getValue().roomNumProperty());
    colUCheckIn.setCellValueFactory(c -> c.getValue().checkInProperty());
    colUCheckOut.setCellValueFactory(c -> c.getValue().checkOutProperty());
    colUTotal.setCellValueFactory(c -> c.getValue().totalProperty().asObject());
    colUStatus.setCellValueFactory(c -> c.getValue().statusProperty());
    usersReservTable.setItems(allRes);
}

    private void initComboBoxes() {
        searchCategory.getItems().addAll("All", "Standard", "Deluxe", "Suite");
        searchCategory.setValue("All");
        bookPayMethod.getItems().addAll("Cash", "Credit Card", "Debit Card", "Online");
        bookPayMethod.setValue("Cash");
        addRoomCategory.getItems().addAll("Standard", "Deluxe", "Suite");
        addRoomCategory.setValue("Standard");
        addRoomStatus.getItems().addAll("available", "maintenance");
        addRoomStatus.setValue("available");
    }

    
    // NAVIGATION
    
    private void showLoginPane()  { loginPane.setVisible(true);  signupPane.setVisible(false); mainApp.setVisible(false); }
    private void showSignupPane() { loginPane.setVisible(false); signupPane.setVisible(true);  mainApp.setVisible(false); }
    private void showMainApp()    { loginPane.setVisible(false); signupPane.setVisible(false); mainApp.setVisible(true); }

    @FXML private void onGoToSignup() { signupError.setText(""); showSignupPane(); }
    @FXML private void onGoToLogin()  { loginError.setText(""); showLoginPane(); }

    private void setActivePage(VBox page) {
        for (VBox p : new VBox[]{pageDashboard, pageSearch, pageMyBookings,
                                  pageManageRooms, pageManageUsers, pageReports}) {
            p.setVisible(false); p.setManaged(false);
        }
        page.setVisible(true); page.setManaged(true);
    }

    @FXML private void onNavDashboard() { setActivePage(pageDashboard); loadDashboard(); }

    @FXML private void onNavSearch() {
        setActivePage(pageSearch);
        roomList.clear();
        searchTable.setItems(roomList);
        bookingPanel.setVisible(false);
        bookingPanel.setManaged(false);
    }

    @FXML private void onNavMyBookings() {
        setActivePage(pageMyBookings);
        loadMyBookings();
    }

    @FXML private void onNavManageRooms() {
        setActivePage(pageManageRooms);
        loadAllRooms();
    }

    @FXML private void onNavManageUsers() {
        setActivePage(pageManageUsers);
        loadAllReservations();
    }

    @FXML private void onNavReports() { setActivePage(pageReports); loadReports(); }

    @FXML private void onLogout() {
        currentUser = null;
        loginUsername.clear(); loginPassword.clear(); loginError.setText("");
        showLoginPane();
    }
    @FXML
    private void onLogin() {
        String user = loginUsername.getText().trim();
        String pass = loginPassword.getText();
        if (user.isEmpty() || pass.isEmpty()) {
            loginError.setText("Please fill in all fields.");
            return;
        }
        try (Connection con = DatabaseManager.getConnection()) {
            String sql = "SELECT user_id,username,full_name,email,phone,role "
                       + "FROM Users WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, DatabaseManager.sha256(pass));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currentUser = new UserModel(
                    rs.getInt("user_id"), rs.getString("username"),
                    rs.getString("full_name"), rs.getString("email"),
                    rs.getString("phone"), rs.getString("role"));
                setupMainApp();
            } else {
                loginError.setText("Invalid username or password.");
            }
        } catch (SQLException e) {
             loginError.setText("DB Error: " + e.getMessage());
        }
    }

    @FXML
    private void onSignup() {
        String uname = signupUsername.getText().trim();
        String fname = signupFullName.getText().trim();
        String email = signupEmail.getText().trim();
        String phone = signupPhone.getText().trim();
        String pass  = signupPassword.getText();
        String conf  = signupConfirm.getText();

        if (uname.isEmpty()||fname.isEmpty()||email.isEmpty()||pass.isEmpty()) {
            signupError.setText("All fields are required."); return;
        }
        if (!pass.equals(conf)) {
            signupError.setText("Passwords do not match."); return;
        }
        try (Connection con = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO Users(username,password,full_name,email,phone,role)"
                       + " VALUES(?,?,?,?,?,'user')";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,uname); ps.setString(2,DatabaseManager.sha256(pass));
            ps.setString(3,fname); ps.setString(4,email); ps.setString(5,phone);
            ps.executeUpdate();
            signupError.setStyle("-fx-text-fill: #4ade80;");
            signupError.setText("Account created! Please log in.");
        } catch (SQLException e) {
            signupError.setStyle("-fx-text-fill: #f87171;");
            signupError.setText("Error: " + e.getMessage());
        }
    }
    private void setupMainApp() {
        lblWelcome.setText("Welcome, " + currentUser.fullName);
        lblUserRole.setText(currentUser.role.toUpperCase());
        boolean isAdmin = "admin".equals(currentUser.role);
        adminMenuSection.setVisible(isAdmin);
        adminMenuSection.setManaged(isAdmin);
        showMainApp();
        setActivePage(pageDashboard);
        loadDashboard();
    }
    private void loadDashboard() {
        try (Connection con = DatabaseManager.getConnection()) {
            boolean isAdmin = "admin".equals(currentUser.role);

            ResultSet rs = con.createStatement().executeQuery("SELECT COUNT(*) FROM Rooms");
            if (rs.next()) cardTotalRooms.setText(String.valueOf(rs.getInt(1)));

            rs = con.createStatement().executeQuery(
                "SELECT COUNT(*) FROM Rooms WHERE status='available'");
            if (rs.next()) cardAvailRooms.setText(String.valueOf(rs.getInt(1)));

            if (isAdmin) {
                rs = con.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM Reservations WHERE status='confirmed'");
                if (rs.next()) cardTotalRes.setText(String.valueOf(rs.getInt(1)));

                rs = con.createStatement().executeQuery(
                    "SELECT ISNULL(SUM(total_price),0) FROM Reservations " +
                    "WHERE status IN ('confirmed','completed')");
                if (rs.next())
                    cardRevenue.setText("PKR " + String.format("%,.0f", rs.getDouble(1)));
            } else {
                PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM Reservations WHERE status='confirmed' AND user_id=?");
                ps.setInt(1, currentUser.userId);
                rs = ps.executeQuery();
                if (rs.next()) cardTotalRes.setText(String.valueOf(rs.getInt(1)));

                cardRevenue.setText("N/A");
                cardRevenue.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #4a5568;");
            }
        } catch (SQLException e) {
            showAlert("DB Error", e.getMessage());
        }
    }
    //  SEARCH ROOMS
    @FXML
    private void onSearchRooms() {
        LocalDate ci = searchCheckIn.getValue();
        LocalDate co = searchCheckOut.getValue();
        if (ci == null || co == null) {
            bookingStatus.setText("Please select check-in and check-out dates.");
            return;
        }
        if (!co.isAfter(ci)) {
            bookingStatus.setText("Check-out must be after check-in."); return;
        }
        String cat = searchCategory.getValue();

        ObservableList<RoomModel> freshList = FXCollections.observableArrayList();
        try (Connection con = DatabaseManager.getConnection()) {
            String sql =
                "SELECT r.room_id,r.room_number,rc.category_name,r.floor,r.capacity," +
                "r.amenities,r.status,r.price_per_night " +
                "FROM Rooms r JOIN RoomCategories rc ON r.category_id=rc.category_id " +
                "WHERE r.status='available' " +
                (!"All".equals(cat) ? "AND rc.category_name=? " : "") +
                "AND r.room_id NOT IN (" +
                "  SELECT res.room_id FROM Reservations res " +
                "  WHERE res.status='confirmed' AND NOT(res.check_out<=? OR res.check_in>=?)" +
                ")";
            PreparedStatement ps = con.prepareStatement(sql);
            int idx = 1;
            if (!"All".equals(cat)) ps.setString(idx++, cat);
            ps.setDate(idx++, Date.valueOf(ci));
            ps.setDate(idx,   Date.valueOf(co));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                freshList.add(new RoomModel(
                    rs.getInt("room_id"), rs.getString("room_number"),
                    rs.getString("category_name"), rs.getInt("floor"),
                    rs.getInt("capacity"), rs.getString("amenities"),
                    rs.getString("status"), rs.getDouble("price_per_night")));
            }
            bookingStatus.setText(freshList.size() + " room(s) found.");
        } catch (SQLException e) {
            showAlert("DB Error", e.getMessage());
        }
        searchTable.setItems(freshList);
        searchTable.refresh();
    }

    @FXML
    private void onSelectRoom() {
        selectedRoom = searchTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) { bookingStatus.setText("Select a room first."); return; }
        bookRoomInfo.setText("Room " + selectedRoom.getRoomNumber() +
            " – " + selectedRoom.getCategory() + " | PKR " +
            String.format("%,.0f", selectedRoom.getPrice()) + "/night");
        bookCheckIn.setValue(searchCheckIn.getValue());
        bookCheckOut.setValue(searchCheckOut.getValue());
        updateBookTotal();
        bookingPanel.setVisible(true);
        bookingPanel.setManaged(true);
    }

    @FXML private void onUpdateBookTotal() { updateBookTotal(); }

    private void updateBookTotal() {
        LocalDate ci = bookCheckIn.getValue();
        LocalDate co = bookCheckOut.getValue();
        if (ci!=null && co!=null && co.isAfter(ci) && selectedRoom!=null) {
            long nights = ChronoUnit.DAYS.between(ci, co);
            double total = nights * selectedRoom.getPrice();
            bookTotalPrice.setText("Total: PKR " + String.format("%,.0f", total)
                + "  (" + nights + " night" + (nights>1?"s":"") + ")");
        }
    }

    @FXML
    private void onConfirmBooking() {
        if (selectedRoom == null) return;
        LocalDate ci = bookCheckIn.getValue();
        LocalDate co = bookCheckOut.getValue();
        if (ci==null||co==null||!co.isAfter(ci)) {
            bookingStatus.setText("Invalid dates."); return;
        }
        long nights = ChronoUnit.DAYS.between(ci,co);
        double total = nights * selectedRoom.getPrice();
        String method = bookPayMethod.getValue();
        try (Connection con = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO Reservations(user_id,room_id,check_in,check_out,total_price,status)"
                       + " OUTPUT INSERTED.reservation_id VALUES(?,?,?,?,?,'confirmed')";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, currentUser.userId);
            ps.setInt(2, selectedRoom.getRoomId());
            ps.setDate(3, Date.valueOf(ci));
            ps.setDate(4, Date.valueOf(co));
            ps.setDouble(5, total);
            ResultSet rs = ps.executeQuery();
            int resId = 0;
            if (rs.next()) resId = rs.getInt(1);

            PreparedStatement pp = con.prepareStatement(
                "INSERT INTO Payments(reservation_id,amount,payment_method,payment_status) VALUES(?,?,?,'paid')");
            pp.setInt(1, resId); pp.setDouble(2, total); pp.setString(3, method);
            pp.executeUpdate();

            bookingPanel.setVisible(false);
            bookingPanel.setManaged(false);
            searchTable.setItems(FXCollections.observableArrayList());
            bookingStatus.setStyle("-fx-text-fill: #4ade80;");
            bookingStatus.setText("✓ Booking confirmed! Reservation ID: " + resId);
        } catch (SQLException e) {
            showAlert("Booking Error", e.getMessage());
        }
    }

    @FXML private void onCancelBookingPanel() {
        bookingPanel.setVisible(false); bookingPanel.setManaged(false);
    }

    //  MY BOOKINGS
    private void loadMyBookings() {
        ObservableList<ReservationModel> freshList = FXCollections.observableArrayList();
        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT r.reservation_id, u.full_name, rm.room_number, rc.category_name, " +
                "r.check_in, r.check_out, r.total_price, r.status " +
                "FROM Reservations r " +
                "JOIN Users u ON r.user_id=u.user_id " +
                "JOIN Rooms rm ON r.room_id=rm.room_id " +
                "JOIN RoomCategories rc ON rm.category_id=rc.category_id " +
                "WHERE r.user_id=?");
            ps.setInt(1, currentUser.userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                freshList.add(new ReservationModel(
                    rs.getInt("reservation_id"),
                    rs.getString("full_name"),
                    rs.getString("room_number"),
                    rs.getString("category_name"),
                    rs.getDate("check_in")  != null ? rs.getDate("check_in").toString()  : "",
                    rs.getDate("check_out") != null ? rs.getDate("check_out").toString() : "",
                    rs.getDouble("total_price"),
                    rs.getString("status")));
            }
        } catch (SQLException e) {
            showAlert("DB Error", e.getMessage());
        }
        myBookingsTable.setItems(freshList);
        myBookingsTable.refresh();
    }

    @FXML
    private void onCancelReservation() {
        ReservationModel sel = myBookingsTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Info","Select a reservation to cancel."); return; }
        if (!"confirmed".equals(sel.getStatus())) {
            showAlert("Info","Only confirmed reservations can be cancelled."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Cancel reservation #" + sel.getResId() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try (Connection con = DatabaseManager.getConnection()) {
                    PreparedStatement ps = con.prepareStatement(
                        "UPDATE Reservations SET status='cancelled' WHERE reservation_id=?");
                    ps.setInt(1, sel.getResId()); ps.executeUpdate();
                    PreparedStatement pp = con.prepareStatement(
                        "UPDATE Payments SET payment_status='refunded' WHERE reservation_id=?");
                    pp.setInt(1, sel.getResId()); pp.executeUpdate();
                    loadMyBookings();
                } catch (SQLException e) { showAlert("DB Error", e.getMessage()); }
            }
        });
    }

    
    //  ADMIN – MANAGE ROOMS
    
    private void loadAllRooms() {
        ObservableList<RoomModel> freshList = FXCollections.observableArrayList();
        try (Connection con = DatabaseManager.getConnection()) {
            String sql =
                "SELECT r.room_id,r.room_number,rc.category_name,r.floor,r.capacity," +
                "r.amenities,r.status,r.price_per_night " +
                "FROM Rooms r JOIN RoomCategories rc ON r.category_id=rc.category_id " +
                "ORDER BY r.room_number";
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                freshList.add(new RoomModel(
                    rs.getInt("room_id"), rs.getString("room_number"),
                    rs.getString("category_name"), rs.getInt("floor"),
                    rs.getInt("capacity"), rs.getString("amenities"),
                    rs.getString("status"), rs.getDouble("price_per_night")));
            }
        } catch (SQLException e) { showAlert("DB Error", e.getMessage()); }
        roomsAdminTable.setItems(freshList);
        roomsAdminTable.refresh();
    }

    @FXML
    private void onAddRoom() {
        String num   = addRoomNumber.getText().trim();
        String flrS  = addFloor.getText().trim();
        String capS  = addCapacity.getText().trim();
        String amen  = addAmenities.getText().trim();
        String prS   = addPrice.getText().trim();
        String cat   = addRoomCategory.getValue();
        String stat  = addRoomStatus.getValue();

        if (num.isEmpty()||flrS.isEmpty()||capS.isEmpty()||prS.isEmpty()) {
            showAlert("Validation","Fill all required fields."); return; }
        try {
            int flr = Integer.parseInt(flrS);
            int cap = Integer.parseInt(capS);
            double pr = Double.parseDouble(prS);
            try (Connection con = DatabaseManager.getConnection()) {
                PreparedStatement pc = con.prepareStatement(
                    "SELECT category_id FROM RoomCategories WHERE category_name=?");
                pc.setString(1,cat);
                ResultSet rc = pc.executeQuery();
                if (!rc.next()) { showAlert("Error","Category not found."); return; }
                int catId = rc.getInt(1);
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Rooms(room_number,category_id,floor,capacity,amenities,status,price_per_night)"
                    +" VALUES(?,?,?,?,?,?,?)");
                ps.setString(1,num); ps.setInt(2,catId); ps.setInt(3,flr);
                ps.setInt(4,cap); ps.setString(5,amen); ps.setString(6,stat); ps.setDouble(7,pr);
                ps.executeUpdate();
                addRoomNumber.clear(); addFloor.clear(); addCapacity.clear();
                addAmenities.clear(); addPrice.clear();
                loadAllRooms();
                showAlert("Success","Room added successfully.");
            }
        } catch (NumberFormatException ex) {
            showAlert("Validation","Floor, Capacity, and Price must be numbers.");
        } catch (SQLException e) { showAlert("DB Error", e.getMessage()); }
    }

    @FXML
    private void onDeleteRoom() {
        RoomModel sel = roomsAdminTable.getSelectionModel().getSelectedItem();
        if (sel==null) { showAlert("Info","Select a room to delete."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete room " + sel.getRoomNumber() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt==ButtonType.YES) {
                try (Connection con = DatabaseManager.getConnection()) {
                    PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM Rooms WHERE room_id=?");
                    ps.setInt(1, sel.getRoomId()); ps.executeUpdate();
                    loadAllRooms();
                } catch (SQLException e) { showAlert("DB Error", e.getMessage()); }
            }
        });
    }

    @FXML
    private void onToggleRoomStatus() {
        RoomModel sel = roomsAdminTable.getSelectionModel().getSelectedItem();
        if (sel==null) { showAlert("Info","Select a room."); return; }
        String newStatus = "available".equals(sel.getStatus()) ? "maintenance" : "available";
        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "UPDATE Rooms SET status=? WHERE room_id=?");
            ps.setString(1,newStatus); ps.setInt(2,sel.getRoomId());
            ps.executeUpdate(); loadAllRooms();
        } catch (SQLException e) { showAlert("DB Error", e.getMessage()); }
    }
    
    private void loadAllReservations() {
        ObservableList<ReservationModel> freshList = FXCollections.observableArrayList();
        try (Connection con = DatabaseManager.getConnection()) {
            String sql =
                "SELECT r.reservation_id,u.full_name,rm.room_number,rc.category_name," +
                "r.check_in,r.check_out,r.total_price,r.status " +
                "FROM Reservations r " +
                "JOIN Users u ON r.user_id=u.user_id " +
                "JOIN Rooms rm ON r.room_id=rm.room_id " +
                "JOIN RoomCategories rc ON rm.category_id=rc.category_id " +
                "ORDER BY r.reservation_id DESC";
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                freshList.add(new ReservationModel(
                    rs.getInt("reservation_id"), rs.getString("full_name"),
                    rs.getString("room_number"), rs.getString("category_name"),
                    rs.getDate("check_in")  != null ? rs.getDate("check_in").toString()  : "",
                    rs.getDate("check_out") != null ? rs.getDate("check_out").toString() : "",
                    rs.getDouble("total_price"), rs.getString("status")));
        }
        } catch (SQLException e) { showAlert("DB Error", e.getMessage()); }
        usersReservTable.setItems(freshList);
        usersReservTable.refresh();
    }

    @FXML
    private void onAdminCancelReservation() {
        ReservationModel sel = usersReservTable.getSelectionModel().getSelectedItem();
        if (sel==null) { showAlert("Info","Select a reservation."); return; }
        if (!"confirmed".equals(sel.getStatus())) {
            showAlert("Info","Only confirmed reservations can be cancelled."); return; }
        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "UPDATE Reservations SET status='cancelled' WHERE reservation_id=?");
            ps.setInt(1,sel.getResId()); ps.executeUpdate();
            PreparedStatement pp = con.prepareStatement(
                "UPDATE Payments SET payment_status='refunded' WHERE reservation_id=?");
            pp.setInt(1,sel.getResId()); pp.executeUpdate();
            loadAllReservations();
        } catch (SQLException e) { showAlert("DB Error", e.getMessage()); }
    }

    private void loadReports() {
        try (Connection con = DatabaseManager.getConnection()) {
            ResultSet rs;
            rs = con.createStatement().executeQuery(
                "SELECT COUNT(*) FROM Reservations WHERE status IN ('confirmed','completed')");
            if (rs.next()) reportTotalRes.setText(String.valueOf(rs.getInt(1)));

            rs = con.createStatement().executeQuery(
                "SELECT ISNULL(SUM(total_price),0) FROM Reservations WHERE status IN ('confirmed','completed')");
            if (rs.next()) reportRevenue.setText("PKR " + String.format("%,.0f", rs.getDouble(1)));

            rs = con.createStatement().executeQuery(
                "SELECT COUNT(*) FROM Reservations WHERE status='cancelled'");
            if (rs.next()) reportCancelled.setText(String.valueOf(rs.getInt(1)));

            rs = con.createStatement().executeQuery(
                "SELECT COUNT(*) FROM Rooms WHERE status='occupied' OR room_id IN " +
                "(SELECT room_id FROM Reservations WHERE status='confirmed' AND check_in<=GETDATE() AND check_out>=GETDATE())");
            ResultSet rtotal = con.createStatement().executeQuery("SELECT COUNT(*) FROM Rooms");
            if (rs.next() && rtotal.next()) {
                double pct = rtotal.getInt(1)==0 ? 0 : (rs.getDouble(1)/rtotal.getInt(1))*100;
                reportOccupancy.setText(String.format("%.1f%%", pct));
            }
        } catch (SQLException e) { showAlert("DB Error", e.getMessage()); }
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }
}