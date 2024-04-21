package uqac.dim.paldle;

import static android.view.Gravity.CENTER;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import uqac.dim.pallll.CustomAdapter;
import uqac.dim.pallll.Pal;
import uqac.dim.pallll.PalOfTheDayManager;

public class MainActivity extends AppCompatActivity {

    private final DatabaseReference palsReference = FirebaseDatabase.getInstance().getReference("pals");
    private int day;
    private int id; // guess game
    private int id2; // description game
    private int id3; // silouhette game

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (PalOfTheDayManager.checkLastDate(this)) {
            id = getRandomId();
            PalOfTheDayManager.setLastPalId(this, id);
            PalOfTheDayManager.setWin(this, false);
            id2 = getRandomId();
            PalOfTheDayManager.setLastPalId2(this, id2);
            PalOfTheDayManager.setWin2(this, false);
            id3 = getRandomId();
            PalOfTheDayManager.setLastPalId3(this, id3);
            PalOfTheDayManager.setWin3(this, false);
            PalOfTheDayManager.setLastDate(this);
        } else {
            id = PalOfTheDayManager.getLastPalId(this);
            id2 = PalOfTheDayManager.getLastPalId2(this);
            id3 = PalOfTheDayManager.getLastPalId3(this);
        }

        Log.v("FERUUU", "id1 : " + String.valueOf(PalOfTheDayManager.getLastPalId(this)));
        Log.v("FERUUU", "id2 : " + String.valueOf(PalOfTheDayManager.getLastPalId2(this)));
        Log.v("FERUUU", "id3 : " + String.valueOf(PalOfTheDayManager.getLastPalId3(this)));

        palsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot palSnapshot : dataSnapshot.getChildren()) {
                    if (palSnapshot.getKey() != null && palSnapshot.getKey().equals(String.valueOf(id))) {
                        Pal pal = palSnapshot.getValue(Pal.class);
                        Log.v("FERUUU", "pal1 : " + pal.getName());
                    } else if (palSnapshot.getKey() != null && palSnapshot.getKey().equals(String.valueOf(id2))) {
                        Pal pal = palSnapshot.getValue(Pal.class);
                        Log.v("FERUUU", "pal2 : " + pal.getName());
                    } else if (palSnapshot.getKey() != null && palSnapshot.getKey().equals(String.valueOf(id3))) {
                        Pal pal = palSnapshot.getValue(Pal.class);
                        Log.v("FERUUU", "pal3 : " + pal.getName());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read value.", databaseError.toException());
            }
        });



        FragHome fragHome = new FragHome(palsReference);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frag, fragHome, "home_fragment")
                .addToBackStack(null)
                .commit();

        FirebaseApp.initializeApp(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.homeMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                FragmentManager fragmentManager = getSupportFragmentManager();

                Fragment fragment = null;
                if (id == R.id.paldexMenu) {
                    fragment = fragmentManager.findFragmentByTag("paldex_fragment");
                    if (fragment == null) {
                        fragment = new FragPaldex(palsReference);
                    }
                } else if (id == R.id.guessMenu) {
                    fragment = fragmentManager.findFragmentByTag("guess_fragment");
                    if (fragment == null) {
                        fragment = new FragGuess(palsReference, PalOfTheDayManager.getLastPalId(MainActivity.this));
                    }
                } else if (id == R.id.descMenu) {
                    fragment = fragmentManager.findFragmentByTag("desc_fragment");
                    if (fragment == null) {
                        fragment = new FragDesc(palsReference, PalOfTheDayManager.getLastPalId2(MainActivity.this));
                    }
                } else if (id == R.id.silMenu) {
                    fragment = fragmentManager.findFragmentByTag("silhouette_fragment");
                    if (fragment == null) {
                        fragment = new FragSil(palsReference, PalOfTheDayManager.getLastPalId3(MainActivity.this));
                    }
                } else if (id == R.id.homeMenu) {
                    fragment = fragmentManager.findFragmentByTag("home_fragment");
                    if (fragment == null) {
                        fragment = new FragHome(palsReference);
                    }
                }

                Fragment currentFragment = fragmentManager.findFragmentById(R.id.frag);
                if (currentFragment != null && fragment != null && currentFragment.getClass().equals(fragment.getClass())) {
                    // Fragment is already displayed, no need to switch
                    return true;
                }

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if (fragment != null) {
                    if (id == R.id.paldexMenu){
                        transaction.replace(R.id.frag, fragment, "paldex_fragment");
                    } else if (id == R.id.guessMenu){
                        transaction.replace(R.id.frag, fragment, "guess_fragment");
                    } if (id == R.id.descMenu){
                        transaction.replace(R.id.frag, fragment, "desc_fragment");
                    } if (id == R.id.silMenu){
                        transaction.replace(R.id.frag, fragment, "silhouette_fragment");
                    } if (id == R.id.homeMenu){
                        transaction.replace(R.id.frag, fragment, "home_fragment");
                    }
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                }

                return false;
            }
        });


    }

    public static int getRandomId() {
        Random random = new Random();
        return random.nextInt(137);
    }


    public static class FragGuess extends Fragment {

        private int id;
        private Pal palOfTheDay;
        private List<Pal> palList;
        private List<Pal> palsGuessed = null;
        private final DatabaseReference palsReference;

        public FragGuess(DatabaseReference palsReference, int id) {
            this.palsReference = palsReference;
            this.id = id;
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.v("FOUFOU", "pause");
            for (Pal pal : palsGuessed ) {
                Log.v("FOUFOU", "pal : " + pal.getName());
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.v("FOUFOU", "resume");
            TableLayout tableLayout = requireView().findViewById(R.id.tableLayout);
            for (Pal pal : palsGuessed ){
                Log.v("FOUFOU", "pal : " + pal.getName());
                TableRow tableRow = new TableRow(this.getContext());
                tableRow.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border));
                TextView textName = new TextView(this.getContext());
                textName.setGravity(CENTER);
                textName.setPadding(8, 16, 8, 16);
                tableRow.addView(textName);
                TextView textId = new TextView(this.getContext());
                textId.setGravity(CENTER);
                textId.setPadding(8, 16, 8, 16);
                tableRow.addView(textId);
                TextView textTypes = new TextView(this.getContext());
                textTypes.setGravity(CENTER);
                textTypes.setPadding(8, 16, 8, 16);
                tableRow.addView(textTypes);
                TextView textSize = new TextView(this.getContext());
                textSize.setGravity(CENTER);
                textSize.setPadding(8, 16, 8, 16);
                tableRow.addView(textSize);
                tableLayout.addView(tableRow);

                textName.setText(pal.getName());
                if (pal.getName().equals(palOfTheDay.getName())) {
                    textName.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    textName.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                }

                textId.setText(String.valueOf(pal.getKey()));
                String key = pal.getKey();

                if (key.equals(palOfTheDay.getKey())) {
                    textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    if (key.substring(0, 3).equals(palOfTheDay.getKey().substring(0, 3))) {
                        textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                }

                String types = "";
                for (String type : pal.getTypes()) {
                    types += type + " ";
                }
                textTypes.setText(types);
                List<String> guessTypes = pal.getTypes();
                List<String> palTypes = palOfTheDay.getTypes();

                if (guessTypes.size() == 2 && palTypes.size() == 2) {
                    if ((Objects.equals(guessTypes.get(0), palTypes.get(0)) && Objects.equals(guessTypes.get(1), palTypes.get(1)) || (Objects.equals(guessTypes.get(0), palTypes.get(1)) && Objects.equals(guessTypes.get(1), palTypes.get(0))))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    } else if (Objects.equals(guessTypes.get(0), palTypes.get(0)) || Objects.equals(guessTypes.get(1), palTypes.get(1)) || Objects.equals(guessTypes.get(0), palTypes.get(1)) || Objects.equals(guessTypes.get(1), palTypes.get(0))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                } else if (guessTypes.size() == 1 && palTypes.size() == 1) {
                    if (Objects.equals(guessTypes.get(0), palTypes.get(0))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                } else {
                    boolean found = false;
                    for (String type : guessTypes) {
                        if (palTypes.contains(type)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                }
                textSize.setText(String.valueOf(pal.getSize()));
                if (Objects.equals(pal.getSize(), palOfTheDay.getSize())) {
                    textSize.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    textSize.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.frag_guess, container, false);

            palList = new ArrayList<>();

            if (palsGuessed == null){
                palsGuessed = new ArrayList<>();
            }

            FirebaseStorage storage = FirebaseStorage.getInstance();


            if (!PalOfTheDayManager.getWin(requireContext())) {
                palsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot palSnapshot : dataSnapshot.getChildren()) {
                            String palId = palSnapshot.getKey();
                            Pal pal = palSnapshot.getValue(Pal.class);
                            if (palId != null && palId.equals(String.valueOf(id))) {
                                palOfTheDay = pal;
                            }
                            palList.add(pal);
                        }

                        AutoCompleteTextView et = view.findViewById(R.id.et);
                        List<String> palName = new ArrayList<String>();
                        for (Pal pal : palList) {
                            palName.add(pal.getName());
                        }
                        CustomAdapter adapter = new CustomAdapter(requireContext(), palList, palName);
                        et.setAdapter(adapter);

                        if (palOfTheDay != null) {
                            Log.v("FERU", palOfTheDay.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to read value.", databaseError.toException());
                    }
                });

                Button btnGuess = view.findViewById(R.id.btnGuess);
                btnGuess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guessClicked(view);
                    }
                });

                AutoCompleteTextView et = view.findViewById(R.id.et);
                et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            et.showDropDown();
                        }
                    }
                });

                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        et.showDropDown();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String input = s.toString().toLowerCase();
                        List<Pal> filteredPals = new ArrayList<>();
                        List<String> filteredPalsName = new ArrayList<>();

                        for (Pal pal : palList) {
                            if (pal.getName().toLowerCase().startsWith(input)) {
                                filteredPals.add(pal);
                                filteredPalsName.add(pal.getName());
                            }
                        }
                        CustomAdapter filteredAdapter = new CustomAdapter(requireContext(), filteredPals, filteredPalsName);
                        et.setAdapter(filteredAdapter);
                        et.showDropDown();
                    }
                });
            } else {
                id = PalOfTheDayManager.getLastPalId(requireContext());
                Log.v("FERUU", String.valueOf(id));

                palsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot palSnapshot : dataSnapshot.getChildren()) {
                            String palId = palSnapshot.getKey();
                            Pal pal = palSnapshot.getValue(Pal.class);
                            if (palId != null && palId.equals(String.valueOf(id))) {
                                palOfTheDay = pal;
                            }
                        }
                        if (palOfTheDay != null) {
                            Log.v("FERU", palOfTheDay.getName());
                        }

                        AutoCompleteTextView et = view.findViewById(R.id.et);
                        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
                        LinearLayout llp = view.findViewById(R.id.llParent);
                        LinearLayout ll = view.findViewById(R.id.llChild);
                        llp.removeView(tableLayout);
                        ll.removeView(et);
                        Button btnGuess = view.findViewById(R.id.btnGuess);
                        ll.removeView(btnGuess);
                        TextView ttv = new TextView(requireContext());
                        ttv.setText("Pal of the day already found !\nIt was :");
                        ttv.setPadding(0, 200, 0, 0);
                        ttv.setGravity(CENTER);
                        ttv.setTextAppearance(R.style.PalNameStyle);
                        ll.addView(ttv);
                        ImageView img = new ImageView(requireContext());
                        img.setPadding(10, 10, 10, 10);
                        img.setContentDescription("palOfTheDay");
                        Picasso.get()
                                .load(palOfTheDay.getImage())
                                .resize(750, 750)
                                .centerInside()
                                .into(img);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.gravity = Gravity.CENTER;
                        img.setLayoutParams(layoutParams);
                        img.setBackgroundResource(R.drawable.border);
                        ll.addView(img);
                        TextView tv = new TextView(requireContext());
                        tv.setPadding(0, 50, 0, 100);
                        tv.setText(palOfTheDay.getName());
                        tv.setGravity(CENTER);
                        tv.setTextAppearance(R.style.PalNameStyle);
                        ll.addView(tv);

                        LinearLayout lol = new LinearLayout(requireContext());
                        lol.setOrientation(LinearLayout.HORIZONTAL);
                        lol.setGravity(Gravity.CENTER);
                        Button btnNextGame = (Button) getLayoutInflater().inflate(R.layout.btn_share, null);
                        if (PalOfTheDayManager.getWin2(requireContext()) && PalOfTheDayManager.getWin3(requireContext())) {
                            btnNextGame.setText("Home");
                        } else {
                            btnNextGame.setText("Next Game");
                        }
                        Button btnShare = (Button) getLayoutInflater().inflate(R.layout.btn_share, null);

                        btnNextGame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();

                                if (!PalOfTheDayManager.getWin2(requireContext())) {
                                    bottomNavigationView.setSelectedItemId(R.id.descMenu);
                                    transaction.replace(R.id.frag, new FragDesc(palsReference, PalOfTheDayManager.getLastPalId2(requireContext())), "desc_fragment");
                                } else if (!PalOfTheDayManager.getWin3(requireContext())) {
                                    bottomNavigationView.setSelectedItemId(R.id.silMenu);
                                    transaction.replace(R.id.frag, new FragSil(palsReference, PalOfTheDayManager.getLastPalId3(requireContext())), "silhouette_fragment");
                                } else {
                                    bottomNavigationView.setSelectedItemId(R.id.homeMenu);
                                    transaction.replace(R.id.frag, new FragHome(palsReference), "home_fragment");
                                }

                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        });
                        btnShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "I found my Pal of the Day : " + palOfTheDay.getName() + " !\nGo find yours on Paldle !");
                                sendIntent.setType("text/plain");

                                Intent shareIntent = Intent.createChooser(sendIntent, null);
                                startActivity(shareIntent);
                            }
                        });
                        lol.addView(btnShare);
                        lol.addView(btnNextGame);
                        llp.addView(lol);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to read value.", databaseError.toException());
                    }
                });
            }
            return view;
        }


        public void guessClicked(View view) {
            AutoCompleteTextView et = view.findViewById(R.id.et);
            String guess = et.getText().toString().toLowerCase();
            boolean win = false;

            Pal guessPal = null;

            for (Pal pal : palList) {
                if (pal.getName().toLowerCase().equals(guess)) {
                    for (Pal palGuessed : palsGuessed) {
                        if (palGuessed.getName().equals(pal.getName())) {
                            et.setText("");
                            et.clearFocus();
                            return;
                        }
                    }
                    guessPal = pal;
                    break;
                }
            }

            if (guessPal != null) {
                palsGuessed.add(guessPal);
                TableLayout tableLayout = view.findViewById(R.id.tableLayout);
                TableRow tableRow = new TableRow(this.getContext());
                tableRow.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border));
                TextView textName = new TextView(this.getContext());
                textName.setGravity(CENTER);
                textName.setPadding(8, 16, 8, 16);
                tableRow.addView(textName);
                TextView textId = new TextView(this.getContext());
                textId.setGravity(CENTER);
                textId.setPadding(8, 16, 8, 16);
                tableRow.addView(textId);
                TextView textTypes = new TextView(this.getContext());
                textTypes.setGravity(CENTER);
                textTypes.setPadding(8, 16, 8, 16);
                tableRow.addView(textTypes);
                TextView textSize = new TextView(this.getContext());
                textSize.setGravity(CENTER);
                textSize.setPadding(8, 16, 8, 16);
                tableRow.addView(textSize);
                tableLayout.addView(tableRow);

                textName.setText(guessPal.getName());
                if (guessPal.getName().equals(palOfTheDay.getName())) {
                    textName.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    win = true;
                } else {
                    textName.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                }

                textId.setText(String.valueOf(guessPal.getKey()));
                String key = guessPal.getKey();

                if (key.equals(palOfTheDay.getKey())) {
                    textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    if (key.substring(0, 3).equals(palOfTheDay.getKey().substring(0, 3))) {
                        textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                }

                String types = "";
                for (String type : guessPal.getTypes()) {
                    types += type + " ";
                }
                textTypes.setText(types);
                List<String> guessTypes = guessPal.getTypes();
                List<String> palTypes = palOfTheDay.getTypes();

                if (guessTypes.size() == 2 && palTypes.size() == 2) {
                    if ((Objects.equals(guessTypes.get(0), palTypes.get(0)) && Objects.equals(guessTypes.get(1), palTypes.get(1)) || (Objects.equals(guessTypes.get(0), palTypes.get(1)) && Objects.equals(guessTypes.get(1), palTypes.get(0))))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    } else if (Objects.equals(guessTypes.get(0), palTypes.get(0)) || Objects.equals(guessTypes.get(1), palTypes.get(1)) || Objects.equals(guessTypes.get(0), palTypes.get(1)) || Objects.equals(guessTypes.get(1), palTypes.get(0))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                } else if (guessTypes.size() == 1 && palTypes.size() == 1) {
                    if (Objects.equals(guessTypes.get(0), palTypes.get(0))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                } else {
                    boolean found = false;
                    for (String type : guessTypes) {
                        if (palTypes.contains(type)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                }

                textSize.setText(String.valueOf(guessPal.getSize()));
                if (Objects.equals(guessPal.getSize(), palOfTheDay.getSize())) {
                    textSize.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    textSize.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                }

                if (win) {
                    PalOfTheDayManager.setWin(requireContext(), true);

                    LinearLayout llp = view.findViewById(R.id.llParent);
                    LinearLayout ll = view.findViewById(R.id.llChild);
                    ll.removeView(et);
                    Button btnGuess = view.findViewById(R.id.btnGuess);
                    ll.removeView(btnGuess);
                    ImageView img = new ImageView(this.getContext());
                    img.setPadding(10, 10, 10, 10);
                    img.setContentDescription("palOfTheDay");
                    Picasso.get()
                            .load(palOfTheDay.getImage())
                            .resize(450, 450)
                            .centerInside()
                            .into(img);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.gravity = Gravity.CENTER;
                    img.setLayoutParams(layoutParams);
                    img.setBackgroundResource(R.drawable.border);
                    ll.addView(img);
                    TextView tv = new TextView(this.getContext());
                    tv.setText(palOfTheDay.getName());
                    tv.setGravity(CENTER);
                    tv.setTextAppearance(R.style.PalNameStyle);
                    ll.addView(tv);

                    LinearLayout lol = new LinearLayout(requireContext());
                    lol.setOrientation(LinearLayout.HORIZONTAL);
                    lol.setGravity(Gravity.CENTER);
                    Button btnNextGame = (Button) getLayoutInflater().inflate(R.layout.btn_share, null);
                    if (PalOfTheDayManager.getWin2(requireContext()) && PalOfTheDayManager.getWin3(requireContext())) {
                        btnNextGame.setText("Home");
                    } else {
                        btnNextGame.setText("Next Game");
                    }
                    Button btnShare = (Button) getLayoutInflater().inflate(R.layout.btn_share, null);

                    btnNextGame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();

                            if (!PalOfTheDayManager.getWin2(requireContext())) {
                                bottomNavigationView.setSelectedItemId(R.id.descMenu);
                                transaction.replace(R.id.frag, new FragDesc(palsReference, PalOfTheDayManager.getLastPalId2(requireContext())), "desc_fragment");
                            } else if (!PalOfTheDayManager.getWin3(requireContext())) {
                                bottomNavigationView.setSelectedItemId(R.id.silMenu);
                                transaction.replace(R.id.frag, new FragSil(palsReference, PalOfTheDayManager.getLastPalId3(requireContext())), "silhouette_fragment");
                            } else {
                                bottomNavigationView.setSelectedItemId(R.id.homeMenu);
                                transaction.replace(R.id.frag, new FragHome(palsReference), "home_fragment");
                            }

                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });
                    btnShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "I found my Pal of the Day : " + palOfTheDay.getName() + " !\nGo find yours on Paldle !");
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            startActivity(shareIntent);
                        }
                    });
                    lol.addView(btnShare);
                    lol.addView(btnNextGame);
                    llp.addView(lol);
                } else {
                    et.setText("");
                    et.clearFocus();
                }
            } else {
                return;
            }
        }
    }

    public static class FragDesc extends Fragment {

        private int id;
        private Pal palOfTheDay;
        private List<Pal> palList;
        private List<Pal> palsGuessed = null;
        private final DatabaseReference palsReference;

        public FragDesc(DatabaseReference palsReference, int id) {
            this.palsReference = palsReference;
            this.id = id;
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.v("FOUFOU", "pause");
            for (Pal pal : palsGuessed ) {
                Log.v("FOUFOU", "pal : " + pal.getName());
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.v("FOUFOU", "resume");
            TableLayout tableLayout = requireView().findViewById(R.id.tableLayout);
            for (Pal pal : palsGuessed ){
                Log.v("FOUFOU", "pal : " + pal.getName());
                TableRow tableRow = new TableRow(this.getContext());
                tableRow.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border));
                TextView textName = new TextView(this.getContext());
                textName.setGravity(CENTER);
                textName.setPadding(8, 16, 8, 16);
                tableRow.addView(textName);
                TextView textId = new TextView(this.getContext());
                textId.setGravity(CENTER);
                textId.setPadding(8, 16, 8, 16);
                tableRow.addView(textId);
                TextView textTypes = new TextView(this.getContext());
                textTypes.setGravity(CENTER);
                textTypes.setPadding(8, 16, 8, 16);
                tableRow.addView(textTypes);
                TextView textSize = new TextView(this.getContext());
                textSize.setGravity(CENTER);
                textSize.setPadding(8, 16, 8, 16);
                tableRow.addView(textSize);
                tableLayout.addView(tableRow);

                textName.setText(pal.getName());
                if (pal.getName().equals(palOfTheDay.getName())) {
                    textName.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    textName.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                }

                textId.setText(String.valueOf(pal.getKey()));
                String key = pal.getKey();

                if (key.equals(palOfTheDay.getKey())) {
                    textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    if (key.substring(0, 3).equals(palOfTheDay.getKey().substring(0, 3))) {
                        textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                }

                String types = "";
                for (String type : pal.getTypes()) {
                    types += type + " ";
                }
                textTypes.setText(types);
                List<String> guessTypes = pal.getTypes();
                List<String> palTypes = palOfTheDay.getTypes();

                if (guessTypes.size() == 2 && palTypes.size() == 2) {
                    if ((Objects.equals(guessTypes.get(0), palTypes.get(0)) && Objects.equals(guessTypes.get(1), palTypes.get(1)) || (Objects.equals(guessTypes.get(0), palTypes.get(1)) && Objects.equals(guessTypes.get(1), palTypes.get(0))))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    } else if (Objects.equals(guessTypes.get(0), palTypes.get(0)) || Objects.equals(guessTypes.get(1), palTypes.get(1)) || Objects.equals(guessTypes.get(0), palTypes.get(1)) || Objects.equals(guessTypes.get(1), palTypes.get(0))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                } else if (guessTypes.size() == 1 && palTypes.size() == 1) {
                    if (Objects.equals(guessTypes.get(0), palTypes.get(0))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                } else {
                    boolean found = false;
                    for (String type : guessTypes) {
                        if (palTypes.contains(type)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                }
                textSize.setText(String.valueOf(pal.getSize()));
                if (Objects.equals(pal.getSize(), palOfTheDay.getSize())) {
                    textSize.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    textSize.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.frag_desc, container, false);
            TextView tv = view.findViewById(R.id.tvDesc);

            palList = new ArrayList<>();

            if (palsGuessed == null){
                palsGuessed = new ArrayList<>();
            }

            FirebaseStorage storage = FirebaseStorage.getInstance();


            if (!PalOfTheDayManager.getWin2(requireContext())) {
                palsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot palSnapshot : dataSnapshot.getChildren()) {
                            String palId = palSnapshot.getKey();
                            Pal pal = palSnapshot.getValue(Pal.class);
                            if (palId != null && palId.equals(String.valueOf(id))) {
                                palOfTheDay = pal;
                            }
                            palList.add(pal);
                        }

                        AutoCompleteTextView et = view.findViewById(R.id.et);
                        List<String> palName = new ArrayList<String>();
                        for (Pal pal : palList) {
                            palName.add(pal.getName());
                        }
                        CustomAdapter adapter = new CustomAdapter(requireContext(), palList, palName);
                        et.setAdapter(adapter);

                        if (palOfTheDay != null) {
                            Log.v("FERU", palOfTheDay.getName());
                            tv.setText(palOfTheDay.getDescription());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to read value.", databaseError.toException());
                    }
                });

                Button btnGuess = view.findViewById(R.id.btnGuess);
                btnGuess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guessClicked(view);
                    }
                });

                AutoCompleteTextView et = view.findViewById(R.id.et);
                et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            et.showDropDown();
                        }
                    }
                });

                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        et.showDropDown();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String input = s.toString().toLowerCase();
                        List<Pal> filteredPals = new ArrayList<>();
                        List<String> filteredPalsName = new ArrayList<>();

                        for (Pal pal : palList) {
                            if (pal.getName().toLowerCase().startsWith(input)) {
                                filteredPals.add(pal);
                                filteredPalsName.add(pal.getName());
                            }
                        }
                        CustomAdapter filteredAdapter = new CustomAdapter(requireContext(), filteredPals, filteredPalsName);
                        et.setAdapter(filteredAdapter);
                        et.showDropDown();
                    }
                });
            } else {
                id = PalOfTheDayManager.getLastPalId2(requireContext());
                Log.v("FERUU", String.valueOf(id));

                palsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot palSnapshot : dataSnapshot.getChildren()) {
                            String palId = palSnapshot.getKey();
                            Pal pal = palSnapshot.getValue(Pal.class);
                            if (palId != null && palId.equals(String.valueOf(id))) {
                                palOfTheDay = pal;
                            }
                        }
                        if (palOfTheDay != null) {
                            Log.v("FERU", palOfTheDay.getName());
                            tv.setText(palOfTheDay.getDescription());
                        }

                        AutoCompleteTextView et = view.findViewById(R.id.et);
                        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
                        LinearLayout llp = view.findViewById(R.id.llParent);
                        LinearLayout ll = view.findViewById(R.id.llChild);
                        llp.removeView(tableLayout);
                        ll.removeView(et);
                        Button btnGuess = view.findViewById(R.id.btnGuess);
                        ll.removeView(btnGuess);
                        TextView ttv = new TextView(requireContext());
                        ttv.setText("Pal's Description of the day already found !\nIt was :");
                        ttv.setPadding(0, 200, 0, 0);
                        ttv.setGravity(CENTER);
                        ttv.setTextAppearance(R.style.PalNameStyle);
                        ll.addView(ttv);
                        ImageView img = new ImageView(requireContext());
                        img.setPadding(10, 10, 10, 10);
                        img.setContentDescription("palOfTheDay");
                        Picasso.get()
                                .load(palOfTheDay.getImage())
                                .resize(750, 750)
                                .centerInside()
                                .into(img);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.gravity = Gravity.CENTER;
                        img.setLayoutParams(layoutParams);
                        img.setBackgroundResource(R.drawable.border);
                        ll.addView(img);
                        TextView tv = new TextView(requireContext());
                        tv.setPadding(0, 50, 0, 100);
                        tv.setText(palOfTheDay.getName());
                        tv.setGravity(CENTER);
                        tv.setTextAppearance(R.style.PalNameStyle);
                        ll.addView(tv);

                        LinearLayout lol = new LinearLayout(requireContext());
                        lol.setOrientation(LinearLayout.HORIZONTAL);
                        lol.setGravity(Gravity.CENTER);
                        Button btnNextGame = (Button) getLayoutInflater().inflate(R.layout.btn_share, null);
                        if (PalOfTheDayManager.getWin(requireContext()) && PalOfTheDayManager.getWin3(requireContext())) {
                            btnNextGame.setText("Home");
                        } else {
                            btnNextGame.setText("Next Game");
                        }
                        Button btnShare = (Button) getLayoutInflater().inflate(R.layout.btn_share, null);

                        btnNextGame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();

                                if (!PalOfTheDayManager.getWin(requireContext())) {
                                    bottomNavigationView.setSelectedItemId(R.id.guessMenu);
                                    transaction.replace(R.id.frag, new FragGuess(palsReference, PalOfTheDayManager.getLastPalId(requireContext())), "guess_fragment");
                                } else if (!PalOfTheDayManager.getWin3(requireContext())) {
                                    bottomNavigationView.setSelectedItemId(R.id.silMenu);
                                    transaction.replace(R.id.frag, new FragSil(palsReference, PalOfTheDayManager.getLastPalId3(requireContext())), "silhouette_fragment");
                                } else {
                                    bottomNavigationView.setSelectedItemId(R.id.homeMenu);
                                    transaction.replace(R.id.frag, new FragHome(palsReference), "home_fragment");
                                }

                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        });
                        btnShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "I found my pal description of the Day : " + palOfTheDay.getName() + " !\nGo find yours on Paldle !");
                                sendIntent.setType("text/plain");

                                Intent shareIntent = Intent.createChooser(sendIntent, null);
                                startActivity(shareIntent);
                            }
                        });
                        lol.addView(btnShare);
                        lol.addView(btnNextGame);
                        llp.addView(lol);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to read value.", databaseError.toException());
                    }
                });
            }
            return view;
        }


        public void guessClicked(View view) {
            AutoCompleteTextView et = view.findViewById(R.id.et);
            String guess = et.getText().toString().toLowerCase();
            boolean win = false;

            Pal guessPal = null;

            for (Pal pal : palList) {
                if (pal.getName().toLowerCase().equals(guess)) {
                    for (Pal palGuessed : palsGuessed) {
                        if (palGuessed.getName().equals(pal.getName())) {
                            et.setText("");
                            et.clearFocus();
                            return;
                        }
                    }
                    guessPal = pal;
                    break;
                }
            }

            if (guessPal != null) {
                palsGuessed.add(guessPal);
                TableLayout tableLayout = view.findViewById(R.id.tableLayout);
                TableRow tableRow = new TableRow(this.getContext());
                tableRow.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border));
                TextView textName = new TextView(this.getContext());
                textName.setGravity(CENTER);
                textName.setPadding(8, 16, 8, 16);
                tableRow.addView(textName);
                TextView textId = new TextView(this.getContext());
                textId.setGravity(CENTER);
                textId.setPadding(8, 16, 8, 16);
                tableRow.addView(textId);
                TextView textTypes = new TextView(this.getContext());
                textTypes.setGravity(CENTER);
                textTypes.setPadding(8, 16, 8, 16);
                tableRow.addView(textTypes);
                TextView textSize = new TextView(this.getContext());
                textSize.setGravity(CENTER);
                textSize.setPadding(8, 16, 8, 16);
                tableRow.addView(textSize);
                tableLayout.addView(tableRow);

                textName.setText(guessPal.getName());
                if (guessPal.getName().equals(palOfTheDay.getName())) {
                    textName.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    win = true;
                } else {
                    textName.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                }

                textId.setText(String.valueOf(guessPal.getKey()));
                String key = guessPal.getKey();

                if (key.equals(palOfTheDay.getKey())) {
                    textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    if (key.substring(0, 3).equals(palOfTheDay.getKey().substring(0, 3))) {
                        textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                }

                String types = "";
                for (String type : guessPal.getTypes()) {
                    types += type + " ";
                }
                textTypes.setText(types);
                List<String> guessTypes = guessPal.getTypes();
                List<String> palTypes = palOfTheDay.getTypes();

                if (guessTypes.size() == 2 && palTypes.size() == 2) {
                    if ((Objects.equals(guessTypes.get(0), palTypes.get(0)) && Objects.equals(guessTypes.get(1), palTypes.get(1)) || (Objects.equals(guessTypes.get(0), palTypes.get(1)) && Objects.equals(guessTypes.get(1), palTypes.get(0))))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    } else if (Objects.equals(guessTypes.get(0), palTypes.get(0)) || Objects.equals(guessTypes.get(1), palTypes.get(1)) || Objects.equals(guessTypes.get(0), palTypes.get(1)) || Objects.equals(guessTypes.get(1), palTypes.get(0))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                } else if (guessTypes.size() == 1 && palTypes.size() == 1) {
                    if (Objects.equals(guessTypes.get(0), palTypes.get(0))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                } else {
                    boolean found = false;
                    for (String type : guessTypes) {
                        if (palTypes.contains(type)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                }

                textSize.setText(String.valueOf(guessPal.getSize()));
                if (Objects.equals(guessPal.getSize(), palOfTheDay.getSize())) {
                    textSize.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    textSize.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                }

                if (win) {
                    PalOfTheDayManager.setWin2(requireContext(), true);

                    LinearLayout llp = view.findViewById(R.id.llParent);
                    LinearLayout ll = view.findViewById(R.id.llChild);
                    ll.removeView(et);
                    Button btnGuess = view.findViewById(R.id.btnGuess);
                    ll.removeView(btnGuess);
                    ImageView img = new ImageView(this.getContext());
                    img.setPadding(10, 10, 10, 10);
                    img.setContentDescription("palOfTheDay");
                    Picasso.get()
                            .load(palOfTheDay.getImage())
                            .resize(450, 450)
                            .centerInside()
                            .into(img);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.gravity = Gravity.CENTER;
                    img.setLayoutParams(layoutParams);
                    img.setBackgroundResource(R.drawable.border);
                    ll.addView(img);
                    TextView tv = new TextView(this.getContext());
                    tv.setText(palOfTheDay.getName());
                    tv.setGravity(CENTER);
                    tv.setTextAppearance(R.style.PalNameStyle);
                    ll.addView(tv);

                    LinearLayout lol = new LinearLayout(requireContext());
                    lol.setOrientation(LinearLayout.HORIZONTAL);
                    lol.setGravity(Gravity.CENTER);
                    Button btnNextGame = (Button) getLayoutInflater().inflate(R.layout.btn_share, null);
                    if (PalOfTheDayManager.getWin(requireContext()) && PalOfTheDayManager.getWin3(requireContext())) {
                        btnNextGame.setText("Home");
                    } else {
                        btnNextGame.setText("Next Game");
                    }
                    Button btnShare = (Button) getLayoutInflater().inflate(R.layout.btn_share, null);

                    btnNextGame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();

                            if (!PalOfTheDayManager.getWin(requireContext())) {
                                bottomNavigationView.setSelectedItemId(R.id.guessMenu);
                                transaction.replace(R.id.frag, new FragGuess(palsReference, PalOfTheDayManager.getLastPalId(requireContext())), "guess_fragment");
                            } else if (!PalOfTheDayManager.getWin3(requireContext())) {
                                bottomNavigationView.setSelectedItemId(R.id.silMenu);
                                transaction.replace(R.id.frag, new FragSil(palsReference, PalOfTheDayManager.getLastPalId3(requireContext())), "silhouette_fragment");
                            } else {
                                bottomNavigationView.setSelectedItemId(R.id.homeMenu);
                                transaction.replace(R.id.frag, new FragHome(palsReference), "home_fragment");
                            }

                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });
                    btnShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "I found my pal description of the Day : " + palOfTheDay.getName() + " !\nGo find yours on Paldle !");
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            startActivity(shareIntent);
                        }
                    });
                    lol.addView(btnShare);
                    lol.addView(btnNextGame);
                    llp.addView(lol);
                } else {
                    et.setText("");
                    et.clearFocus();
                }
            } else {
                return;
            }
        }
    }

    public static class FragSil extends Fragment {

        private int id;
        private Pal palOfTheDay;
        private List<Pal> palList;
        private List<Pal> palsGuessed = null;
        private final DatabaseReference palsReference;

        public FragSil(DatabaseReference palsReference, int id) {
            this.palsReference = palsReference;
            this.id = id;
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.v("FOUFOU", "pause");
            for (Pal pal : palsGuessed ) {
                Log.v("FOUFOU", "pal : " + pal.getName());
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.v("FOUFOU", "resume");
            TableLayout tableLayout = requireView().findViewById(R.id.tableLayout);
            for (Pal pal : palsGuessed ){
                Log.v("FOUFOU", "pal : " + pal.getName());
                TableRow tableRow = new TableRow(this.getContext());
                tableRow.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border));
                TextView textName = new TextView(this.getContext());
                textName.setGravity(CENTER);
                textName.setPadding(8, 16, 8, 16);
                tableRow.addView(textName);
                TextView textId = new TextView(this.getContext());
                textId.setGravity(CENTER);
                textId.setPadding(8, 16, 8, 16);
                tableRow.addView(textId);
                TextView textTypes = new TextView(this.getContext());
                textTypes.setGravity(CENTER);
                textTypes.setPadding(8, 16, 8, 16);
                tableRow.addView(textTypes);
                TextView textSize = new TextView(this.getContext());
                textSize.setGravity(CENTER);
                textSize.setPadding(8, 16, 8, 16);
                tableRow.addView(textSize);
                tableLayout.addView(tableRow);

                textName.setText(pal.getName());
                if (pal.getName().equals(palOfTheDay.getName())) {
                    textName.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    textName.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                }

                textId.setText(String.valueOf(pal.getKey()));
                String key = pal.getKey();

                if (key.equals(palOfTheDay.getKey())) {
                    textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    if (key.substring(0, 3).equals(palOfTheDay.getKey().substring(0, 3))) {
                        textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                }

                String types = "";
                for (String type : pal.getTypes()) {
                    types += type + " ";
                }
                textTypes.setText(types);
                List<String> guessTypes = pal.getTypes();
                List<String> palTypes = palOfTheDay.getTypes();

                if (guessTypes.size() == 2 && palTypes.size() == 2) {
                    if ((Objects.equals(guessTypes.get(0), palTypes.get(0)) && Objects.equals(guessTypes.get(1), palTypes.get(1)) || (Objects.equals(guessTypes.get(0), palTypes.get(1)) && Objects.equals(guessTypes.get(1), palTypes.get(0))))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    } else if (Objects.equals(guessTypes.get(0), palTypes.get(0)) || Objects.equals(guessTypes.get(1), palTypes.get(1)) || Objects.equals(guessTypes.get(0), palTypes.get(1)) || Objects.equals(guessTypes.get(1), palTypes.get(0))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                } else if (guessTypes.size() == 1 && palTypes.size() == 1) {
                    if (Objects.equals(guessTypes.get(0), palTypes.get(0))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                } else {
                    boolean found = false;
                    for (String type : guessTypes) {
                        if (palTypes.contains(type)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                }
                textSize.setText(String.valueOf(pal.getSize()));
                if (Objects.equals(pal.getSize(), palOfTheDay.getSize())) {
                    textSize.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    textSize.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.frag_silhouette, container, false);
            ImageView iv = view.findViewById(R.id.imSilhouette);

            palList = new ArrayList<>();

            if (palsGuessed == null){
                palsGuessed = new ArrayList<>();
            }

            FirebaseStorage storage = FirebaseStorage.getInstance();


            if (!PalOfTheDayManager.getWin3(requireContext())) {
                palsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot palSnapshot : dataSnapshot.getChildren()) {
                            String palId = palSnapshot.getKey();
                            Pal pal = palSnapshot.getValue(Pal.class);
                            if (palId != null && palId.equals(String.valueOf(id))) {
                                palOfTheDay = pal;
                            }
                            palList.add(pal);
                        }

                        AutoCompleteTextView et = view.findViewById(R.id.et);
                        List<String> palName = new ArrayList<String>();
                        for (Pal pal : palList) {
                            palName.add(pal.getName());
                        }
                        CustomAdapter adapter = new CustomAdapter(requireContext(), palList, palName);
                        et.setAdapter(adapter);

                        if (palOfTheDay != null) {
                            Log.v("FERU", palOfTheDay.getName());
                            iv.setPadding(10, 10, 10, 10);
                            iv.setContentDescription("palOfTheDay");
                            Picasso.get()
                                    .load(palOfTheDay.getSilhouette())
                                    .resize(450, 450)
                                    .centerInside()
                                    .into(iv);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to read value.", databaseError.toException());
                    }
                });

                Button btnGuess = view.findViewById(R.id.btnGuess);
                btnGuess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guessClicked(view);
                    }
                });

                AutoCompleteTextView et = view.findViewById(R.id.et);
                et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            et.showDropDown();
                        }
                    }
                });

                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        et.showDropDown();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String input = s.toString().toLowerCase();
                        List<Pal> filteredPals = new ArrayList<>();
                        List<String> filteredPalsName = new ArrayList<>();

                        for (Pal pal : palList) {
                            if (pal.getName().toLowerCase().startsWith(input)) {
                                filteredPals.add(pal);
                                filteredPalsName.add(pal.getName());
                            }
                        }
                        CustomAdapter filteredAdapter = new CustomAdapter(requireContext(), filteredPals, filteredPalsName);
                        et.setAdapter(filteredAdapter);
                        et.showDropDown();
                    }
                });
            } else {
                id = PalOfTheDayManager.getLastPalId3(requireContext());
                Log.v("FERUU", String.valueOf(id));

                palsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot palSnapshot : dataSnapshot.getChildren()) {
                            String palId = palSnapshot.getKey();
                            Pal pal = palSnapshot.getValue(Pal.class);
                            if (palId != null && palId.equals(String.valueOf(id))) {
                                palOfTheDay = pal;
                            }
                        }
                        if (palOfTheDay != null) {
                            Log.v("FERU", palOfTheDay.getName());
                        }

                        AutoCompleteTextView et = view.findViewById(R.id.et);
                        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
                        LinearLayout llp = view.findViewById(R.id.llParent);
                        LinearLayout ll = view.findViewById(R.id.llChild);
                        llp.removeView(tableLayout);
                        ll.removeView(et);
                        Button btnGuess = view.findViewById(R.id.btnGuess);
                        ll.removeView(btnGuess);
                        TextView ttv = new TextView(requireContext());
                        ttv.setText("Pal's Silhouette of the day already found !\nIt was :");
                        ttv.setPadding(0, 200, 0, 0);
                        ttv.setGravity(CENTER);
                        ttv.setTextAppearance(R.style.PalNameStyle);
                        ll.addView(ttv);
                        iv.setPadding(10, 10, 10, 10);
                        iv.setContentDescription("palOfTheDay");
                        Picasso.get()
                                .load(palOfTheDay.getImage())
                                .resize(750, 750)
                                .centerInside()
                                .into(iv);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.gravity = Gravity.CENTER;
                        TextView tv = new TextView(requireContext());
                        tv.setPadding(0, 50, 0, 100);
                        tv.setText(palOfTheDay.getName());
                        tv.setGravity(CENTER);
                        tv.setTextAppearance(R.style.PalNameStyle);
                        ll.addView(tv);

                        LinearLayout lol = new LinearLayout(requireContext());
                        lol.setOrientation(LinearLayout.HORIZONTAL);
                        lol.setGravity(Gravity.CENTER);
                        Button btnNextGame = (Button) getLayoutInflater().inflate(R.layout.btn_share, null);
                        if (PalOfTheDayManager.getWin(requireContext()) && PalOfTheDayManager.getWin2(requireContext())) {
                            btnNextGame.setText("Home");
                        } else {
                            btnNextGame.setText("Next Game");
                        }
                        Button btnShare = (Button) getLayoutInflater().inflate(R.layout.btn_share, null);

                        btnNextGame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();

                                if (!PalOfTheDayManager.getWin(requireContext())) {
                                    bottomNavigationView.setSelectedItemId(R.id.guessMenu);
                                    transaction.replace(R.id.frag, new FragGuess(palsReference, PalOfTheDayManager.getLastPalId(requireContext())), "guess_fragment");
                                } else if (!PalOfTheDayManager.getWin2(requireContext())) {
                                    bottomNavigationView.setSelectedItemId(R.id.descMenu);
                                    transaction.replace(R.id.frag, new FragDesc(palsReference, PalOfTheDayManager.getLastPalId2(requireContext())), "desc_fragment");
                                } else {
                                    bottomNavigationView.setSelectedItemId(R.id.homeMenu);
                                    transaction.replace(R.id.frag, new FragHome(palsReference), "home_fragment");
                                }

                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        });
                        btnShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "I found my pal silhouette of the Day : " + palOfTheDay.getName() + " !\nGo find yours on Paldle !");
                                sendIntent.setType("text/plain");

                                Intent shareIntent = Intent.createChooser(sendIntent, null);
                                startActivity(shareIntent);
                            }
                        });
                        lol.addView(btnShare);
                        lol.addView(btnNextGame);
                        llp.addView(lol);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to read value.", databaseError.toException());
                    }
                });
            }
            return view;
        }


        public void guessClicked(View view) {
            AutoCompleteTextView et = view.findViewById(R.id.et);
            String guess = et.getText().toString().toLowerCase();
            boolean win = false;

            Pal guessPal = null;

            for (Pal pal : palList) {
                if (pal.getName().toLowerCase().equals(guess)) {
                    for (Pal palGuessed : palsGuessed) {
                        if (palGuessed.getName().equals(pal.getName())) {
                            et.setText("");
                            et.clearFocus();
                            return;
                        }
                    }
                    guessPal = pal;
                    break;
                }
            }

            if (guessPal != null) {
                palsGuessed.add(guessPal);
                TableLayout tableLayout = view.findViewById(R.id.tableLayout);
                TableRow tableRow = new TableRow(this.getContext());
                tableRow.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border));
                TextView textName = new TextView(this.getContext());
                textName.setGravity(CENTER);
                textName.setPadding(8, 16, 8, 16);
                tableRow.addView(textName);
                TextView textId = new TextView(this.getContext());
                textId.setGravity(CENTER);
                textId.setPadding(8, 16, 8, 16);
                tableRow.addView(textId);
                TextView textTypes = new TextView(this.getContext());
                textTypes.setGravity(CENTER);
                textTypes.setPadding(8, 16, 8, 16);
                tableRow.addView(textTypes);
                TextView textSize = new TextView(this.getContext());
                textSize.setGravity(CENTER);
                textSize.setPadding(8, 16, 8, 16);
                tableRow.addView(textSize);
                tableLayout.addView(tableRow);

                textName.setText(guessPal.getName());
                if (guessPal.getName().equals(palOfTheDay.getName())) {
                    textName.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    win = true;
                } else {
                    textName.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                }

                textId.setText(String.valueOf(guessPal.getKey()));
                String key = guessPal.getKey();

                if (key.equals(palOfTheDay.getKey())) {
                    textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    if (key.substring(0, 3).equals(palOfTheDay.getKey().substring(0, 3))) {
                        textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textId.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                }

                String types = "";
                for (String type : guessPal.getTypes()) {
                    types += type + " ";
                }
                textTypes.setText(types);
                List<String> guessTypes = guessPal.getTypes();
                List<String> palTypes = palOfTheDay.getTypes();

                if (guessTypes.size() == 2 && palTypes.size() == 2) {
                    if ((Objects.equals(guessTypes.get(0), palTypes.get(0)) && Objects.equals(guessTypes.get(1), palTypes.get(1)) || (Objects.equals(guessTypes.get(0), palTypes.get(1)) && Objects.equals(guessTypes.get(1), palTypes.get(0))))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    } else if (Objects.equals(guessTypes.get(0), palTypes.get(0)) || Objects.equals(guessTypes.get(1), palTypes.get(1)) || Objects.equals(guessTypes.get(0), palTypes.get(1)) || Objects.equals(guessTypes.get(1), palTypes.get(0))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                } else if (guessTypes.size() == 1 && palTypes.size() == 1) {
                    if (Objects.equals(guessTypes.get(0), palTypes.get(0))) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                } else {
                    boolean found = false;
                    for (String type : guessTypes) {
                        if (palTypes.contains(type)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_orange_background));
                    } else {
                        textTypes.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                    }
                }

                textSize.setText(String.valueOf(guessPal.getSize()));
                if (Objects.equals(guessPal.getSize(), palOfTheDay.getSize())) {
                    textSize.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_green_background));
                } else {
                    textSize.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border_red_background));
                }

                if (win) {
                    PalOfTheDayManager.setWin3(requireContext(), true);

                    LinearLayout llp = view.findViewById(R.id.llParent);
                    LinearLayout ll = view.findViewById(R.id.llChild);
                    ll.removeView(et);
                    Button btnGuess = view.findViewById(R.id.btnGuess);
                    ll.removeView(btnGuess);
                    ImageView iv = view.findViewById(R.id.imSilhouette);
                    iv.setPadding(10, 10, 10, 10);
                    iv.setContentDescription("palOfTheDay");
                    Picasso.get()
                            .load(palOfTheDay.getImage())
                            .resize(450, 450)
                            .centerInside()
                            .into(iv);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.gravity = Gravity.CENTER;
                    TextView tv = new TextView(this.getContext());
                    tv.setText(palOfTheDay.getName());
                    tv.setGravity(CENTER);
                    tv.setTextAppearance(R.style.PalNameStyle);
                    ll.addView(tv);

                    LinearLayout lol = new LinearLayout(requireContext());
                    lol.setOrientation(LinearLayout.HORIZONTAL);
                    lol.setGravity(Gravity.CENTER);
                    Button btnNextGame = (Button) getLayoutInflater().inflate(R.layout.btn_share, null);
                    if (PalOfTheDayManager.getWin(requireContext()) && PalOfTheDayManager.getWin2(requireContext())) {
                        btnNextGame.setText("Home");
                    } else {
                        btnNextGame.setText("Next Game");
                    }
                    Button btnShare = (Button) getLayoutInflater().inflate(R.layout.btn_share, null);

                    btnNextGame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();

                            if (!PalOfTheDayManager.getWin(requireContext())) {
                                bottomNavigationView.setSelectedItemId(R.id.guessMenu);
                                transaction.replace(R.id.frag, new FragGuess(palsReference, PalOfTheDayManager.getLastPalId(requireContext())), "guess_fragment");
                            } else if (!PalOfTheDayManager.getWin2(requireContext())) {
                                bottomNavigationView.setSelectedItemId(R.id.descMenu);
                                transaction.replace(R.id.frag, new FragDesc(palsReference, PalOfTheDayManager.getLastPalId2(requireContext())), "desc_fragment");
                            } else {
                                bottomNavigationView.setSelectedItemId(R.id.homeMenu);
                                transaction.replace(R.id.frag, new FragHome(palsReference), "home_fragment");
                            }

                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });
                    btnShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "I found my pal silhouette of the Day : " + palOfTheDay.getName() + " !\nGo find yours on Paldle !");
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            startActivity(shareIntent);
                        }
                    });
                    lol.addView(btnShare);
                    lol.addView(btnNextGame);
                    llp.addView(lol);
                } else {
                    et.setText("");
                    et.clearFocus();
                }
            } else {
                return;
            }
        }
    }

    public static class FragPaldex extends Fragment implements View.OnClickListener{


        private final DatabaseReference palsReference;

        public FragPaldex(DatabaseReference palsReference) {
            this.palsReference = palsReference;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.frag_paldex, container, false);

            GridLayout grid = view.findViewById(R.id.grid);

            palsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot palSnapshot : dataSnapshot.getChildren()) {
                            String id = palSnapshot.getKey();
                            Pal pal = palSnapshot.getValue(Pal.class);

                            ImageView img = new ImageView(getContext());
                            img.setContentDescription(id);
                            img.setOnClickListener(FragPaldex.this);
                            img.setPadding(10, 10, 10, 10);
                            Picasso.get()
                                    .load(pal.getImage())
                                    .resize(450, 450)
                                    .centerInside()
                                    .into(img);

                            TextView tv = new TextView(getContext());
                            tv.setText(pal.getName());
                            tv.setGravity(Gravity.CENTER);
                            tv.setTextAppearance(R.style.PalNameStyle);

                            LinearLayout ll = new LinearLayout(getContext());
                            ll.setOrientation(LinearLayout.VERTICAL);
                            ll.setGravity(Gravity.CENTER);
                            Drawable borderDrawable = ContextCompat.getDrawable(getContext(), R.drawable.border);
                            ll.setBackground(borderDrawable);

                            ll.addView(img);
                            ll.addView(tv);

                            grid.addView(ll);

                        }
                    } else {
                        TextView noPalsTextView = new TextView(getContext());
                        noPalsTextView.setText("No pals found in the database.");
                        grid.addView(noPalsTextView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            return view;
        }

        public void onClick(View v) {
            String id = (String) v.getContentDescription();
            Fragment fragPalView = new FragPalView();
            Bundle args = new Bundle();
            args.putString("EXTRA_ID", id);
            fragPalView.setArguments(args);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frag, fragPalView);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public static class FragPalView extends Fragment implements View.OnClickListener {

        private DatabaseReference palsReference;
        private String palId;

        public FragPalView() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.frag_palview, container, false);

            Bundle args = getArguments();
            if (args != null) {
                palId = args.getString("EXTRA_ID");

                if (palId != null && !palId.isEmpty()) {
                    palsReference = FirebaseDatabase.getInstance().getReference("pals").child(palId);

                    palsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Pal pal = dataSnapshot.getValue(Pal.class);

                                ImageView img = view.findViewById(R.id.img);
                                img.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.border));
                                img.setPadding(10, 10, 10, 10);
                                Picasso.get()
                                        .load(pal.getImage())
                                        .resize(800, 800)
                                        .centerInside()
                                        .into(img);

                                TextView name = view.findViewById(R.id.name);
                                name.setText(pal.getName() + " (" + pal.getKey() + ")");

                                TextView desc = view.findViewById(R.id.desc);
                                desc.setText(pal.getDescription());

                                TextView types = view.findViewById(R.id.types);
                                String typess = "Types : ";
                                for (String type : pal.getTypes()) {
                                    typess += type + " ";
                                }
                                types.setText(typess);

                                TextView size = view.findViewById(R.id.size);
                                size.setText("Size : " + pal.getSize());

                                Button backButton = view.findViewById(R.id.btnBack);
                                backButton.setOnClickListener(FragPalView.this);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // error
                        }
                    });
                }
            }
            return view;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnBack) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frag, new FragPaldex(FirebaseDatabase.getInstance().getReference("pals")));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }

    public static class FragHome extends Fragment implements View.OnClickListener{

        private DatabaseReference palsReference;

        public FragHome(DatabaseReference palsReference){
            this.palsReference = palsReference;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_home, container, false);
            Button btnGuess = view.findViewById(R.id.btnGuessGame);
            btnGuess.setOnClickListener(this);
            Button btnDesc = view.findViewById(R.id.btnDescGame);
            btnDesc.setOnClickListener(this);
            Button btnSil = view.findViewById(R.id.btnSilGame);
            btnSil.setOnClickListener(this);


            if (PalOfTheDayManager.getWin(requireContext())) {
                btnGuess.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            }
            if (PalOfTheDayManager.getWin2(requireContext())) {
                btnDesc.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            }
            if (PalOfTheDayManager.getWin3(requireContext())) {
                btnSil.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            }
            return view;
        }

        @Override
        public void onClick(View v) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
            if (v.getId() == R.id.btnGuessGame) {
                transaction.replace(R.id.frag, new FragGuess(palsReference, PalOfTheDayManager.getLastPalId(requireContext())));
                bottomNavigationView.setSelectedItemId(R.id.guessMenu);
            } else if (v.getId() == R.id.btnDescGame) {
                transaction.replace(R.id.frag, new FragDesc(palsReference, PalOfTheDayManager.getLastPalId2(requireContext())));
                bottomNavigationView.setSelectedItemId(R.id.descMenu);
            } if (v.getId() == R.id.btnSilGame) {
                transaction.replace(R.id.frag, new FragSil(palsReference, PalOfTheDayManager.getLastPalId3(requireContext())));
                bottomNavigationView.setSelectedItemId(R.id.silMenu);
            }
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }
}
