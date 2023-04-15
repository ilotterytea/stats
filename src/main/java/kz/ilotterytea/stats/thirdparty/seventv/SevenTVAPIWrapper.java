package kz.ilotterytea.stats.thirdparty.seventv;

import com.google.gson.Gson;
import kz.ilotterytea.stats.SharedConstants;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.api.EmoteSet;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.api.SevenTVUser;
import kz.ilotterytea.stats.thirdparty.seventv.schemas.api.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class SevenTVAPIWrapper {
    public static User getUser(Integer userId) {
        Logger logger = LoggerFactory.getLogger(SevenTVAPIWrapper.class.getSimpleName());
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .get()
                .url(String.format(SharedConstants.STV_API_USER_ENDPOINT, userId))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() != 200 || response.body() == null) {
                logger.warn("Couldn't get a user: " + ((response.code() != 200) ? "received " + response.code() + " status code!" : "response body is null!"));
                return null;
            }

            return new Gson().fromJson(response.body().string(), User.class);
        } catch (IOException e) {
            logger.error("Couldn't get a user: ", e);
            return null;
        }
    }

    public static SevenTVUser getSevenTVUser(String sevenTVId) {
        Logger logger = LoggerFactory.getLogger(SevenTVAPIWrapper.class.getSimpleName());
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .get()
                .url(String.format(SharedConstants.STV_API_SEVENTV_USER_ENDPOINT, sevenTVId))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() != 200 || response.body() == null) {
                logger.warn("Couldn't get a user: " + ((response.code() != 200) ? "received " + response.code() + " status code!" : "response body is null!"));
                return null;
            }

            return new Gson().fromJson(response.body().string(), SevenTVUser.class);
        } catch (IOException e) {
            logger.error("Couldn't get a user: ", e);
            return null;
        }
    }

    public static EmoteSet getEmoteSet(String seventvID) {
        Logger logger = LoggerFactory.getLogger(SevenTVAPIWrapper.class.getSimpleName());
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .get()
                .url(String.format(SharedConstants.STV_API_EMOTESET_ENDPOINT, seventvID))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() != 200 || response.body() == null) {
                logger.warn("Couldn't get an emote set: " + ((response.code() != 200) ? "received " + response.code() + " status code!" : "response body is null!"));
                return null;
            }

            return new Gson().fromJson(response.body().string(), EmoteSet.class);
        } catch (IOException e) {
            logger.error("Couldn't get an emote set: ", e);
            return null;
        }
    }
}
