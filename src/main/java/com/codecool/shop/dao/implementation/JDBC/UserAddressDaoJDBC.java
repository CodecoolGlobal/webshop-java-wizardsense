package com.codecool.shop.dao.implementation.JDBC;

import com.codecool.shop.config.ConnectionHandler;
import com.codecool.shop.dao.GenericQueriesDao;
import com.codecool.shop.model.UserAddress;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserAddressDaoJDBC extends ConnectionHandler implements GenericQueriesDao<UserAddress> {
    private PreparedStatement statement;
    private static UserAddressDaoJDBC instance = null;

    private UserAddressDaoJDBC() {
    }

    public static UserAddressDaoJDBC getInstance() {
        if (instance == null) {
            instance = new UserAddressDaoJDBC();
        }
        return instance;
    }


    @Override
    public void add(UserAddress userAddress) throws SQLException {
        statement = getConn().prepareStatement("INSERT INTO addresses (user_id, name, e_mail, phone_number, country, city, zip_code, address) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;");
        statement.setInt(1, userAddress.getUserId());
        statement.setString(2, userAddress.getOrderFields().get("name"));
        statement.setString(3, userAddress.getOrderFields().get("eMail"));
        statement.setString(4, userAddress.getOrderFields().get("phoneNumber"));
        statement.setString(5, userAddress.getOrderFields().get("country"));
        statement.setString(6, userAddress.getOrderFields().get("city"));
        statement.setString(7, userAddress.getOrderFields().get("zipCode"));
        statement.setString(8, userAddress.getOrderFields().get("address"));
        ResultSet result = statement.executeQuery();
        int cartId = userAddress.getId();
        while (result.next()) {
            cartId = result.getInt("id");
        }
        userAddress.setId(cartId);
        statement.close();
    }

    @Override
    public UserAddress find(int id) throws SQLException {
        UserAddress address;
        statement = getConn().prepareStatement("SELECT * FROM addresses WHERE id = ?;");
        statement.setInt(1, id);

        ResultSet results = statement.executeQuery();

        int userId = 0;
        HashMap<String, String> addressFields = new HashMap<>();
        while (results.next()) {

            addressFields.put("name", results.getString("name"));
            addressFields.put("eMail", results.getString("e_mail"));
            addressFields.put("phoneNumber", results.getString("phone_number"));
            addressFields.put("country", results.getString("country"));
            addressFields.put("city", results.getString("city"));
            addressFields.put("zipCode", results.getString("zip_code"));
            addressFields.put("address", results.getString("address"));

            userId = results.getInt("user_id");
        }
        address = new UserAddress(addressFields, userId);
        address.setId(id);

        statement.close();
        results.close();

        return address;
    }

    @Override
    public void remove(int id) throws SQLException {
        statement = getConn().prepareStatement("DELETE FROM addresses WHERE id=?;");
        statement.setInt(1, id);
        statement.executeUpdate();
        statement.close();
    }

    @Override
    public List<UserAddress> getAll() throws SQLException {
        List<UserAddress> addresses = new ArrayList<>();
        PreparedStatement statement = getConn().prepareStatement("SELECT id FROM addresses");
        ResultSet results = statement.executeQuery();

        while (results.next()) {

            int id = results.getInt("id");
            addresses.add(find(id));

        }
        statement.close();
        return addresses;
    }

    @Override
    public void removeAll() throws SQLException {
        PreparedStatement statement = getConn().prepareStatement("TRUNCATE addresses CASCADE ");
        statement.executeUpdate();
        statement.close();
    }

    public List<UserAddress> getAddressByUserId(int userID) throws SQLException {
        PreparedStatement statement = getConn().prepareStatement("SELECT * FROM addresses WHERE user_id=?;");
        statement.setInt(1, userID);

        List<UserAddress> addressesOfUser = new ArrayList<>();
        ResultSet results = statement.executeQuery();

        while (results.next()) {
            addressesOfUser.add(find(results.getInt("id")));
        }

        statement.close();
        return addressesOfUser;
    }
}
