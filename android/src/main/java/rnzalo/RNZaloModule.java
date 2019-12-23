
package rnzalo;


import com.zing.zalo.zalosdk.oauth.OAuthCompleteListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.zing.zalo.zalosdk.oauth.ZaloOpenAPICallback;
import com.zing.zalo.zalosdk.oauth.ZaloSDK;
import com.zing.zalo.zalosdk.oauth.OauthResponse;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Promise;
import com.zing.zalo.zalosdk.oauth.LoginVia;
import com.zing.zalo.zalosdk.oauth.LoginVia;
import android.util.Log;
import org.json.JSONObject;

public class RNZaloModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mReactContext;


    public RNZaloModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mReactContext = reactContext;
    }

    @ReactMethod
    public void login(final Promise promise) {
        ZaloSDK.Instance.unauthenticate();
        final ReactApplicationContext activity = this.mReactContext;
        final String[] Fields = {"id", "birthday", "gender", "picture", "name"};
        ZaloSDK.Instance.authenticate(this.mReactContext.getCurrentActivity(), LoginVia.APP_OR_WEB , new OAuthCompleteListener() {
            @Override
            public void onAuthenError(int errorCode, String message) {
                final String code = errorCode + "";
                Log.v("ReactNative", "fail");
                promise.reject(code, message);
            }

            @Override
            public void onGetOAuthComplete(long uId, String oauthCode, String channel) {
                final WritableMap params = Arguments.createMap();
                params.putString("uId", "" + uId);
                params.putString("oauthCode", "" + oauthCode);
                params.putString("channel", "" + channel);
                promise.resolve(params);
                ZaloSDK.Instance.getProfile(activity, new ZaloOpenAPICallback() {
                    @Override
                    public void onResult(JSONObject data) {
                        try {
                            WritableMap user = UtilService.convertJsonToMap(data);
                            params.putMap("user", user);
                            promise.resolve(params);
                        } catch (Exception ex) {
                            String message = ex.getMessage();
                            promise.reject("Get profile error", message);
                        }
                    }
                }, Fields);
            }

            @Override
            public void onGetOAuthComplete(OauthResponse oauthResponse) {
                super.onGetOAuthComplete(oauthResponse);
            }
        });
    }

    @ReactMethod
    public void logout() {
        ZaloSDK.Instance.unauthenticate();
    }


    @Override
    public String getName() {
        return "RNZalo";
    }
}
