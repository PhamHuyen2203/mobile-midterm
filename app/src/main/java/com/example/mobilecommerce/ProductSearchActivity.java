package com.example.mobilecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adapters.ProductAdapter;
import com.example.dals.ProductDAO;
import com.example.models.Product;
import com.example.utils.SessionManager;

import java.util.List;

public class ProductSearchActivity
        extends AppCompatActivity {

    private static final String TAG =
            ProductSearchActivity.class.getSimpleName();

    private EditText edtProductName;
    private EditText edtMinPrice;
    private EditText edtMaxPrice;

    private Button btnSearchProduct;

    private TextView tvSearchMessage;
    private TextView tvResultCount;
    private TextView tvEmptyProduct;

    private ListView lvProducts;

    private ProductDAO productDAO;
    private ProductAdapter productAdapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        sessionManager =
                new SessionManager(this);

        /*
         * Tất cả User đều có thể trở thành Customer.
         * Vì vậy chỉ cần kiểm tra đã đăng nhập.
         */
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(
                R.layout.activity_product_search
        );

        initializeViews();
        initializeObjects();
        initializeEvents();

        /*
         * Khi mở màn hình lần đầu,
         * hiển thị toàn bộ sản phẩm.
         */
        performSearch();
    }

    private void initializeViews() {
        edtProductName =
                findViewById(
                        R.id.edtProductName
                );

        edtMinPrice =
                findViewById(
                        R.id.edtMinPrice
                );

        edtMaxPrice =
                findViewById(
                        R.id.edtMaxPrice
                );

        btnSearchProduct =
                findViewById(
                        R.id.btnSearchProduct
                );

        tvSearchMessage =
                findViewById(
                        R.id.tvSearchMessage
                );

        tvResultCount =
                findViewById(
                        R.id.tvResultCount
                );

        tvEmptyProduct =
                findViewById(
                        R.id.tvEmptyProduct
                );

        lvProducts =
                findViewById(
                        R.id.lvProducts
                );
    }

    private void initializeObjects() {
        productDAO =
                new ProductDAO(this);

        productAdapter =
                new ProductAdapter(
                        this,
                        this::addProductToCart
                );

        lvProducts.setAdapter(
                productAdapter
        );
    }

    private void initializeEvents() {
        btnSearchProduct.setOnClickListener(
                view -> performSearch()
        );

        edtProductName.setOnEditorActionListener(
                (textView, actionId, event) -> {
                    if (actionId
                            == EditorInfo.IME_ACTION_SEARCH) {
                        performSearch();
                        return true;
                    }

                    return false;
                }
        );
    }

    private void performSearch() {
        hideMessage();

        String keyword =
                edtProductName.getText()
                        .toString()
                        .trim();

        String minPriceText =
                edtMinPrice.getText()
                        .toString()
                        .trim();

        String maxPriceText =
                edtMaxPrice.getText()
                        .toString()
                        .trim();

        boolean hasMinPrice =
                !TextUtils.isEmpty(
                        minPriceText
                );

        boolean hasMaxPrice =
                !TextUtils.isEmpty(
                        maxPriceText
                );

        /*
         * Phải nhập đầy đủ cả hai đầu khoảng giá.
         */
        if (hasMinPrice != hasMaxPrice) {
            showMessage(
                    R.string.error_enter_full_price_range
            );

            return;
        }

        Double minPrice = null;
        Double maxPrice = null;

        if (hasMinPrice) {
            try {
                minPrice =
                        Double.parseDouble(
                                minPriceText
                        );

                maxPrice =
                        Double.parseDouble(
                                maxPriceText
                        );

            } catch (NumberFormatException exception) {
                showMessage(
                        R.string.error_invalid_price
                );

                return;
            }

            if (minPrice < 0
                    || maxPrice < 0) {
                showMessage(
                        R.string.error_negative_price
                );

                return;
            }

            if (minPrice > maxPrice) {
                showMessage(
                        R.string.error_min_price_greater
                );

                return;
            }
        }

        setSearchingState(true);

        try {
            List<Product> products =
                    productDAO.searchProducts(
                            keyword,
                            minPrice,
                            maxPrice
                    );

            productAdapter.setProducts(
                    products
            );

            tvResultCount.setText(
                    getResources().getQuantityString(
                            R.plurals.product_result_count,
                            products.size(),
                            products.size()
                    )
            );

            boolean isEmpty =
                    products.isEmpty();

            tvEmptyProduct.setVisibility(
                    isEmpty
                            ? View.VISIBLE
                            : View.GONE
            );

            lvProducts.setVisibility(
                    isEmpty
                            ? View.GONE
                            : View.VISIBLE
            );

        } catch (Exception exception) {
            Log.e(
                    TAG,
                    getString(
                            R.string.error_search_product
                    ),
                    exception
            );

            showMessage(
                    R.string.error_search_product
            );

        } finally {
            setSearchingState(false);
        }
    }

    private void addProductToCart(
            Product product
    ) {
        int userID =
                sessionManager.getUserID();

        if (userID <= 0) {
            Toast.makeText(
                    this,
                    R.string.error_invalid_session,
                    Toast.LENGTH_LONG
            ).show();

            sessionManager.logout();
            redirectToLogin();

            return;
        }

        try {
            int result =
                    productDAO.addProductToCart(
                            userID,
                            product.getProductID(),
                            1
                    );

            if (result
                    == ProductDAO.CART_INSERTED) {

                String message =
                        getString(
                                R.string.message_product_inserted,
                                product.getProductName()
                        );

                Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_SHORT
                ).show();

            } else if (
                    result
                            == ProductDAO.CART_UPDATED
            ) {

                Toast.makeText(
                        this,
                        R.string.message_product_quantity_updated,
                        Toast.LENGTH_SHORT
                ).show();

            } else {
                Toast.makeText(
                        this,
                        R.string.error_add_to_cart,
                        Toast.LENGTH_LONG
                ).show();
            }

        } catch (Exception exception) {
            Log.e(
                    TAG,
                    getString(
                            R.string.error_add_to_cart
                    ),
                    exception
            );

            Toast.makeText(
                    this,
                    R.string.error_add_to_cart,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void setSearchingState(
            boolean searching
    ) {
        btnSearchProduct.setEnabled(
                !searching
        );

        btnSearchProduct.setText(
                searching
                        ? R.string.action_searching
                        : R.string.action_search
        );
    }

    private void showMessage(
            int stringResource
    ) {
        tvSearchMessage.setText(
                stringResource
        );

        tvSearchMessage.setVisibility(
                View.VISIBLE
        );
    }

    private void hideMessage() {
        tvSearchMessage.setText(null);

        tvSearchMessage.setVisibility(
                View.GONE
        );
    }

    private void redirectToLogin() {
        Intent intent =
                new Intent(
                        this,
                        LoginActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}