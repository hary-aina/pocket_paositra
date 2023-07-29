package paositra.pocket.service;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Header;

public interface ApiService {

    //login client
    @POST("auth/login")
    Call<JSONObject> login(@Query("email") String email, @Query("password") String password);

}
