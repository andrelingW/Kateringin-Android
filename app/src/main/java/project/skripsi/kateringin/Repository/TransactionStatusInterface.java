package project.skripsi.kateringin.Repository;

import project.skripsi.kateringin.Model.TransactionResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface TransactionStatusInterface {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Authorization: Basic U0ItTWlkLXNlcnZlci1qOEk4WVhBNVU5WDdObmMxeVNudE1wR1I6S2F0ZXJpbmdpbjEyMw=="
    })
    @GET("v2/{orderId}/status")
    Call<TransactionResponse> getTransactionResponse(@Path("orderId") String orderId);
}
