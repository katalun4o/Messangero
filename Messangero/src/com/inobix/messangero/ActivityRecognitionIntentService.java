package com.inobix.messangero;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ActivityRecognitionIntentService extends IntentService  {

	public ActivityRecognitionIntentService() {
		super("ActivityRecognitionIntentService");
		// TODO Auto-generated constructor stub
	}
	
	//public ActivityRecognitionIntentService(String name) {
		//super(name);
		// TODO Auto-generated constructor stub
	//}

	@Override
    protected void onHandleIntent(Intent intent) {
		// If the incoming intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);
            // Get the most probable activity
            DetectedActivity mostProbableActivity =
                    result.getMostProbableActivity();
            /*
             * Get the probability that this activity is the
             * the user's actual activity
             */
            int confidence = mostProbableActivity.getConfidence();
            /*
             * Get an integer describing the type of activity
             */
            int activityType = mostProbableActivity.getType();
            if(PreferencesUtil.CurrentUserActivity == activityType)
            {
            	PreferencesUtil.CurrentUserActivityCount++;	
            }
            else
            {
            	PreferencesUtil.CurrentUserActivityCount = 0;
            }
            PreferencesUtil.CurrentUserActivity = activityType;
            
            String activityName = getNameFromType(activityType);
            
            Log.d("MessangeroAct","Activity: " + activityName);
            /*
             * At this point, you have retrieved all the information
             * for the current update. You can display this
             * information to the user in a notification, or
             * send it to an Activity or Service in a broadcast
             * Intent.
             */
            
        } else {
            /*
             * This implementation ignores intents that don't contain
             * an activity update. If you wish, you can report them as
             * errors.
             */
        }

    }

	private String getNameFromType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }


}
