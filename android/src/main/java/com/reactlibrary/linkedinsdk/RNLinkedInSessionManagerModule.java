package com.reactlibrary.linkedinsdk;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.ApiErrorResponse;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class RNLinkedInSessionManagerModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    public RNLinkedInSessionManagerModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }


    @Override
    public String getName() {
        return "RNLinkedInSessionManager";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("R_BASICPROFILE", Scope.R_BASICPROFILE.getName());
        constants.put("R_CONTACTINFO", Scope.R_CONTACTINFO.getName());
        constants.put("R_EMAILADDRESS", Scope.R_EMAILADDRESS.getName());
        constants.put("R_FULLPROFILE", Scope.R_FULLPROFILE.getName());
        constants.put("RW_COMPANY_ADMIN", Scope.RW_COMPANY_ADMIN.getName());
        constants.put("W_SHARE", Scope.W_SHARE.getName());
        return constants;
    }

    private void log(String msg) {
        Log.d("RNLinkedInSDK", msg);
    }

    private void sendEvent(String eventName,
                           @Nullable WritableMap params) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    private LISessionManager getLISessionManager() {
        return LISessionManager.getInstance(getReactApplicationContext());
    }

    @Override
    public void onNewIntent(Intent intent) {
    }

    @Override
    public void onActivityResult(Activity activity, final int requestCode, final int resultCode, final Intent intent) {
        getLISessionManager().onActivityResult(getCurrentActivity(), requestCode, resultCode, intent);
    }

    private static Scope buildScope(ReadableArray scopes) {
        Scope.LIPermission[] permissions = new Scope.LIPermission[scopes.size()];
        for (int i = 0; i < scopes.size(); i++) {
            String scope = scopes.getString(i);
            switch (scope.toLowerCase()) {
                case "r_basicprofile":
                    permissions[i] = Scope.R_BASICPROFILE;
                    break;
                case "r_contactinfo":
                    permissions[i] = Scope.R_CONTACTINFO;
                    break;
                case "r_emailaddress":
                    permissions[i] = Scope.R_EMAILADDRESS;
                    break;
                case "r_fullprofile":
                    permissions[i] = Scope.R_FULLPROFILE;
                    break;
                case "rw_company_admin":
                    permissions[i] = Scope.RW_COMPANY_ADMIN;
                    break;
                case "w_share":
                    permissions[i] = Scope.W_SHARE;
                    break;
                default:
                    //
            }
        }
        return Scope.build(permissions);
    }

    @ReactMethod
    public void initSession(ReadableMap config, final Promise promise) {
        log("configure");
        getLISessionManager().init(getCurrentActivity(), buildScope(config.getArray("scopes")), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                AccessToken accessToken = getLISessionManager().getSession().getAccessToken();

                WritableMap params = Arguments.createMap();
                params.putString("accessToken", accessToken.getValue());
                params.putDouble("expiresOn", (double) accessToken.getExpiresOn());
                params.putBoolean("isExpired", accessToken.isExpired());

                sendEvent("RNLinkedInSessionManager.authSuccess", params);

                params = Arguments.createMap();
                params.putString("accessToken", accessToken.getValue());
                params.putDouble("expiresOn", (double) accessToken.getExpiresOn());
                params.putBoolean("isExpired", accessToken.isExpired());

                promise.resolve(params);
            }

            @Override
            public void onAuthError(LIAuthError error) {
                String errorCode = null;
                String errorMessage = null;

                String json = error.toString();
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        try { errorCode = jsonObject.getString("errorCode"); } catch (JSONException e) {}
                        try { errorMessage = jsonObject.getString("errorMessage"); } catch (JSONException e) {}
                    } catch (JSONException e) {}
                }

                WritableMap params = Arguments.createMap();
                params.putString("errorCode", errorCode);
                params.putString("errorMessage", errorMessage);

                sendEvent("RNLinkedInSessionManager.authError", params);

                params = Arguments.createMap();
                params.putString("errorCode", errorCode);
                params.putString("errorMessage", errorMessage);

                promise.reject(errorCode, errorMessage);
            }
        }, true);
    }

    @ReactMethod
    public void getRequest(String url, final Promise promise) {
        ReactApplicationContext context = getReactApplicationContext();
        APIHelper apiHelper = APIHelper.getInstance(context);
        apiHelper.getRequest(context, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                WritableMap params = Arguments.createMap();
                params.putInt("status", apiResponse.getStatusCode());
                params.putString("data", apiResponse.getResponseDataAsString());
                promise.resolve(params);
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                JSONObject json = new JSONObject();
                try {
                    json.put("statusCode", liApiError.getHttpStatusCode());
                    json.put("message", liApiError.getMessage());
                    json.put("localizedMessage", liApiError.getLocalizedMessage());
                    ApiErrorResponse errorResponse = liApiError.getApiErrorResponse();
                    if (errorResponse != null) {
                        json.put("errorStatus", errorResponse.status);
                        json.put("errorCode", errorResponse.errorCode);
                        json.put("errorMessage", errorResponse.message);
                        json.put("requestId", errorResponse.requestId);
                        json.put("timestamp", errorResponse.timestamp);
                    }
                } catch (JSONException e) {
                }
                promise.reject(Integer.toString(liApiError.getHttpStatusCode()), json.toString());
            }
        });
    }
}
