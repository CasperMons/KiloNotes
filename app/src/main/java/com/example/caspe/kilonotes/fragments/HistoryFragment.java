package com.example.caspe.kilonotes.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caspe.kilonotes.R;
import com.example.caspe.kilonotes.adapters.RidesAdapter;
import com.example.caspe.kilonotes.model.Ride;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HistoryFragment extends Fragment {

    ListView historyList;
    SwipeRefreshLayout swipeRefreshHistory;
    Button btnFilterDateStart;
    Button btnFilterDateEnd;
    Button btnFilterName;
    DatePickerDialog.OnDateSetListener filterDateStartListener;
    DatePickerDialog.OnDateSetListener filterDateEndListener;
    long filterStartDate;
    long filterEndDate;
    String filterName;
    ImageButton btnClearFilter;
    TextView txtFilterPrice;
    LinearLayout layoutCalcPrice;
    FirebaseUser currentUser;

    final FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        declareLayoutElements(view);
        clearFilters();
        getHistoryRides();

        swipeRefreshHistory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), R.string.toast_refresh_history, Toast.LENGTH_SHORT).show();
                clearFilters();
                getHistoryRides();
            }
        });

        btnFilterDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        R.style.AppTheme,
                        filterDateStartListener,
                        year, month, day);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
            }
        });

        btnFilterDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        R.style.AppTheme,
                        filterDateEndListener,
                        year, month, day);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
            }
        });

        filterDateStartListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                btnFilterDateStart.setText(day + "-" + (month + 1) + "-" + year);
                Calendar startDate = Calendar.getInstance();
                startDate.set(year, month, day, 0, 0);
                filterStartDate = startDate.getTime().getTime();
                if (filterEndDate != 0) {
                    getHistoryByFilter();
                }
            }
        };

        filterDateEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                btnFilterDateEnd.setText(day + "-" + (month + 1) + "-" + year);
                Calendar endDate = Calendar.getInstance();
                endDate.set(year, month, day, 23, 59, 59);
                filterEndDate = endDate.getTime().getTime();
                if (filterStartDate != 0) {
                    getHistoryByFilter();
                }
            }
        };

        btnFilterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder nameInputDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.alert_title_input_name)
                        .setMessage(R.string.alert_message_input_name)
                        .setIcon(R.drawable.kilo_note_logo);
                final EditText input = new EditText(getContext());
                input.setHint(R.string.edit_nick_name);

                nameInputDialog.setView(input);

                nameInputDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        filterName = input.getText().toString();
                        if (!filterName.equals("")) {
                            btnFilterName.setText(filterName);
                            getHistoryByFilter();
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                        }
                    }
                });

                nameInputDialog.setNegativeButton(R.string.btn_txt_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                nameInputDialog.show();
            }
        });

        btnClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFilters();
            }
        });

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Ride clickedRide = (Ride) parent.getItemAtPosition(position);
                if (clickedRide.userName.equals(Ride.UNREGISTERED)) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.alert_title_claim_ride)
                            .setMessage(R.string.alert_message_claim_ride)
                            .setIcon(R.drawable.kilo_note_logo)
                            .setPositiveButton(R.string.alert_confirm_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    claimRide(clickedRide);
                                }
                            }).setNegativeButton(R.string.btn_no, null).show();
                }
            }
        });

        return view;
    }

    public void declareLayoutElements(View view) {
        historyList = (ListView) view.findViewById(R.id.history_list);
        swipeRefreshHistory = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        btnFilterDateStart = (Button) view.findViewById(R.id.filter_date_start);
        btnFilterDateEnd = (Button) view.findViewById(R.id.filter_date_end);
        btnFilterName = (Button) view.findViewById(R.id.filter_name);
        btnClearFilter = (ImageButton) view.findViewById(R.id.btn_clear_filter);
        txtFilterPrice = (TextView) view.findViewById(R.id.price_of_filter);
        layoutCalcPrice = (LinearLayout) view.findViewById(R.id.price_filter_layout);
    }

    public void clearFilters() {
        filterStartDate = 0;
        filterEndDate = 0;
        filterName = "";
        btnFilterDateStart.setText(R.string.btn_filter_date_start);
        btnFilterDateEnd.setText(R.string.btn_filter_date_end);
        btnFilterName.setText(R.string.btn_filter_name);
        txtFilterPrice.setText("");

        ViewGroup.LayoutParams params = layoutCalcPrice.getLayoutParams();
        params.height = 0;
        layoutCalcPrice.setLayoutParams(params);

        getHistoryRides();
    }

    public void getHistoryRides() {
        swipeRefreshHistory.setRefreshing(true);
        DatabaseReference ref = fbDatabase.getReference("Rides");
        ref.limitToLast(100).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Ride> lstRidesFromDb = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Ride newRide = ds.getValue(Ride.class);
                    lstRidesFromDb.add(newRide);
                }
                swipeRefreshHistory.setRefreshing(false);
                updateHistoryListView(lstRidesFromDb);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DBERROR", databaseError.getMessage());
            }
        });
    }

    public void getHistoryByFilter() {
        swipeRefreshHistory.setRefreshing(true);
        DatabaseReference dbRef = fbDatabase.getReference("Rides");
        Query query = dbRef;

        if (filterStartDate > 0 && filterEndDate > 0) {
            query = dbRef.orderByChild("timestamp").startAt(filterStartDate).endAt(filterEndDate);
        } else if (!filterName.equals("")) {
            query = dbRef.orderByChild("userName").equalTo(filterName);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Ride> filteredRides = new ArrayList<>();
                for (DataSnapshot rideFromDb : dataSnapshot.getChildren()) {
                    if (!filterName.equals("")) {
                        if (filterName.equals(rideFromDb.getValue(Ride.class).userName)) {
                            filteredRides.add(rideFromDb.getValue(Ride.class));
                        }
                    } else {
                        filteredRides.add(rideFromDb.getValue(Ride.class));
                    }
                }
                if (!filterName.equals("") && filterStartDate > 0 && filterEndDate > 0) {
                    setFee(filteredRides);
                }
                swipeRefreshHistory.setRefreshing(false);
                updateHistoryListView(filteredRides);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DBERROR", databaseError.getMessage());
            }
        });
    }

    public void updateHistoryListView(ArrayList<Ride> lstRides) {
        if (lstRides.size() > 0) {
            Collections.sort(lstRides, Collections.reverseOrder());
            RidesAdapter ridesAdapter = new RidesAdapter(getContext(), lstRides);
            historyList.setAdapter(ridesAdapter);
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.alert_title_filter_null)
                    .setMessage(R.string.alert_message_filter_null)
                    .setIcon(R.drawable.kilo_note_logo_red)
                    .show();
        }
    }

    private void setFee(ArrayList<Ride> lstRides) {
        double price;
        long drivenKm = 0;
        for (Ride ride : lstRides) {
            drivenKm = drivenKm + (ride.endDistance - ride.startDistance);
        }
        price = drivenKm * Ride.RIDE_PRICE;

        txtFilterPrice.setText("€" + Ride.priceFormat.format(price));
        ViewGroup.LayoutParams params = layoutCalcPrice.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutCalcPrice.setLayoutParams(params);
    }

    private void claimRide(final Ride rideToClaim) {

        final DatabaseReference ref = fbDatabase.getReference("Rides");
        ref.orderByChild("timestamp").equalTo(rideToClaim.timestamp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = "";

                rideToClaim.userName = currentUser.getDisplayName();
                rideToClaim.userId = currentUser.getUid();

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    key = snap.getKey();
                }
                if (!key.equals("")) {
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(key, rideToClaim);
                    ref.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                new AlertDialog.Builder(getContext())
                                        .setTitle(R.string.alert_title_claim_ride)
                                        .setMessage(R.string.alert_message_claimed)
                                        .setIcon(R.drawable.kilo_note_logo_green).show();
                                getHistoryRides();

                            } else {
                                new AlertDialog.Builder(getContext())
                                        .setTitle(R.string.alert_title_claim_ride)
                                        .setMessage(R.string.alert_message_claim_failed)
                                        .setIcon(R.drawable.kilo_note_logo_red).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}