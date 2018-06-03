package kevinliao.com.quakefinder;

import java.util.List;

import kevinliao.com.quakefinder.network.Earthquake;

public interface MainContract {

    interface View {
        void showEvent(List<Earthquake> list);

        void showProgressbar();

        void hideProgressbar();

        void showNetworkErrorMessage();
    }

    interface Presenter {
        void takeView(View view);

        void dropView();

        void loadEvent(String start);

        int loadEventFromDatabase(long start, long end);

        void loadEventFromCloud(long start, long end);
    }
}
