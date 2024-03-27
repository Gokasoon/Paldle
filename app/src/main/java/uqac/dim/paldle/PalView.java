package uqac.dim.paldle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import uqac.dim.pallll.Pal;

public class PalView extends AppCompatActivity {

    private DatabaseReference palsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pal_view);

        Intent intent = getIntent();
        if (intent != null) {
            String id = intent.getStringExtra("EXTRA_ID");

            if (id != null && !id.isEmpty()) {
                palsReference = FirebaseDatabase.getInstance().getReference("pals").child(id);

                palsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Pal pal = dataSnapshot.getValue(Pal.class);

                            ImageView img = findViewById(R.id.img);
                            img.setBackground(getDrawable(R.drawable.border));
                            img.setPadding(10, 10, 10, 10);
                            Picasso.get()
                                    .load(pal.getImage())
                                    .resize(800, 800)
                                    .centerInside()
                                    .into(img);

                            TextView name = findViewById(R.id.name);
                            name.setText(pal.getName() + " (" + pal.getKey() + ")");

                            TextView desc = findViewById(R.id.desc);
                            desc.setText(pal.getDescription());

                            TextView types = findViewById(R.id.types);
                            String typess = "Types : ";
                            for (String type : pal.getTypes() ){
                                typess += type + " ";
                            }
                            types.setText(typess);

                            TextView size = findViewById(R.id.size);
                            size.setText("Size : " + pal.getSize());

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle the error case
                    }
                });
            }
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(0);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.paldexMenu) {
                    Intent intent = new Intent(PalView.this, Paldex.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.homeMenu) {
                    Intent intent = new Intent(PalView.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    public void paldexClicked(View view) {
        Intent intent = new Intent(PalView.this, Paldex.class);
        startActivity(intent);
    }
}
