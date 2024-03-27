    package uqac.dim.paldle;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;

    import android.content.Intent;
    import android.graphics.drawable.Drawable;
    import android.os.Bundle;
    import android.view.Gravity;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.GridLayout;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;

    import com.google.android.material.bottomnavigation.BottomNavigationView;
    import com.google.firebase.FirebaseApp;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import com.squareup.picasso.Picasso;

    import uqac.dim.pallll.Pal;

    public class Paldex extends AppCompatActivity implements View.OnClickListener{

        private final DatabaseReference palsReference = FirebaseDatabase.getInstance().getReference("pals");

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_paldex);

            FirebaseApp.initializeApp(this);

            GridLayout grid = findViewById(R.id.grid);

            palsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot palSnapshot : dataSnapshot.getChildren()) {
                            String id = palSnapshot.getKey();
                            Pal pal = palSnapshot.getValue(Pal.class);


                            ImageView img = new ImageView(Paldex.this);
                            img.setContentDescription(id);
                            img.setOnClickListener(Paldex.this);
                            img.setPadding(10, 10, 10, 10);
                            Picasso.get()
                                    .load(pal.getImage())
                                    .resize(450, 450)
                                    .centerInside()
                                    .into(img);



                            TextView tv = new TextView(Paldex.this);
                            tv.setText(pal.getName());
                            tv.setGravity(Gravity.CENTER);
                            tv.setTextAppearance(R.style.PalNameStyle);


                            LinearLayout ll = new LinearLayout(Paldex.this);
                            ll.setOrientation(LinearLayout.VERTICAL);
                            ll.setGravity(Gravity.CENTER);
                            Drawable borderDrawable = ContextCompat.getDrawable(Paldex.this, R.drawable.border);
                            ll.setBackground(borderDrawable);


                            ll.addView(img);
                            ll.addView(tv);

                            grid.addView(ll);


                        }
                    } else {
                        TextView noPalsTextView = new TextView(Paldex.this);
                        noPalsTextView.setText("No pals found in the database.");
                        grid.addView(noPalsTextView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.paldexMenu);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.paldexMenu) {
                        return true;
                    } else if (id == R.id.homeMenu) {
                        Intent intent = new Intent(Paldex.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });
        }

        @Override
        public void onClick(View v) {
            String id = (String) v.getContentDescription();
            Intent intent = new Intent(Paldex.this, PalView.class);
            intent.putExtra("EXTRA_ID", id);
            startActivity(intent);
        }


    }
