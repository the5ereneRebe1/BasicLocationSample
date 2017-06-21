package com.google.android.gms.location.sample.basiclocationsample.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MeetService extends Service {
    public MeetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
