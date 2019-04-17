package com.tangem.data.network;

import android.util.Log;

import com.tangem.App;
import com.tangem.data.network.model.BlockcypherBody;
import com.tangem.data.network.model.BlockcypherFee;
import com.tangem.data.network.model.BlockcypherResponse;

import java.util.Random;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerApiBlockcypher {
    private static String TAG = ServerApiRipple.class.getSimpleName();

    public static final String BLOCKCYPHER_ADDRESS = "blockcypher_address";
    public static final String BLOCKCYPHER_FEE = "blockcypher_fee";
    public static final String BLOCKCYPHER_SEND = "blockcypher_send";

    private int requestsCount = 0;

    public boolean isRequestsSequenceCompleted() {
        Log.i(TAG, String.format("isRequestsSequenceCompleted: %s (%d requests left)", String.valueOf(requestsCount <= 0), requestsCount));
        return requestsCount <= 0;
    }

    private ResponseListener responseListener;

    public interface ResponseListener {
        void onSuccess(String method, BlockcypherResponse blockcypherResponse);

        void onSuccess(String method, BlockcypherFee blockcypherFee);

        void onFail(String method, String message);
    }

    public void setResponseListener(ResponseListener listener) {
        responseListener = listener;
    }

    public void requestData(String method, String wallet, String tx) {
        requestsCount++;
        BlockcypherApi blockcypherApi = App.Companion.getNetworkComponent().getRetrofitBlockcypher().create(BlockcypherApi.class);

        switch (method) {
            case BLOCKCYPHER_ADDRESS:
                Call<BlockcypherResponse> addressCall = blockcypherApi.blockcypherAddress(wallet);
                addressCall.enqueue(new Callback<BlockcypherResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BlockcypherResponse> call, @NonNull Response<BlockcypherResponse> response) {
                        if (response.code() == 200) {
                            requestsCount--;
                            responseListener.onSuccess(method, response.body());
                            Log.i(TAG, "requestData " + method + " onResponse " + response.code());
                        } else {
                            responseListener.onFail(method, String.valueOf(response.code()));
                            Log.e(TAG, "requestData " + method + " onResponse " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BlockcypherResponse> call, @NonNull Throwable t) {
                        responseListener.onFail(method, String.valueOf(t.getMessage()));
                        Log.e(TAG, "requestData " + method + " onFailure " + t.getMessage());
                    }
                });
                break;

            case BLOCKCYPHER_FEE:
                Call<BlockcypherFee> feeCall = blockcypherApi.blockcypherMain();
                feeCall.enqueue(new Callback<BlockcypherFee>() {
                    @Override
                    public void onResponse(@NonNull Call<BlockcypherFee> call, @NonNull Response<BlockcypherFee> response) {
                        if (response.code() == 200) {
                            requestsCount--;
                            responseListener.onSuccess(method, response.body());
                            Log.i(TAG, "requestData " + method + " onResponse " + response.code());
                        } else {
                            responseListener.onFail(method, String.valueOf(response.code()));
                            Log.e(TAG, "requestData " + method + " onResponse " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BlockcypherFee> call, @NonNull Throwable t) {
                        responseListener.onFail(method, String.valueOf(t.getMessage()));
                        Log.e(TAG, "requestData " + method + " onFailure " + t.getMessage());
                    }
                });
                break;

            case BLOCKCYPHER_SEND:
                BlockcypherToken blockcypherToken = BlockcypherToken.values()[new Random().nextInt(BlockcypherToken.values().length)];

                Call<BlockcypherResponse> sendCall = blockcypherApi.blockcypherPush(new BlockcypherBody(tx), blockcypherToken.getToken());
                sendCall.enqueue(new Callback<BlockcypherResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BlockcypherResponse> call, @NonNull Response<BlockcypherResponse> response) {
                        if (response.code() == 201) {
                            requestsCount--;
                            responseListener.onSuccess(method, response.body());
                            Log.i(TAG, "requestData " + method + " onResponse " + response.code());
                        } else {
                            responseListener.onFail(method, String.valueOf(response.code()));
                            Log.e(TAG, "requestData " + method + " onResponse " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BlockcypherResponse> call, @NonNull Throwable t) {
                        responseListener.onFail(method, String.valueOf(t.getMessage()));
                        Log.e(TAG, "requestData " + method + " onFailure " + t.getMessage());
                    }
                });
                break;

            default:
                responseListener.onFail(method, "undeclared method");
                Log.e(TAG, "requestData " + method + " onFailure - undeclared method");
                break;
        }
    }
}
