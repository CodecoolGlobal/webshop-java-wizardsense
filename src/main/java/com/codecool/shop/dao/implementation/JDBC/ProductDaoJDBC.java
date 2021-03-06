package com.codecool.shop.dao.implementation.JDBC;

import com.codecool.shop.config.ConnectionHandler;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDaoJDBC extends ConnectionHandler implements ProductDao {

    private static ProductDaoJDBC instance = null;

    private ProductDaoJDBC() {
    }

    public static ProductDaoJDBC getInstance() {
        if (instance == null) {
            instance = new ProductDaoJDBC();
        }
        return instance;
    }

    @Override
    public void add(Product product) throws SQLException {
        PreparedStatement statement = getConn().prepareStatement("INSERT INTO products (name, description, default_price, " +
                "default_currency, product_category_id, supplier_id, image_src) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id;");
        statement.setString(1, product.getName());
        statement.setString(2, product.getDescription());
        statement.setFloat(3, product.getDefaultPrice());
        statement.setString(4, product.getDefaultCurrency().getCurrencyCode());
        statement.setInt(5, product.getProductCategory().getId());
        statement.setInt(6, product.getSupplier().getId());
        statement.setString(7, product.getImageSrc());
        ResultSet result = statement.executeQuery();
        int cartId = product.getId();
        while (result.next()) {
            cartId = result.getInt("id");
        }
        product.setId(cartId);
        statement.close();
        result.close();
    }

    @Override
    public Product find(int id) throws SQLException {
        PreparedStatement statement = getConn().prepareStatement("SELECT * FROM products WHERE id=?;");
        statement.setInt(1, id);

        ResultSet results = statement.executeQuery();

        int productId = 0;
        String prodName = "";
        String prodDesc = "";
        float defPrice = 0;
        String defCurrency = "";
        int categoryId = 0;
        int supplierId = 0;
        String imageSrc = "";
        Product found;
        while (results.next()) {

            productId = results.getInt("id");
            prodName = results.getString("name");
            prodDesc = results.getString("description");
            defPrice = results.getFloat("default_price");
            defCurrency = results.getString("default_currency");
            categoryId = results.getInt("product_category_id");
            supplierId = results.getInt("supplier_id");
            imageSrc = results.getString("image_src");
        }

        ProductCategory category = ProductCategoryDaoJDBC.getInstance().find(categoryId);
        Supplier supplier = SupplierDaoJDBC.getInstance().find(supplierId);

        found = new Product(prodName, defPrice, defCurrency, prodDesc, category, supplier);
        found.setId(productId);
        found.setImageSrc(imageSrc);

        results.close();
        return found;
    }

    @Override
    public void remove(int id) throws SQLException {
        PreparedStatement statement = getConn().prepareStatement("DELETE FROM products WHERE id=?;");
        statement.setInt(1, id);
        statement.executeUpdate();
        statement.close();
    }

    @Override
    public List<Product> getAll() throws SQLException {
        PreparedStatement statement = getConn().prepareStatement("SELECT id FROM products;");
        ResultSet results = statement.executeQuery();

        List<Product> products = new ArrayList<>();

        while (results.next()) {

            int id = results.getInt("id");
            products.add(find(id));

        }
        results.close();
        return products;
    }

    @Override
    public List<Product> getBy(Supplier supplier) throws SQLException {

        List<Product> product = getAll();
        return product.stream().filter(prod -> prod.getSupplier().getId() == supplier.getId()).collect(Collectors.toList());
    }

    @Override
    public List<Product> getBy(ProductCategory productCategory) throws SQLException {

        List<Product> product = getAll();
        return product.stream().filter(prod -> prod.getProductCategory().getId() == productCategory.getId()).collect(Collectors.toList());
    }

    @Override
    public void removeAll() throws SQLException {
        PreparedStatement statement = getConn().prepareStatement("TRUNCATE carts CASCADE ");
            statement.executeUpdate();
            statement.close();
    }
}
