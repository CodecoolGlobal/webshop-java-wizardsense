package com.codecool.shop.controller;

import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.implementation.JDBC.CartDaoJDBC;
import com.codecool.shop.dao.implementation.JDBC.ProductDaoJDBC;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


@WebServlet(urlPatterns = {"/shopping-cart"})
public class CartController extends HttpServlet {
    private CartDaoJDBC cartDataStore = CartDaoJDBC.getInstance();
    private ProductDaoJDBC productDataStore = ProductDaoJDBC.getInstance();

    private void addOrRemoveProduct(HttpServletRequest req){
        List<String> headers = Collections.list(req.getParameterNames());

        HttpSession session = req.getSession();

        int userId = (int)session.getAttribute("userID");

        Cart cart = cartDataStore.getCartByUserId(userId);

        if (headers.contains("increase")) {
            int prodId = Integer.parseInt(req.getParameter("increase"));

            cartDataStore.increaseOrDecreaseQuantity(cart, prodId, true);
        }
        else if (headers.contains("decrease")) {
            int prodId = Integer.parseInt(req.getParameter("decrease"));

            if(cartDataStore.getCartProductQuantity(cart, prodId) <= 1) {
                cartDataStore.remove(prodId);
            } else {
                cartDataStore.increaseOrDecreaseQuantity(cart, prodId, false);
            }
        }
    }

    private float getTotalSum(Cart cart) {
        float sum = 0;

        for (Product product : cart.getProductList().keySet()) {
            sum += product.getDefaultPrice() * cart.getProductList().get(product);
        }
        return sum;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<String> headers = Collections.list(req.getParameterNames());
        if(headers.contains("increase") || headers.contains("decrease")) {
            addOrRemoveProduct(req);
        }

        HttpSession session = req.getSession();

        Integer userId = (Integer)session.getAttribute("userID");

        Cart cart = cartDataStore.getCartByUserId(userId);


        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());

        context.setVariable("cart" , cart);
        context.setVariable("totalSum", cart == null || cart.getProductList().isEmpty() ? 0 : getTotalSum(cart));
        context.setVariable("userID", session.getAttribute("userID"));
        context.setVariable("userName", session.getAttribute("userName"));

        engine.process("product/shopping-cart.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        doGet(req, resp);
    }

}

// // Alternative setting of the template context
// Map<String, Object> params = new HashMap<>();
// params.put("category", productCategoryDataStore.find(1));
// params.put("products", productDataStore.getBy(productCategoryDataStore.find(1)));
// context.setVariables(params);