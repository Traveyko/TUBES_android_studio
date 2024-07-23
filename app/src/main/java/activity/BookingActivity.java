package activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.travel_dan_booking_hotel.R;

import java.util.Calendar;
import java.util.HashMap;

import database.DatabaseHelper;
import session.SessionManager;

public class BookingActivity extends AppCompatActivity {

    protected Cursor cursor;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    Spinner spinLokasi, spinHotel;
    SessionManager session;
    String email;
    int id_book;
    public String sLokasi, sHotel, sTanggal;
    int harga_hotel;
    private EditText etTanggal;
    private DatePickerDialog dpTanggal;
    int jmlHarga;
    Calendar newCalendar = Calendar.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        dbHelper = new DatabaseHelper(activity.BookingActivity.this);
        db = dbHelper.getReadableDatabase();

        final String[] Lokasi = {"Jakarta", "Bandung", "Purwokerto", "Yogyakarta", "Surabaya"};
        final String[] Hotel = {"Harper", "Ladera", "OYO", "REDHORS"};

        spinLokasi = findViewById(R.id.Lokasi);
        spinHotel = findViewById(R.id.Hotel);

        ArrayAdapter<CharSequence> adapterLokasi = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, Lokasi);
        adapterLokasi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLokasi.setAdapter(adapterLokasi);

        ArrayAdapter<CharSequence> adapterHotel = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, Hotel);
        adapterHotel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinHotel.setAdapter(adapterHotel);


        spinLokasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sLokasi = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinHotel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sHotel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        Button btnBook = findViewById(R.id.reserv);

        etTanggal = findViewById(R.id.tanggal_booking);
        etTanggal.setInputType(InputType.TYPE_NULL);
        etTanggal.requestFocus();
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        email = user.get(SessionManager.KEY_EMAIL);
        setDateTimeField();

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perhitunganHarga();
                if (sLokasi != null && sHotel != null && sTanggal != null ) {
                    if ((sLokasi.equalsIgnoreCase("Jakarta") && sHotel.equalsIgnoreCase(""))
                            || (sLokasi.equalsIgnoreCase("Bandung") && sHotel.equalsIgnoreCase(""))
                            || (sLokasi.equalsIgnoreCase("Purwokerto") && sHotel.equalsIgnoreCase(""))
                            || (sLokasi.equalsIgnoreCase("Yogyakarta") && sHotel.equalsIgnoreCase(""))
                            || (sLokasi.equalsIgnoreCase("Surabaya") && sHotel.equalsIgnoreCase(""))) {
                        Toast.makeText(activity.BookingActivity.this, "Asal dan Tujuan tidak boleh kosong !", Toast.LENGTH_LONG).show();
                    } else {
                        AlertDialog dialog = new AlertDialog.Builder(activity.BookingActivity.this)
                                .setTitle("Ingin booking sekarang?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            db.execSQL("INSERT INTO TB_BOOK (asal, tujuan, tanggal) VALUES ('" +
                                                    sLokasi + "','" +
                                                    sHotel + "','" +
                                                    sTanggal + "');");
                                            cursor = db.rawQuery("SELECT id_book FROM TB_BOOK ORDER BY id_book DESC", null);
                                            cursor.moveToLast();
                                            if (cursor.getCount() > 0) {
                                                cursor.moveToPosition(0);
                                                id_book = cursor.getInt(0);
                                            }
                                            db.execSQL("INSERT INTO TB_HARGA (username, id_book, harga_total) VALUES ('" +
                                                    email + "','" +
                                                    id_book + "','" +
                                                    harga_hotel + "');");
                                            Toast.makeText(activity.BookingActivity.this, "Booking berhasil", Toast.LENGTH_LONG).show();
                                            finish();
                                        } catch (Exception e) {
                                            Toast.makeText(activity.BookingActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Tidak", null)
                                .create();
                        dialog.show();
                    }
                } else {
                    Toast.makeText(activity.BookingActivity.this, "Mohon lengkapi data pemesanan!", Toast.LENGTH_LONG).show();
                }
            }
        });

        setupToolbar();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbKrl);
        toolbar.setTitle("Form Booking");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void perhitunganHarga() {
        if (sLokasi.equalsIgnoreCase("Jakarta") && sHotel.equalsIgnoreCase("Harper")) {
            harga_hotel = 100000;
        } else if (sLokasi.equalsIgnoreCase("Jakarta") && sHotel.equalsIgnoreCase("Ladera")) {
            harga_hotel = 200000;
        } else if (sLokasi.equalsIgnoreCase("Jakarta") && sHotel.equalsIgnoreCase("OYO")) {
            harga_hotel = 150000;
        } else if (sLokasi.equalsIgnoreCase("Jakarta") && sHotel.equalsIgnoreCase("REDHORS")) {
            harga_hotel = 180000;
        } else if (sLokasi.equalsIgnoreCase("Bandung") &&sHotel.equalsIgnoreCase("Harper")) {
            harga_hotel = 100000;
        } else if (sLokasi.equalsIgnoreCase("Bandung") && sHotel.equalsIgnoreCase("Ladera")) {
            harga_hotel = 120000;
        } else if (sLokasi.equalsIgnoreCase("Bandung") && sHotel.equalsIgnoreCase("OYO")) {
            harga_hotel = 120000;
        } else if (sLokasi.equalsIgnoreCase("Bandung") && sHotel.equalsIgnoreCase("REDHORS")) {
            harga_hotel = 190000;
        } else if (sLokasi.equalsIgnoreCase("Surabaya") && sHotel.equalsIgnoreCase("Harper")) {
            harga_hotel = 200000;
        } else if (sLokasi.equalsIgnoreCase("Surabaya") && sHotel.equalsIgnoreCase("Ladera")) {
            harga_hotel = 120000;
        } else if (sLokasi.equalsIgnoreCase("Surabaya") && sHotel.equalsIgnoreCase("OYO")) {
            harga_hotel = 170000;
        } else if (sLokasi.equalsIgnoreCase("Surabaya") && sHotel.equalsIgnoreCase("REDHORS")) {
            harga_hotel = 180000;
        } else if (sLokasi.equalsIgnoreCase("Purwokerto") && sHotel.equalsIgnoreCase("Harper")) {
            harga_hotel= 150000;
        } else if (sLokasi.equalsIgnoreCase("Purwokerto") && sHotel.equalsIgnoreCase("Ladera")) {
            harga_hotel = 120000;
        } else if (sLokasi.equalsIgnoreCase("Purwokerto") && sHotel.equalsIgnoreCase("OYO")) {
            harga_hotel = 80000;
        } else if (sLokasi.equalsIgnoreCase("Purwokerto") && sHotel.equalsIgnoreCase("REDHORS")) {
            harga_hotel = 170000;
        } else if (sLokasi.equalsIgnoreCase("Yogyakarta") && sHotel.equalsIgnoreCase("Harper")) {
            harga_hotel = 180000;
        } else if (sLokasi.equalsIgnoreCase("Yogyakarta") && sHotel.equalsIgnoreCase("Ladera")) {
            harga_hotel = 190000;
        } else if (sLokasi.equalsIgnoreCase("Yogyakarta") && sHotel.equalsIgnoreCase("OYO")) {
            harga_hotel = 80000;
        } else if (sLokasi.equalsIgnoreCase("Yogyakarta") && sHotel.equalsIgnoreCase("REDHORS")) {
            harga_hotel = 180000;
        }

        jmlHarga = Integer.parseInt(String.valueOf(harga_hotel));


        jmlHarga = harga_hotel;
    }

    private void setDateTimeField() {
        etTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpTanggal.show();
            }
        });

        dpTanggal = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String[] bulan = {"Januari", "Februari", "Maret", "April", "Mei",
                        "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
                sTanggal = dayOfMonth + " " + bulan[monthOfYear] + " " + year;
                etTanggal.setText(sTanggal);

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
}
