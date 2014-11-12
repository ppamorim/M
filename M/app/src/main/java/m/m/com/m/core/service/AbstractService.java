package m.m.com.m.core.service;

import android.os.AsyncTask;
import android.os.Build;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class AbstractService extends AsyncTask<Request, Void, String> {

    private static final int ERROR_REQUEST = -1;

    private OnRequestResponse mOnRequestResponse;
    private Request mRequest;

    public AbstractService(Request request, OnRequestResponse onRequestResponse) {
        mRequest = request;
        mOnRequestResponse = onRequestResponse;

    }

    public void runOnAsyncTask() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mRequest);
        } else {
            execute(mRequest);
        }
    }

    public void runOnUiThread() {
        try {
            new OkHttpClient()
                    .newCall(mRequest)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            mOnRequestResponse.onFail(-2);
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            mOnRequestResponse.onSucess(response.body().string());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Request... request) {
        try {
            return new OkHttpClient()
                    .newCall(request[0])
                    .execute()
                    .body()
                    .string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result != null) {
            mOnRequestResponse.onSucess(result);
        } else {
            mOnRequestResponse.onFail(ERROR_REQUEST);
        }

    }

    public static interface OnRequestResponse {
        public void onSucess(String response);
        public void onFail(int error);
    }

}
