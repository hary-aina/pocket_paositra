package paositra.pocket.service;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Header;

public interface ApiService {

    //login client
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("auth/login")
    Call<JsonObject> login(@Body RequestBody requestBody);

    //get solde
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("operation/carte/getSolde/{wallet_carte}")
    Call<JsonObject> getSolde(@Path("wallet_carte") String wallet_carte, @Header("Authorization") String authorization);

    //activate compte
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("operation/carte/activerCarte")
    Call<JsonObject> activateAccount(@Header("Authorization") String authorization);

    //desactiver compte
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("operation/carte/desactiverCarte")
    Call<JsonObject> unactivateAccount(@Header("Authorization") String authorization);

    //avoir les dix dernier transaction
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("operation/historique/getTenLastTransactions")
    Call<JsonObject> getLastTenTransactions(@Header("Authorization") String authorization);


}
