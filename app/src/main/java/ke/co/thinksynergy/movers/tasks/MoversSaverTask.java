package ke.co.thinksynergy.movers.tasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ke.co.thinksynergy.movers.model.User;
import ke.co.toshngure.basecode.utils.BaseUtils;

/**
 * Created by Anthony Ngure on 11/12/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class MoversSaverTask extends AsyncTask<JSONArray, Void, List<User>> {

    private OnTaskFinishListener<List<User>> mTaskFinishListener;

    public MoversSaverTask(OnTaskFinishListener<List<User>> listener) {
        this.mTaskFinishListener = listener;
    }

    @Override
    protected List<User> doInBackground(JSONArray... data) {
        List<User> nearByUsers = new ArrayList<>();
        try {
            for (int i = 0; i < data[0].length(); i++) {
                JSONObject userObject = data[0].getJSONObject(i);
                nearByUsers.add(BaseUtils.getSafeGson().fromJson(userObject.toString(), User.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return nearByUsers;
    }

    @Override
    protected void onPostExecute(List<User> movers) {
        super.onPostExecute(movers);
        mTaskFinishListener.onTaskFinish(movers);
    }
}
