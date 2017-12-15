package com.sdt.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sdt.lib.BaseRequest;
import com.sdt.lib.HttpCallback;
import com.sdt.lib.HttpTask;
import com.sdt.lib.IdCreator;
import com.sdt.lib.TaskDispatcher;

public class MainActivity extends AppCompatActivity {

    private int requestId;
    ProgressBar mProgressBar;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.loadingbar);
        tvResult=findViewById(R.id.result_text);
        getData();

    }

    void getData() {
        requestId = IdCreator.gen();
        CheckUpdateRequest request = new CheckUpdateRequest(this);
        HttpTask task = new HttpTask(this, request, requestId, callback);
        TaskDispatcher.getInstance().dispatch(task);
        mProgressBar.setVisibility(View.VISIBLE);

    }

    HttpCallback callback = new HttpCallback() {
        @Override
        public void onRequestSuccess(int id, BaseRequest request) {
            UpdateBean bean = ((CheckUpdateRequest) request).getResultData();
            mProgressBar.setVisibility(View.GONE);
            tvResult.setText(bean.toString());
        }

        @Override
        public void onRequestFail(int id, BaseRequest request) {
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestCancel(int id) {
            mProgressBar.setVisibility(View.GONE);
        }
    };

}
