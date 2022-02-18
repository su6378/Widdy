package com.example.widdy.newnhot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.widdy.R;
import com.example.widdy.profile.ProfileEtc;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class NewHot extends AppCompatActivity {

    private ImageView newhot_profile;

    /**
     * TabLayout
     */
    private TabLayout mTabLayout;
    /**
     * 内容列表
     */
    private RecyclerView mRecyclerView;
    /**
     * AppBarLayout
     */
    private AppBarLayout mAppBar;

    /**
     * LinearLayoutManager
     */
    private LinearLayoutManager mLinearLayoutManager;
    /**
     * 数据源
     */
    private ArrayList<Item> mItems;


    /**
     * 是否处于滚动状态，避免连锁反应
     */
    private boolean isScroll;
    /**
     * RecyclerView高度
     */
    private int mRecyclerViewHeight;
    /**
     * 平滑滚动 Scroller
     */
    private RecyclerView.SmoothScroller mSmoothScroller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_newnhot);

        mTabLayout = findViewById(R.id.tab_layout);
        mRecyclerView = findViewById(R.id.recycler_view);
        /*mAppBar = view.findViewById(R.id.app_bar);
        ((CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams()).setBehavior(new FixAppBarLayoutBehavior());*/
        initRecyclerView();
        initTabLayout();
        initData();

        //프로필 기타 페이지로 이동
        newhot_profile = findViewById(R.id.newhot_profile);

        newhot_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewHot.this, ProfileEtc.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
            }
        });




    }
    private void initData() {
        mItems = new ArrayList<>();
        Item item = new Item();
        item.name = "공개 예정";
        ArrayList<Item.SubItem> itemsub = new ArrayList<>();
        itemsub.add(new Item.SubItem("便民生活----", "这是描述"));
        itemsub.add(new Item.SubItem("充值中心----", "这是描述"));
        itemsub.add(new Item.SubItem("便民生活----", "这是描述"));
        itemsub.add(new Item.SubItem("便民生活----", "这是描述"));
        itemsub.add(new Item.SubItem("便民生活----", "这是描述"));
        item.mSubItems = itemsub;

        Item item1 = new Item();
        item1.name = "모두가 좋아해요";
        ArrayList<Item.SubItem> itemsub1 = new ArrayList<>();
        itemsub1.add(new Item.SubItem("余额宝======", "这是描述"));
        itemsub1.add(new Item.SubItem("花呗======", "这是描述"));
        itemsub1.add(new Item.SubItem("芝麻信用=========", "这是描述"));
        itemsub1.add(new Item.SubItem("蚂蚁借呗======", "这是描述"));
        item1.mSubItems = itemsub1;

        Item item2 = new Item();
        item2.name = "톱 10";
        ArrayList<Item.SubItem> itemsub2 = new ArrayList<>();
        itemsub2.add(new Item.SubItem("资金往来", "这是描述"));
        itemsub2.add(new Item.SubItem("资金往来", "这是描述"));
        item2.mSubItems = itemsub2;


        mItems.add(item);
        mItems.add(item1);
        mItems.add(item2);
        //这里模仿接口回调，动态设置TabLayout和RecyclerView 相同数据。保证position
        for (Item it : mItems) {
            mTabLayout.addTab(mTabLayout.newTab().setText(it.name));

        }

       /* mTabLayout.getTabAt(0).setIcon(R.drawable.ic_popcorn);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_burning);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_top10);*/
        MyAdapter myAdapter = new MyAdapter(mItems);
        mRecyclerView.setAdapter(myAdapter);

    }
    private void initTabLayout() {
        mTabLayout.setTabMode(TabLayout.MODE_AUTO);
        //mTabLayout.setTabTextColors(Color.WHITE, ContextCompat.getColor(getActivity(), R.color.black));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //点击tab的时候，RecyclerView自动滑到该tab对应的item位置
                int position = tab.getPosition();
                if (!isScroll) {
                    // 不会滚动到顶部，出现了就不滚动
//                  mRecyclerView.smoothScrollToPosition(position);
                    // 没有动画
//                    mLinearLayoutManager.scrollToPositionWithOffset(position, 0);
                    // 有动画且滚动到顶部
                   /* mSmoothScroller.setTargetPosition(position);
                    mLinearLayoutManager.startSmoothScroll(mSmoothScroller);*/
                    mRecyclerView.smoothScrollToPosition(position);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private void initRecyclerView() {
        mSmoothScroller = new LinearSmoothScroller(this) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            @Nullable
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return mLinearLayoutManager.computeScrollVectorForPosition(targetPosition);
            }
        };

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isScroll = false;
                } else {
                    isScroll = true;
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //滑动RecyclerView list的时候，根据最上面一个Item的position来切换tab
//                mTabLayout.setScrollPosition(mLinearLayoutManager.findFirstVisibleItemPosition(), 0, true);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                TabLayout.Tab tabAt = mTabLayout.getTabAt(layoutManager.findFirstCompletelyVisibleItemPosition());
                if (tabAt != null && !tabAt.isSelected()) {
                    tabAt.select();
                }
            }
        });
    }
    class MyAdapter extends RecyclerView.Adapter {

        public static final int VIEW_TYPE_ITEM = 1;
        public static final int VIEW_TYPE_FOOTER = 2;
        protected List<Item> mData;
        /**
         * 复用同一个View对象池
         */
        private RecyclerView.RecycledViewPool mRecycledViewPool;
        /**
         * item高度
         */
        private int itemHeight;


        public MyAdapter(@Nullable ArrayList<Item> data) {
            mData = data;
            mRecycledViewPool = new RecyclerView.RecycledViewPool();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.demo_item, parent, false);
                return new MyAdapter.ItemViewHolder(view);
            } else {
                //Footer是最后留白的位置，以便最后一个item能够出发tab的切换
                View view = new View(parent.getContext());
                Log.e("footer", "parentHeight: " + mRecyclerViewHeight + "--" + "itemHeight: " + itemHeight);
                view.setLayoutParams(
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                mRecyclerViewHeight - itemHeight));
                return new MyAdapter.FooterViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
                final MyAdapter.ItemViewHolder itemViewHolder = (MyAdapter.ItemViewHolder) holder;
                itemViewHolder.mTitle.setText(mData.get(position).name);
                RecyclerView recyclerView = itemViewHolder.mRecyclerView;
                recyclerView.setRecycledViewPool(mRecycledViewPool);
                recyclerView.setHasFixedSize(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()) {
                    @Override
                    public boolean canScrollVertically() {
                        //保证嵌套滚动不冲突
                        return false;
                    }

                    @Override
                    public void onLayoutCompleted(RecyclerView.State state) {
                        super.onLayoutCompleted(state);
                        mRecyclerViewHeight = mRecyclerView.getHeight();
                        itemHeight = itemViewHolder.itemView.getHeight();
                        Log.e("onLayoutCompleted",
                                "itemHeight: " + itemHeight + "--" + position);
                    }
                });
                DemoAdapter demoAdapter = new DemoAdapter(mData.get(position).mSubItems);
                recyclerView.setAdapter(demoAdapter);

            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == mData.size()) {
                return VIEW_TYPE_FOOTER;
            } else {
                return VIEW_TYPE_ITEM;
            }
        }

        @Override
        public int getItemCount() {
            return mData.size() + 1;
        }

        class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {

            private TextView mTitle;
            private RecyclerView mRecyclerView;

            public ItemViewHolder(View itemView) {
                super(itemView);
                mTitle = (TextView) itemView.findViewById(R.id.demo_item_text);
                mRecyclerView = (RecyclerView) itemView.findViewById(R.id.demo_item_recycler_view);
            }
        }
    }
}