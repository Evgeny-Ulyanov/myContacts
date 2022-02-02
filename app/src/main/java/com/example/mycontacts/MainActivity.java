package com.example.mycontacts;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.mycontacts.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MyContactsDatabase myContactsDatabase;
    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private ActivityMainBinding binding;
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        contactAdapter = new ContactAdapter(contactArrayList, MainActivity.this);
        recyclerView.setAdapter(contactAdapter);

        myContactsDatabase = Room.databaseBuilder(this, MyContactsDatabase.class, "ContactsDB").allowMainThreadQueries().build();

        loadContacts();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Contact contact = contactArrayList.get(viewHolder.getAdapterPosition());
                deleteContact(contact);
            }
        }).attachToRecyclerView(recyclerView);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditContact(false, null, -1);
            }
        });
    }

    public void addAndEditContact(boolean isUpdate, Contact contact, int position) {

        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.add_edit_contact, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        TextView contactTitleTextView = view.findViewById(R.id.contactTitleTextView);
        EditText firstNameEditText = view.findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = view.findViewById(R.id.lastNameEditText);
        EditText emailEditText = view.findViewById(R.id.emailEditText);
        EditText phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);

        contactTitleTextView.setText(!isUpdate ? "Добавить контакт" : "Изменить контакт");

        if (isUpdate && contact != null) {
            firstNameEditText.setText(contact.getFirstName());
            lastNameEditText.setText(contact.getLastName());
            emailEditText.setText(contact.getEmail());
            phoneNumberEditText.setText(contact.getPhoneNumber());
        }

        builder.setCancelable(false)
        .setPositiveButton(isUpdate ? "Обновить" : "Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(firstNameEditText.getText().toString())){
                    Toast.makeText(MainActivity.this, "Введите имя", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(lastNameEditText.getText().toString())){
                    Toast.makeText(MainActivity.this, "Введите фамилию", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(emailEditText.getText().toString())){
                    Toast.makeText(MainActivity.this, "Введите email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(phoneNumberEditText.getText().toString())){
                    Toast.makeText(MainActivity.this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
                } else {
                    if (isUpdate && contact != null){
                        updateContact(
                                firstNameEditText.getText().toString(),
                                lastNameEditText.getText().toString(),
                                emailEditText.getText().toString(),
                                phoneNumberEditText.getText().toString(),
                                position
                        );
                    } else {
                        addContact(firstNameEditText.getText().toString(),
                                lastNameEditText.getText().toString(),
                                emailEditText.getText().toString(),
                                phoneNumberEditText.getText().toString());
                    }
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadContacts() {

        contactArrayList = (ArrayList<Contact>) myContactsDatabase.getContactDao().getAllContact();

        contactAdapter.setContactArrayList(contactArrayList);

    }

    private void deleteContact(Contact contact){
        myContactsDatabase.getContactDao().deleteContact(contact);
        loadContacts();
    }

    private void addContact(String firstName, String lastName, String email, String phoneNumber){

        Contact contact = new Contact(
                0,
                firstName,
                lastName,
                email,
                phoneNumber
        );

        myContactsDatabase.getContactDao().insertContact(contact);
        loadContacts();
    }

    public void updateContact(String firstName, String lastName, String email, String phoneNumber, int position){

        Contact contact = contactArrayList.get(position);

        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        contact.setPhoneNumber(phoneNumber);

        myContactsDatabase.getContactDao().updateContact(contact);
        loadContacts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetAllContactsAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}