package Memory;

import com.codecool.shop.dao.GenericQueriesDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.implementation.Memory.CartDaoMem;
import com.codecool.shop.dao.implementation.Memory.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.Memory.ProductDaoMem;
import com.codecool.shop.dao.implementation.Memory.SupplierDaoMem;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class MemoryDaoTest {

    // Getting the memory DAOs
    private ProductDao productDataStore = ProductDaoMem.getInstance();
    private GenericQueriesDao<ProductCategory> productCategoryDataStore = ProductCategoryDaoMem.getInstance();
    private GenericQueriesDao<Supplier> supplierDataStore = SupplierDaoMem.getInstance();
    private GenericQueriesDao<Cart> cartDataStore = CartDaoMem.getInstance();

    private ProductCategory testCategory = new ProductCategory("Test Category", "Test Department", "Test Description");
    private Supplier testSupplier = new Supplier("Test Supplier", "Test Description");
    private Product testProduct = new Product("TestProd", 0, "HUF", "Just a test", testCategory, testSupplier);

    private ProductCategory fakeCategory = new ProductCategory("Fake Category", "Fake Department", "Fake Description");
    private Supplier fakeSupplier = new Supplier("Fake Supplier", "Fake Description");
    private Product fakeProduct = new Product("FakeProd", 0, "HUF", "Just a fake", fakeCategory, fakeSupplier);

    private Cart testCart = new Cart(new TreeMap<>(), null);

    @BeforeEach
    void cleanTheSlate() throws SQLException {
        productDataStore.removeAll();
        supplierDataStore.removeAll();
        productCategoryDataStore.removeAll();
        cartDataStore.removeAll();
    }

    @Test
    void testAddProductCategory() throws SQLException {
        productCategoryDataStore.add(testCategory);
        ProductCategory supposedProductCategory = productCategoryDataStore.find(1);
        assertEquals(testCategory, supposedProductCategory);
    }

    @Test
    void testRemoveProductCategory() throws SQLException {
        productCategoryDataStore.add(testCategory);
        productCategoryDataStore.remove(1);
        assertEquals(productCategoryDataStore.getAll(), new ArrayList<ProductCategory>());
    }

    @Test
    void testAddSupplier() throws SQLException {
        supplierDataStore.add(testSupplier);
        Supplier supposedSupplier = supplierDataStore.find(1);
        assertEquals(testSupplier, supposedSupplier);
    }

    @Test
    void testRemoveSupplier() throws SQLException {
        supplierDataStore.add(testSupplier);
        supplierDataStore.remove(1);
        assertEquals(supplierDataStore.getAll(), new ArrayList<Supplier>());
    }

    @Test
    void testAddProduct() throws SQLException {
        productDataStore.add(testProduct);
        Product supposedProduct = productDataStore.find(1);
        assertEquals(testProduct, supposedProduct);
    }

    @Test
    void testFilterBySupplier() throws SQLException {
        productDataStore.add(testProduct);
        productDataStore.add(fakeProduct);
        List<Product> supposedProducts = new ArrayList<Product>() {{
            add(testProduct);
        }};
        assertEquals(productDataStore.getBy(testSupplier), supposedProducts);
        productDataStore.remove(2);
    }

    @Test
    void testFilterByCategory() throws SQLException {
        productDataStore.add(testProduct);
        productDataStore.add(fakeProduct);
        List<Product> supposedProducts = new ArrayList<Product>() {{
            add(testProduct);
        }};
        assertEquals(productDataStore.getBy(testCategory), supposedProducts);
        productDataStore.remove(2);
    }

    @Test
    void testRemoveProduct() throws SQLException {
        productDataStore.add(testProduct);
        productDataStore.remove(1);
        assertEquals(productDataStore.getAll(), new ArrayList<Product>());
    }

    @Test
    void testAddCart() throws SQLException {
        cartDataStore.add(testCart);
        Cart supposedCart = cartDataStore.find(0);
        assertEquals(testCart, supposedCart);
    }

    @Test
    void testRemoveCart() throws SQLException {
        cartDataStore.add(testCart);
        cartDataStore.remove(0);
        assertEquals(cartDataStore.getAll(), new ArrayList<Cart>());
    }

    @Test
    void testAddToCart() throws SQLException {
        cartDataStore.add(testCart);
        Cart cartToAddTo = cartDataStore.find(0);
        cartToAddTo.addProduct(testProduct);
        HashMap<Product, Integer> supposedMap = new HashMap<Product, Integer>() {{
            put(testProduct, 1);
        }};
        assertEquals(cartToAddTo.getProductList(), supposedMap);

    }

    @Test
    void testRemoveFromCart() throws SQLException {
        cartDataStore.add(testCart);
        Cart cartToAddTo = cartDataStore.find(0);
        cartToAddTo.addProduct(testProduct);
        cartToAddTo.removeProduct(testProduct);
        HashMap<Product, Integer> supposedMap = new HashMap<>();
        assertEquals(cartToAddTo.getProductList(), supposedMap);
    }


}