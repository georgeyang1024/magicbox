package cn.georgeyang.csdnblog.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by george.yang on 16/5/1.
 */
public class BaseFragment extends Fragment {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(bugFixClickListener);
    }

    protected Context plugInContext;
    public void setPlugInContext (Context context) {
        this.plugInContext = context;
    }

    private View.OnClickListener bugFixClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}
