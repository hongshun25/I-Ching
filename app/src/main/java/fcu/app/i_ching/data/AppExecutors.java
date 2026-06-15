package fcu.app.i_ching.data;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static volatile AppExecutors instance;

    private final Executor diskIo;
    private final Executor mainThread;

    public AppExecutors(Executor diskIo, Executor mainThread) {
        this.diskIo = diskIo;
        this.mainThread = mainThread;
    }

    public static AppExecutors get() {
        if (instance == null) {
            synchronized (AppExecutors.class) {
                if (instance == null) {
                    instance = new AppExecutors(
                            Executors.newSingleThreadExecutor(),
                            new MainThreadExecutor()
                    );
                }
            }
        }
        return instance;
    }

    public static AppExecutors direct() {
        Executor direct = Runnable::run;
        return new AppExecutors(direct, direct);
    }

    public Executor diskIo() {
        return diskIo;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    }
}
