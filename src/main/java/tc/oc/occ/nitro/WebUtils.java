package tc.oc.occ.nitro;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WebUtils {
  private static final String USERNAME_API = "https://api.mojang.com/minecraft/profile/lookup/name/";

    public static CompletableFuture<UUID> getUUID(String username) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        URL urlOBJ = new URI(USERNAME_API + Preconditions.checkNotNull(username)).toURL();
                        HttpsURLConnection connection = (HttpsURLConnection) urlOBJ.openConnection();
                        connection.setRequestMethod("GET");
                        if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                            StringBuilder sb = new StringBuilder();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                sb.append(line);
                            }
                            JsonObject json = new Gson().fromJson(sb.toString(), JsonObject.class);
                            String uuid = new StringBuilder(json.get("id").getAsString())
                                    .insert(20, '-')
                                    .insert(16, '-')
                                    .insert(12, '-')
                                    .insert(8, '-')
                                    .toString();
                            return UUID.fromString(uuid);
                        }
                        return null;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
