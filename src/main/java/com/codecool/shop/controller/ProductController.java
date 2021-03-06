package com.codecool.shop.controller;

import com.codecool.shop.config.ErrorHandler;
import com.codecool.shop.dao.GenericQueriesDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.implementation.JDBC.*;
import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.model.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@WebServlet(urlPatterns = {"/"})
public class ProductController extends HttpServlet {
    private ProductDao productDataStore = ProductDaoJDBC.getInstance();
    private CartDaoJDBC cartDataStore = CartDaoJDBC.getInstance();
    private UserDaoJDBC userDataStore = UserDaoJDBC.getInstance();
    private SupplierDaoJDBC supplierDataStore = SupplierDaoJDBC.getInstance();
    private ProductCategoryDaoJDBC productCategoryDataStore = ProductCategoryDaoJDBC.getInstance();
    private List<Product> defaultProds = null;
    private ErrorHandler handler = new ErrorHandler();

    private void filter(HttpServletRequest req) throws SQLException {
        List<String> headers = Collections.list(req.getParameterNames());

        if (headers.contains("filter")) {
            String filterName = req.getParameter("filter").trim();

            List<Supplier> suppliers = supplierDataStore.getAll().stream().filter(supplier -> supplier.getName().equals(filterName)).collect(Collectors.toList());
            List<ProductCategory> categories = productCategoryDataStore.getAll().stream().filter(cat -> cat.getName().equals(filterName)).collect(Collectors.toList());


            if (suppliers.size() > 0) {
                defaultProds = productDataStore.getBy(supplierDataStore.find(suppliers.get(0).getId()));
            } else {
                defaultProds = productDataStore.getBy(productCategoryDataStore.find(categories.get(0).getId()));
            }
        } else if (headers.contains("reset")) {
            defaultProds = null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();

        try {
            int cartSize = 0;
            if (session.getAttribute("userID") != null) {
                int userID = (int) session.getAttribute("userID");
                Cart cart = cartDataStore.getCartByUserId(userID);

                cartSize = cart != null ? cartDataStore.getCartByUserId(userID).getSumOfProducts() : 0;
            }


            TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
            WebContext context = new WebContext(req, resp, req.getServletContext());


            filter(req);
            context.setVariable("categories", productCategoryDataStore.getAll());
            context.setVariable("suppliers", supplierDataStore.getAll());
            context.setVariable("cartSize", cartSize);
            context.setVariable("products", defaultProds != null ? defaultProds : productDataStore.getAll());
            context.setVariable("userID", session.getAttribute("userID"));
            context.setVariable("userName", session.getAttribute("userName"));


            engine.process("product/index.html", context, resp.getWriter());
        } catch (IOException | SQLException e) {
            handler.ExceptionOccurred(resp, session, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

        List<String> headers = Collections.list(req.getParameterNames());
        HttpSession session = req.getSession();

        try {
            if (headers.contains("product")) {

                int userId = (int) session.getAttribute("userID");
                User user = userDataStore.find(userId);

                if (user != null) {
                    int productId = Integer.parseInt(req.getParameter("product"));
                    Product product = productDataStore.find(productId);

                    TreeMap<Product, Integer> products = new TreeMap<>();
                    products.put(product, 1);

                    Cart cartToCheck = cartDataStore.getCartByUserId(userId);

                    Cart newCart = new Cart(products, user);
                    newCart.setId(userId);

                    if (cartToCheck == null) {
                        cartDataStore.add(newCart);
                    } else {

                        if (cartDataStore.getCartProductQuantity(cartToCheck, productId) >= 1) {
                            cartDataStore.increaseOrDecreaseQuantity(cartToCheck, productId, true);
                        } else {
                            cartDataStore.add(newCart);
                        }
                    }
                }
            }
            doGet(req, resp);

        } catch (NumberFormatException | SQLException e) {
            handler.ExceptionOccurred(resp, session, e);
        }
    }

}