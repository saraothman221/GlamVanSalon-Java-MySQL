package Dao;

import util.DBConnection;
import java.sql.*;
import model.Customer;

public class CustomerDao {

    // ===============================
    // INSERT CUSTOMER (NO ADDRESS)
    // ===============================
    public static boolean insertCustomer(int userId, String phone) throws SQLException {
        String sql = "INSERT INTO Customer (customerID, phone) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, phone);

            return ps.executeUpdate() > 0;
        }
    }

    // ===============================
    // GET CUSTOMER BY ID (NO ADDRESS)
    // ===============================
    public static Customer getCustomerById(int id) throws SQLException {
    String sql =
        "SELECT u.userID, u.name, u.username, u.email, u.password, " +
        "       c.phone, c.address " +                     
        "FROM User u JOIN Customer c ON u.userID = c.customerID " +
        "WHERE u.userID = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return new Customer(
                rs.getInt("userID"),
                rs.getString("name"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("phone"),
                rs.getString("address")   
            );
        }
        return null;
    }
}


    // UPDATE CUSTOMER (NO ADDRESS)
    public static boolean updateCustomer(Customer customer) throws SQLException {
    String sql = "UPDATE Customer SET phone = ?, address = ? WHERE customerID = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, customer.getPhone());
        pst.setString(2, customer.getAddress());     
        pst.setInt(3, customer.getUserID());

        return pst.executeUpdate() > 0;
    }
}
public static boolean phoneExists(String phone, int excludeUserId) throws SQLException {
    String sql = "SELECT COUNT(*) FROM Customer WHERE phone = ? AND customerID <> ?";
    PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
    ps.setString(1, phone);
    ps.setInt(2, excludeUserId);

    ResultSet rs = ps.executeQuery();
    rs.next();
    return rs.getInt(1) > 0;
}

public static boolean updateCustomerAddress(int customerId, String address) throws SQLException {
    String sql = "UPDATE Customer SET address = ? WHERE customerID = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, address);
        pst.setInt(2, customerId);

        return pst.executeUpdate() > 0;
    }
}


}
