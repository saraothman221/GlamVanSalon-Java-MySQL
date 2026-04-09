package Dao;

import model.*;
import util.DBConnection;
import java.sql.*;

public class UserDao {

    // ==========================
    // LOGIN (email or username)
    // ==========================
    public User login(String loginInput, String password) {
        String query = "SELECT * FROM User WHERE (email = ? OR username = ?) AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, loginInput);
            stmt.setString(2, loginInput);
            stmt.setString(3, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userID = rs.getInt("userID");
                String name = rs.getString("name");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String role = rs.getString("role");

                switch (role) {
                    case "Manager":
                        return new Manager(userID, name,username, email, password);

                    case "Customer":
                        return getCustomerDetails(userID, name, username, email, password, conn);

                    case "Employee":
                        return new Employee(userID, name, username, email, password);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==========================
    // INSERT USER (Customer)
    // ==========================
    public static int insertUser(String name, String username, String email, String password) throws SQLException {
        String sql = "INSERT INTO User (name, username, email, password, role) VALUES (?, ?, ?, ?, 'Customer')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, password);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // =======================================================
    //  GET CUSTOMER DETAILS (USED FOR LOGIN AND PROFILE)
    // =======================================================
    private static Customer getCustomerDetails(int userID, String name,
                                           String username, String email,
                                           String password, Connection conn)
        throws SQLException {

    String sql = "SELECT phone, address FROM Customer WHERE customerID = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, userID);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String phone   = rs.getString("phone");
            String address = rs.getString("address");
            return new Customer(userID, name, username, email, password, phone, address);
        }
    }
    return null;
}


    // ==========================
    // GET USER BY EMAIL
    // ==========================
    public static User getUserByEmail(String email) throws SQLException {
    String sql = "SELECT * FROM User WHERE email = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int userID = rs.getInt("userID");
            String name = rs.getString("name");
            String username = rs.getString("username");
            String password = rs.getString("password");
            String role = rs.getString("role");

            switch (role) {
                case "Customer":
                    return getCustomerDetails(userID, name, username, email, password, conn);

                case "Manager":
                    return new Manager(userID, name, username, email, password);

                case "Employee":
                    return new Employee(userID, name, username, email, password);
            }
        }
    }
    return null;
}

    public static User getUserByLogin(String loginInput) throws SQLException {
    String sql = "SELECT * FROM User WHERE email = ? OR username = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, loginInput);
        ps.setString(2, loginInput);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int userID = rs.getInt("userID");
            String name = rs.getString("name");
            String username = rs.getString("username");
            String email = rs.getString("email");
            String password = rs.getString("password");
            String role = rs.getString("role");

            switch (role) {
                case "Customer":
                    return getCustomerDetails(userID, name, username, email, password, conn);
                case "Manager":
                    return new Manager(userID, name, username, email, password);
                case "Employee":
                    return new Employee(userID, name, username, email, password);
            }
        }
    }

    return null;
}


    // ==========================
    // UPDATE USER (COMMON FIELDS)
    // ==========================
    public static boolean updateUser(User user) {
        String sql = "UPDATE User SET name = ?, email = ?, password = ? WHERE userID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getUserID());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
   public static boolean emailExists(String email, int excludeUserId) throws SQLException {
    String sql = "SELECT COUNT(*) FROM User WHERE email = ? AND userID <> ?";
    PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
    ps.setString(1, email);
    ps.setInt(2, excludeUserId);

    ResultSet rs = ps.executeQuery();
    rs.next();
    return rs.getInt(1) > 0;
}

    

}
