package m.m.com.m.core.activity;

import android.app.Activity;
import android.os.Bundle;

import m.m.com.m.utils.DebugUtil;

/**
 * Created by pedro on 14/11/14.
 */
public class HelperActivity extends Activity {

    private HelperActivity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ctx = this;
        String action = (String) getIntent().getExtras().get("DO");
        if (action.equals("radio")) {
            DebugUtil.log("teste1");
            //Your code
        } else if (action.equals("volume")) {
            DebugUtil.log("teste2");
            //Your code
        } else if (action.equals("reboot")) {
            DebugUtil.log("teste3");
            //Your code
        } else if (action.equals("top")) {
            DebugUtil.log("teste4");
            //Your code
        } else if (action.equals("app")) {
            DebugUtil.log("teste5");
            //Your code
        }

        if (!action.equals("reboot"))
            finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
