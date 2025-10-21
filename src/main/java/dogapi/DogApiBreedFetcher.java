package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     *
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException{
        String url = "https://dog.ceo/api/breed/" + breed.toLowerCase() + "/list";
        Request request = new Request.Builder()
                .url(url)
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new BreedNotFoundException("Unable to fetch breed " + breed);

            }

            String jsonBase = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonBase);

            if (!jsonObject.getString("status").equals("success")) {
                throw new BreedNotFoundException("Breed is not found:" + breed);
            }

            JSONArray subBreedsArray = jsonObject.getJSONArray("message");
            List<String> subBreeds = new ArrayList<>();
            for (int i = 0; i < subBreedsArray.length(); i++) {
                subBreeds.add(subBreedsArray.getString(i));
            }

            return subBreeds;


        } catch(IOException e){
            throw new BreedNotFoundException("unable to process API request for breed");
        }
    }


}