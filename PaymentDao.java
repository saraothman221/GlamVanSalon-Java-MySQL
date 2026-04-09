/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import model.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DBConnection;

public class PaymentDao {

    private static Connection conn;

    public PaymentDao() {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addPayment(Payment payment, String receivedBy, String card_num, String cvv, String exp_date) throws SQLException {
        Connection conn = DBConnection.getConnection();
        boolean isSuccess = false;

        try {
            conn.setAutoCommit(false);  // Begin transaction

            // Insert into Payment table
            String insertPaymentSQL = "INSERT INTO Payment (appointmentID, amount, method) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertPaymentSQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, payment.getAppointmentID());
                System.out.println(payment.getMethod());
                ps.setDouble(2, payment.getAmount());
                ps.setString(3, payment.getMethod());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating payment failed, no rows affected.");
                }

                // Get the generated paymentID (last inserted id)
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int paymentID = generatedKeys.getInt(1);

                    // Insert into Cash table for cash payments
                    if ("Cash".equals(payment.getMethod())) {
                        String insertCashSQL = "INSERT INTO Cash (cashID, receivedBy) VALUES (?, ?)";
                        try (PreparedStatement psCash = conn.prepareStatement(insertCashSQL)) {
                            psCash.setInt(1, paymentID);  // Use paymentID as cashID
                            psCash.setString(2, receivedBy);  // Store who received the payment
                            psCash.executeUpdate();
                        }
                    }
                    if ("CreditCard".equals(payment.getMethod())) {
                        String insertCashSQL = "INSERT INTO CreditCard (creditID, cardNumber,cvv_num, expiryDate) VALUES (?,?,?, ?)";
                        try (PreparedStatement psCash = conn.prepareStatement(insertCashSQL)) {
                            psCash.setInt(1, paymentID);  // Use paymentID as cashID
                            psCash.setString(2, card_num);
                            psCash.setString(3, cvv);
                            psCash.setString(4, exp_date);// Store who received the payment
                            psCash.executeUpdate();
                        }
                    }

                    isSuccess = true;
                }

                conn.commit();  // Commit transaction

            } catch (SQLException e) {
                conn.rollback();  // Rollback transaction if any error occurs
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);  // Restore default auto-commit behavior
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isSuccess;
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM Payment";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Payment p = new Payment();
                p.setPaymentID(rs.getInt("paymentID"));
                p.setAppointmentID(rs.getInt("appointmentID"));
                p.setAmount(rs.getDouble("amount"));
                p.setMethod(rs.getString("method"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updatePayment(Payment payment) {
        String sql = "UPDATE Payment SET amount = ?, method = ? WHERE appointmentID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, payment.getAmount());
            stmt.setString(2, payment.getMethod());
            stmt.setInt(3, payment.getAppointmentID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePayment(int appointmentID) {
        String sql = "DELETE FROM Payment WHERE appointmentID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
