package ke.co.thinksynergy.movers.tasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ke.co.thinksynergy.movers.model.DriverOption;
import ke.co.thinksynergy.movers.network.BackEnd;
import ke.co.toshngure.basecode.utils.BaseUtils;

/**
 * Created by Anthony Ngure on 11/12/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class ParseDriverOptionsTask extends AsyncTask<JSONObject, Void, List<DriverOption>> {

    private OnTaskFinishListener<List<DriverOption>> mTaskFinishListener;

    public ParseDriverOptionsTask(OnTaskFinishListener<List<DriverOption>> listener) {
        this.mTaskFinishListener = listener;
    }

    @Override
    protected List<DriverOption> doInBackground(JSONObject... data) {
        List<DriverOption> driverOptions = new ArrayList<>();
        try {
            JSONArray optionsJsonArray = data[0].getJSONArray(BackEnd.Response.DATA);
            for (int i = 0; i < optionsJsonArray.length(); i++) {
                JSONObject questionJsonObject = optionsJsonArray.getJSONObject(i);
                DriverOption option = BaseUtils.getSafeGson().fromJson(questionJsonObject.toString(), DriverOption.class);
                driverOptions.add(option);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return driverOptions;
    }

    @Override
    protected void onPostExecute(List<DriverOption> driverOptions) {
        super.onPostExecute(driverOptions);
        mTaskFinishListener.onTaskFinish(driverOptions);
    }
}
