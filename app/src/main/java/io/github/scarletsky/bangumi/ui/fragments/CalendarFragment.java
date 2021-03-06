package io.github.scarletsky.bangumi.ui.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import java.util.List;

import io.github.scarletsky.bangumi.R;
import io.github.scarletsky.bangumi.adapters.FragmentAdapter;
import io.github.scarletsky.bangumi.api.ApiManager;
import io.github.scarletsky.bangumi.api.models.Calendar;
import io.github.scarletsky.bangumi.events.GetCalendarEvent;
import io.github.scarletsky.bangumi.utils.BusProvider;
import io.github.scarletsky.bangumi.utils.ToastManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scarlex on 15-7-2.
 */
public class CalendarFragment extends BaseToolbarFragment {

    private static final String TAG = CalendarFragment.class.getSimpleName();
    private List<Calendar> mCalendars;
    private int currentPosition = 0;


    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentAdapter pagerAdapter = new FragmentAdapter(
                getActivity(),
                getActivity().getSupportFragmentManager(),
                FragmentAdapter.PagerType.CALENDAR);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs_wrapper).findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) getView().findViewById(R.id.pager);

        pager.setAdapter(pagerAdapter);
        tabs.setViewPager(pager);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (currentPosition != position) {

                    currentPosition = position;

                    if (mCalendars != null) {
                        BusProvider.getInstance().post(new GetCalendarEvent(mCalendars));
                    }

                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ApiManager.getBangumiApi().listCalendar(new Callback<List<Calendar>>() {
            @Override
            public void success(List<Calendar> calendars, Response response) {
                mCalendars = calendars;
                BusProvider.getInstance().post(new GetCalendarEvent(mCalendars));
            }

            @Override
            public void failure(RetrofitError error) {
                ToastManager.show(getActivity(), getString(R.string.toast_collection_update_successfully));

            }
        });
    }

    @Override
    protected void setToolbarTitle() {
        getToolbar().setTitle(getString(R.string.title_calendar));
    }
}
