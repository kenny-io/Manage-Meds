package com.example.ekene.managemeds.ui.main.ui;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ekene.managemeds.AuthActivity;
import com.example.ekene.managemeds.data.model.Medicine;
import com.example.ekene.managemeds.data.model.MenuView;
import com.example.ekene.managemeds.data.model.User;
import com.example.ekene.managemeds.databinding.ActivityMainBinding;
import com.example.ekene.managemeds.settings.Settings;
import com.example.ekene.managemeds.ui.main.adapter.MainDashboardAdapter;
import com.example.ekene.managemeds.ui.main.viewmodel.MainViewModel;
import com.example.ekene.managemeds.ui.medicine.adapter.DailyMedicineAdapter;
import com.example.ekene.managemeds.ui.medicine.adapter.DailyMedicineStatisticsAdapter;
import com.example.ekene.managemeds.ui.medicine.ui.AddMedicineActivity;
import com.example.ekene.managemeds.ui.medicine.ui.MedicineActivity;
import com.example.ekene.managemeds.ui.medicine.ui.MonthlyIntakeActivity;
import com.example.ekene.managemeds.ui.medicine.ui.SearchMedsActivity;
import com.example.ekene.managemeds.ui.medicine.viewmodel.MedicineViewModel;
import com.example.ekene.managemeds.util.CirclePagerIndicatorDecoration;
import com.example.ekene.managemeds.util.ItemOffsetDecoration;
import com.example.ekene.managemeds.view.ProfileDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tingyik90.snackprogressbar.SnackProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBarManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.ekene.managemeds.R;
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int NOTIFICATION_ID = 0;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.recyclerViewDailyMedicineStatistics)
    RecyclerView recyclerViewDailyMedicineStatistics;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.recyclerViewDailyMedicine)
    RecyclerView recyclerViewDailyMedicine;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.text_empty)
    TextView emptyText;
    @BindView(R.id.layout_empty)
    FrameLayout emptyFrame;
    @BindView(R.id.cardMedDaily)
    CardView cardMedDaily;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    User user;
    AlarmManager alarmManager;
    private ProfileDialog profileDialog;
    private SnackProgressBarManager snackProgressBarManager;
    private GoogleApiClient mGoogleApiClient;
    private List<Medicine> medicineList;
    private List<MenuView> menuViewList;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding bindings = DataBindingUtil.setContentView(this, R.layout.activity_main);


        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        user = mainViewModel.getUserLiveData();
        bindings.setUser(user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In Api and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        init();

        profileDialog = ProfileDialog.newInstance(((dialog, which) -> logout()));

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");
        String currentDate = simpleDateFormat.format(calendar.getTime());
        date.setText(currentDate);

        //Initialize Snackbar Manager -> Attach/pin to the bottom of the layout :)
        snackProgressBarManager = new SnackProgressBarManager(coordinatorLayout)
                .setProgressBarColor(R.color.colorAccent)
                .setOverlayLayoutAlpha(0.6f);

        menuViewList = getMenuOptions();

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        MainDashboardAdapter mainDashboardAdapter = new MainDashboardAdapter(this, menuViewList, (v, position) -> {
            MenuView role = menuViewList.get(position);
            String menuName = role.getName();
            switch (menuName) {
                case "Add Meds":
                    Intent addMedicine = new Intent(getApplicationContext(), AddMedicineActivity.class);
                    startActivity(addMedicine);
                    break;
                case "Search Meds":
                    Intent searchMeds = new Intent(getApplicationContext(), SearchMedsActivity.class);
                    startActivity(searchMeds);
                    break;
                case "Monthly Intake":
                    Intent monthlyIntake = new Intent(getApplicationContext(), MonthlyIntakeActivity.class);
                    startActivity(monthlyIntake);
                    break;
                default:
                    Toast.makeText(MainActivity.this, "Sorry, dev still in progress", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        recyclerView.setAdapter(mainDashboardAdapter);

        MedicineViewModel medicineViewModel = ViewModelProviders.of(this).get(MedicineViewModel.class);
        medicineViewModel.getMedicineList().observe(this, medicines -> {
            if (MainActivity.this.medicineList == null) {
                setListData(medicines);
            }
        });
    }

    public void setListData(final List<Medicine> medicineList) {
        this.medicineList = medicineList;
        if (medicineList.isEmpty()) {
            emptyFrame.setVisibility(View.VISIBLE);
        } else {

            cardMedDaily.setVisibility(View.VISIBLE);

            DailyMedicineStatisticsAdapter dailyMedicineStatisticsAdapter = new DailyMedicineStatisticsAdapter(this, medicineList, (v, position) -> {
                Medicine medicine = medicineList.get(position);
                Intent intent = new Intent(getApplicationContext(), MedicineActivity.class);
                Bundle b = new Bundle();

                b.putString("name", medicine.getName());
                b.putString("description", medicine.getDescription());
                b.putString("interval", medicine.getInterval());
                b.putString("pills", medicine.getPills());
                b.putString("pillsTaken", medicine.getPillsTaken());
                b.putBoolean("true", medicine.isHasNotification());
                b.putString("startDate", String.valueOf(medicine.getStartDate()));
                b.putString("endDate", String.valueOf(medicine.getEndDate()));
                b.putInt("days", medicine.getDays());
                intent.putExtras(b);
                startActivity(intent);
            });

            recyclerViewDailyMedicineStatistics.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false));
            // add pager behavior
            PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerViewDailyMedicineStatistics);
            // pager indicator
            recyclerViewDailyMedicineStatistics.addItemDecoration(new CirclePagerIndicatorDecoration());
            recyclerViewDailyMedicineStatistics.setAdapter(dailyMedicineStatisticsAdapter);

            DailyMedicineAdapter dailyMedicineAdapter = new DailyMedicineAdapter(this, medicineList, (v, position) -> {
                Medicine medicine = medicineList.get(position);
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), MedicineActivity.class);
                Bundle b = new Bundle();

                b.putString("name", medicine.getName());
                b.putString("description", medicine.getDescription());
                b.putString("interval", medicine.getInterval());
                b.putString("pills", medicine.getPills());
                b.putString("pillsTaken", medicine.getPillsTaken());
                b.putBoolean("true", medicine.isHasNotification());
                b.putString("startDate", String.valueOf(medicine.getStartDate()));
                b.putString("endDate", String.valueOf(medicine.getEndDate()));
                b.putInt("days", medicine.getDays());
                intent.putExtras(b);
                MainActivity.this.startActivity(intent);
            });

            recyclerViewDailyMedicine.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false));
            recyclerViewDailyMedicine.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL));
            recyclerViewDailyMedicine.setAdapter(dailyMedicineAdapter);
        }
    }

    private void init() {
        emptyText.setText(Html.fromHtml(getString(R.string.text_empty_message)));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem profileItem = menu.findItem(R.id.action_profile);
        Glide.with(this)
                .asBitmap()
                .load(user.getImageUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(new SimpleTarget<Bitmap>(100, 100) {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        profileItem.setIcon(new BitmapDrawable(getResources(), resource));
                    }
                });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_profile) {
            profileDialog.show(getSupportFragmentManager(), "profile");
            return true;
        } else if (id == R.id.action_settings) {
            // start settings Activity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Mock Data
    private List<MenuView> getMenuOptions() {
        List<MenuView> listViewItems = new ArrayList<>();
        listViewItems.add(new MenuView(1, "Add Meds", R.drawable.ic_add_medicine));
        listViewItems.add(new MenuView(2, "Monthly Intake", R.drawable.ic_monthly_intake));
        listViewItems.add(new MenuView(3, "Search Meds", R.drawable.ic_search));
        return listViewItems;
    }

    private void logout() {
        if (!Settings.isLoggedIn()) {
            return;
        }

        SnackProgressBar snackProgressBar = new SnackProgressBar(
                SnackProgressBar.TYPE_INDETERMINATE,
                "Logging Out...")
                .setSwipeToDismiss(false);

        // Show snack progress during logout
        snackProgressBarManager.dismissAll();
        snackProgressBarManager.show(snackProgressBar, SnackProgressBarManager.LENGTH_INDEFINITE);

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {

            //Clear Shared Pref File
            Settings.setLoggedInSharedPref(false);

            //Clear Local DB

            //Redirect User to Login Page
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            startActivity(intent);
            finish();
        });

        snackProgressBarManager.dismiss();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
