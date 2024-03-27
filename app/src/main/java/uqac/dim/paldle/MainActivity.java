package uqac.dim.paldle;

import static android.view.Gravity.CENTER;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
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

public class MainActivity extends AppCompatActivity {

    private final DatabaseReference palsReference = FirebaseDatabase.getInstance().getReference("pals");
    private int day;
    private int id;
    private int [] ids = new int [15]; // track the last 15 pals
    private Pal palOfTheDay;
    private List<Pal> palList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        boolean find;
        do {
            id = getRandomId();
            find = false;
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == id) {
                    find = true;
                    id = getRandomId();
                    break;
                }
            }
        } while (find);

        day = 0; // TODO
        ids[day] = id;

        palList = new ArrayList<>();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

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

                AutoCompleteTextView et = findViewById(R.id.et);
                List<String> palName = new ArrayList<String>();
                for (Pal pal : palList) {
                    palName.add(pal.getName());
                }
                CustomAdapter adapter = new CustomAdapter(MainActivity.this, palList, palName);
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

        AutoCompleteTextView et = findViewById(R.id.et);

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

                CustomAdapter filteredAdapter = new CustomAdapter(MainActivity.this, filteredPals, filteredPalsName);
                et.setAdapter(filteredAdapter);
                et.showDropDown();
            }


        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.homeMenu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.paldexMenu) {
                    Intent intent = new Intent(MainActivity.this, Paldex.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.homeMenu) {
                    return true;
                }
                return false;
            }
        });


    }


    public void paldexClicked(View view) {
        Intent intent = new Intent(MainActivity.this, Paldex.class);
        startActivity(intent);
    }

    public static int getRandomId() {
        Random random = new Random();
        return random.nextInt(137);
    }

    public void guessClicked(View view) {
        AutoCompleteTextView et = findViewById(R.id.et);
        String guess = et.getText().toString().toLowerCase();
        boolean win = false;

        Pal guessPal = null;

        for (Pal pal : palList) {
            if (pal.getName().toLowerCase().equals(guess)) {
                guessPal = pal;
                break;
            }
        }

        if (guessPal != null) {
            TableLayout tableLayout = findViewById(R.id.tableLayout);
            TableRow tableRow = new TableRow(this);
            tableRow.setBackground(getDrawable(R.drawable.border));
            TextView textName = new TextView(this);
            textName.setGravity(CENTER);
            textName.setPadding(8,16,8,16);
            tableRow.addView(textName);
            TextView textId = new TextView(this);
            textId.setGravity(CENTER);
            textId.setPadding(8,16,8,16);
            tableRow.addView(textId);
            TextView textTypes = new TextView(this);
            textTypes.setGravity(CENTER);
            textTypes.setPadding(8,16,8,16);
            tableRow.addView(textTypes);
            TextView textSize = new TextView(this);
            textSize.setGravity(CENTER);
            textSize.setPadding(8,16,8,16);
            tableRow.addView(textSize);
            tableLayout.addView(tableRow);

            textName.setText(guessPal.getName());
            if (guessPal.getName().equals(palOfTheDay.getName())) {
                textName.setBackground(getDrawable(R.drawable.border_green_background));
                win = true;
            } else {
                textName.setBackground(getDrawable(R.drawable.border_red_background));
            }

            textId.setText(String.valueOf(guessPal.getKey()));
            String key = guessPal.getKey();

            if (key.equals(palOfTheDay.getKey())) {
                textId.setBackground(getDrawable(R.drawable.border_green_background));
            } else {
                if (key.substring(0, 3).equals(palOfTheDay.getKey().substring(0, 3))) {
                    textId.setBackground(getDrawable(R.drawable.border_orange_background));
                } else {
                    textId.setBackground(getDrawable(R.drawable.border_red_background));
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
                if ((Objects.equals(guessTypes.get(0), palTypes.get(0)) && Objects.equals(guessTypes.get(1), palTypes.get(1)) || (Objects.equals(guessTypes.get(0), palTypes.get(1)) && Objects.equals(guessTypes.get(1), palTypes.get(0))))){
                    textTypes.setBackground(getDrawable(R.drawable.border_green_background));
                } else if (Objects.equals(guessTypes.get(0), palTypes.get(0)) || Objects.equals(guessTypes.get(1), palTypes.get(1)) || Objects.equals(guessTypes.get(0), palTypes.get(1)) || Objects.equals(guessTypes.get(1), palTypes.get(0))) {
                    textTypes.setBackground(getDrawable(R.drawable.border_orange_background));
                } else {
                    textTypes.setBackground(getDrawable(R.drawable.border_red_background));
                }
            } else if (guessTypes.size() == 1 && palTypes.size() == 1){
                if (Objects.equals(guessTypes.get(0), palTypes.get(0))) {
                    textTypes.setBackground(getDrawable(R.drawable.border_green_background));
                } else {
                    textTypes.setBackground(getDrawable(R.drawable.border_red_background));
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
                    textTypes.setBackground(getDrawable(R.drawable.border_orange_background));
                } else {
                    textTypes.setBackground(getDrawable(R.drawable.border_red_background));
                }
            }

            textSize.setText(String.valueOf(guessPal.getSize()));
            if (Objects.equals(guessPal.getSize(), palOfTheDay.getSize())) {
                textSize.setBackground(getDrawable(R.drawable.border_green_background));
            } else {
                textSize.setBackground(getDrawable(R.drawable.border_red_background));
            }

            if (win){
                LinearLayout llp = findViewById(R.id.llParent);
                LinearLayout ll = findViewById(R.id.llChild);
                ll.removeView(et);
                Button btnGuess = findViewById(R.id.btnGuess);
                ll.removeView(btnGuess);
                ImageView img = new ImageView(this);
                img.setPadding(10, 10, 10, 10);
                img.setContentDescription("palOfTheDay");
                Picasso.get()
                        .load(palOfTheDay.getImage())
                        .resize(450, 450)
                        .centerInside()
                        .into(img);
                ll.addView(img);
                TextView tv = new TextView(this);
                tv.setText(palOfTheDay.getName());
                tv.setGravity(CENTER);
                tv.setTextAppearance(R.style.PalNameStyle);
                ll.addView(tv);
            } else {
                et.setText("");
            }

        } else {
            return;
        }
    }
}
