package ke.co.thinksynergy.movers.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.activity.BaseActivity;
import ke.co.thinksynergy.movers.activity.MainActivity;
import ke.co.thinksynergy.movers.model.User;
import ke.co.thinksynergy.movers.network.BackEnd;
import ke.co.thinksynergy.movers.network.Client;
import ke.co.thinksynergy.movers.utils.PrefUtils;
import ke.co.toshngure.basecode.networking.ResponseHandler;
import ke.co.toshngure.basecode.utils.BaseUtils;

/**
 * Created by Anthony Ngure on 08/11/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class AddNameFragment extends BaseFragment {

    @BindView(R.id.firstNameMET)
    MaterialEditText firstNameMET;
    @BindView(R.id.lastNameMET)
    MaterialEditText lastNameMET;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    Unbinder unbinder;

    public AddNameFragment() {
    }

    public static AddNameFragment newInstance() {

        Bundle args = new Bundle();

        AddNameFragment fragment = new AddNameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_name, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //BaseUtils.cacheInput(firstNameMET, R.string.hint_first_name, PrefUtils.getInstance());
        firstNameMET.addValidator(BaseUtils.createRequiredValidator(getString(R.string.error_field_is_required)));

        //BaseUtils.cacheInput(lastNameMET, R.string.hint_last_name, PrefUtils.getInstance());
        lastNameMET.addValidator(BaseUtils.createRequiredValidator(getString(R.string.error_field_is_required)));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        connect();
    }

    @Override
    public void connect() {
        super.connect();
        if (firstNameMET.validate() && lastNameMET.validate()){
            RequestParams params = new RequestParams();
            params.put(BackEnd.Params.NAME, firstNameMET.getText().toString()+" "+lastNameMET.getText().toString());
            Client.getInstance().getClient()
                    .post(Client.absoluteUrl(BackEnd.EndPoints.USER_ADD_NAME),
                            params, new ResponseHandler(this));
        } else {
            Snackbar.make(fab, "You must fill in all the fileds!", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, view -> {}).show();
        }
    }

    @Override
    protected void onSuccessResponse(JSONObject data, JSONObject meta) {
        super.onSuccessResponse(data, meta);
        User user = PrefUtils.getInstance().getUser();
        try {
            user.setName(data.getString(BackEnd.Params.NAME));
            PrefUtils.getInstance().saveUser(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ((BaseActivity) getActivity())
                .startNewTaskActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }
}
