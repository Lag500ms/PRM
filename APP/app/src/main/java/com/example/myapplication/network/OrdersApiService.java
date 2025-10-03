package com.example.myapplication.network;

import com.example.myapplication.model.common.PageResponse;
import com.example.myapplication.model.orders.CreateOrderRequest;
import com.example.myapplication.model.orders.OrderResponse;
import com.example.myapplication.model.orders.UpdateOrderRequest;
import com.example.myapplication.model.orders.UpdateStatusRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrdersApiService {

    @GET("v1/dealer/orders")
    Call<PageResponse<OrderResponse>> listOrders(@Query("page") int page, @Query("size") int size,
            @Query("status") String status);

    @GET("v1/dealer/orders/{id}")
    Call<OrderResponse> getOrder(@Path("id") String id);

    @POST("v1/dealer/orders")
    Call<OrderResponse> createOrder(@Body CreateOrderRequest request);

    @PUT("v1/dealer/orders")
    Call<OrderResponse> updateOrder(@Body UpdateOrderRequest request);

    @PUT("v1/dealer/orders/status")
    Call<OrderResponse> updateStatus(@Body UpdateStatusRequest request);

    @DELETE("v1/dealer/orders/{id}")
    Call<Void> deleteOrder(@Path("id") String id);
}
