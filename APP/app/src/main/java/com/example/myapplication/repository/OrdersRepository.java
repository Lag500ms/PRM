package com.example.myapplication.repository;

import android.content.Context;
import com.example.myapplication.model.common.PageResponse;
import com.example.myapplication.model.orders.CreateOrderRequest;
import com.example.myapplication.model.orders.UpdateOrderRequest;
import com.example.myapplication.model.orders.UpdateStatusRequest;
import com.example.myapplication.model.orders.OrderResponse;
import com.example.myapplication.network.OrdersApiService;
import com.example.myapplication.network.RetrofitClient;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class OrdersRepository {

    private final OrdersApiService api;

    public OrdersRepository(Context context) {
        this.api = RetrofitClient.createWithAuth(context, OrdersApiService.class);
    }

    public PageResponse<OrderResponse> list(int page, int size, String status) throws IOException {
        return api.listOrders(page, size, status).execute().body();
    }

    public OrderResponse getById(String id) throws IOException {
        return api.getOrder(id).execute().body();
    }

    public OrderResponse create(CreateOrderRequest request) throws IOException {
        return api.createOrder(request).execute().body();
    }

    public OrderResponse update(UpdateOrderRequest request) throws IOException {
        return api.updateOrder(request).execute().body();
    }

    public OrderResponse updateStatus(UpdateStatusRequest request) throws IOException {
        return api.updateStatus(request).execute().body();
    }

    public boolean delete(String id) throws IOException {
        Response<Void> resp = api.deleteOrder(id).execute();
        return resp.isSuccessful();
    }
}
