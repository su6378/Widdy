package com.example.widdy.begin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BeginVPagerAdapter extends FragmentStateAdapter {

    public int mCount;

    public BeginVPagerAdapter(@NonNull FragmentActivity fragmentActivity, int count) {
        super(fragmentActivity);
        this.mCount = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);
        if(index==0) return new First();
        else if(index==1) return new Second();
        else if(index==2) return new Third();
        else if(index==3) return new Fourth();
        else return new First();

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public int getRealPosition(int position) { return position % mCount; }

}
