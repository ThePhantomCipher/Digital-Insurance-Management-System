package com.mycompany.insurancemanagementsystem;

import java.sql.*;

public class DBUtil {

private static final String URL =
"jdbc:mysql://localhost:3306/insurance?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

private static final String USER = "root";
private static final String PASS = "kamogelo1";

public static Connection connect() throws SQLException {

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
        throw new SQLException("MySQL Driver not found", e);
    }

    return DriverManager.getConnection(URL, USER, PASS);
}

public static double calculatePremium(String type, double coverage) {

    switch (type) {
        case "Life": return coverage * 0.05;
        case "Health": return coverage * 0.08;
        case "Auto": return coverage * 0.10;
        default: return 0;
    }
}

public static void deletePolicy(String idNum)
    throws SQLException {

    Connection con = connect();

    try {

    con.setAutoCommit(false);

    String findCustomer =
    "SELECT customer_id FROM customers WHERE id_number=?";

    PreparedStatement ps =
    con.prepareStatement(findCustomer);

    ps.setString(1, idNum);

    ResultSet rs = ps.executeQuery();

    if(rs.next()){

    int custId = rs.getInt(1);

    PreparedStatement ps2 =
    con.prepareStatement(
    "DELETE FROM policies WHERE customer_id=?"
    );

    ps2.setInt(1, custId);
    ps2.executeUpdate();

    PreparedStatement ps3 =
    con.prepareStatement(
    "DELETE FROM customers WHERE customer_id=?"
    );

    ps3.setInt(1, custId);
    ps3.executeUpdate();

    }

    con.commit();

    } catch(Exception e){

    con.rollback();
    throw e;

    } finally {

    con.close();

    }
}

public static void updatePolicy(

    String idNum,
    String name,
    String surname,
    String address,
    int age,
    String type,
    double coverage

    ) throws SQLException {

    Connection con = connect();

    try {

    con.setAutoCommit(false);

    String updateCustomer =
    "UPDATE customers SET name=?,surname=?,address=?,age=? WHERE id_number=?";

    PreparedStatement ps =
    con.prepareStatement(updateCustomer);

    ps.setString(1,name);
    ps.setString(2,surname);
    ps.setString(3,address);
    ps.setInt(4,age);
    ps.setString(5,idNum);

    ps.executeUpdate();

    double premium =
    calculatePremium(type,coverage);

    String updatePolicy =
    "UPDATE policies p\n" + "JOIN customers c\n" + "ON p.customer_id=c.customer_id\n" + "SET p.policy_type=?,\n" + "p.coverage_amount=?,\n" + "p.premium_amount=?\n" + "WHERE c.id_number=?\n";

    PreparedStatement ps2 =
    con.prepareStatement(updatePolicy);

    ps2.setString(1,type);
    ps2.setDouble(2,coverage);
    ps2.setDouble(3,premium);
    ps2.setString(4,idNum);

    ps2.executeUpdate();

    con.commit();

    } catch(Exception e){

    con.rollback();
    throw e;

    } finally {

    con.close();

    }
}

public static boolean customerExists(String idNum) {

    try (Connection con = connect()) {

        String sql =
        "SELECT id_number FROM customers WHERE id_number = ?";

        PreparedStatement ps =
        con.prepareStatement(sql);

        ps.setString(1, idNum);

        ResultSet rs = ps.executeQuery();

        return rs.next();

    } catch (Exception e) {

        e.printStackTrace();
        return false;
    }
}

public static void insertCustomerAndPolicy(
String idNum,
String name,
String surname,
String address,
int age,
String type,
double coverage) throws SQLException {

Connection con = connect();

try {

con.setAutoCommit(false);

String insertCustomer =
"INSERT INTO customers(id_number,name,surname,address,age) VALUES(?,?,?,?,?)";

PreparedStatement ps =
con.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS);

ps.setString(1, idNum);
ps.setString(2, name);
ps.setString(3, surname);
ps.setString(4, address);
ps.setInt(5, age);

ps.executeUpdate();

ResultSet rs = ps.getGeneratedKeys();
rs.next();

int custId = rs.getInt(1);

double premium =
calculatePremium(type, coverage);

String insertPolicy =
"INSERT INTO policies(customer_id,policy_type,coverage_amount,premium_amount) VALUES(?,?,?,?)";

PreparedStatement ps2 =
con.prepareStatement(insertPolicy);

ps2.setInt(1, custId);
ps2.setString(2, type);
ps2.setDouble(3, coverage);
ps2.setDouble(4, premium);

ps2.executeUpdate();

con.commit();

} catch (Exception e) {

con.rollback();
throw e;

} finally {

con.close();

}
}
}