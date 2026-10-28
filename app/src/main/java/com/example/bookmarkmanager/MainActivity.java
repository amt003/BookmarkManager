
package com.example.bookmarkmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    EditText etTitle, etUrl;
    Button btnSave;
    ListView listBookmarks;

    DatabaseHelper dbHelper;

    ArrayList<String> bookmarkList;
    ArrayList<Integer> bookmarkIds;
    ArrayAdapter<String> adapter;

    int selectedId = -1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTitle = findViewById(R.id.etTitle);
        etUrl = findViewById(R.id.etUrl);
        btnSave = findViewById(R.id.btnSave);
        listBookmarks = findViewById(R.id.listBookmarks);

        dbHelper = new com.example.bookmarkmanager.DatabaseHelper(this);

        bookmarkList = new ArrayList<>();
        bookmarkIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                bookmarkList
        );

        listBookmarks.setAdapter(adapter);

        loadBookmarks();

        btnSave.setOnClickListener(v -> saveBookmark());


        listBookmarks.setOnItemClickListener((parent, view,
                                              position, id) -> {
            selectedId = bookmarkIds.get(position);

            String item = bookmarkList.get(position);
            String[] parts = item.split("\n", 2);

            etTitle.setText(parts[0]);
            etUrl.setText(parts.length > 1 ? parts[1] : "");

            btnSave.setText("Update Bookmark");
        });


        listBookmarks.setOnItemLongClickListener(
                (parent, view, position, id) -> {
                    int bookmarkId = bookmarkIds.get(position);

                    new AlertDialog.Builder(this)
                            .setTitle("Delete Bookmark")
                            .setMessage("Are you sure you want to delete it?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                dbHelper.deleteBookmark(bookmarkId);
                                resetForm();
                                loadBookmarks();
                                Toast.makeText(this, "Bookmark deleted",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();

                    return true;
                });
    }

    private void saveBookmark() {
        String title = etTitle.getText().toString().trim();
        String url = etUrl.getText().toString().trim();

        if (title.isEmpty() || url.isEmpty()) {
            Toast.makeText(this, "Enter title and URL", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedId == -1) {
            long result = dbHelper.addBookmark(title, url);

            if (result != -1) {
                Toast.makeText(this, "Bookmark added",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            dbHelper.updateBookmark(selectedId, title, url);

            Toast.makeText(this, "Bookmark updated",
                    Toast.LENGTH_SHORT).show();
        }

        resetForm();
        loadBookmarks();
    }

    private void loadBookmarks() {
        bookmarkList.clear();
        bookmarkIds.clear();

        try (Cursor cursor = dbHelper.getAllBookmarks()) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                String title = cursor.getString(
                        cursor.getColumnIndexOrThrow("title"));

                String url = cursor.getString(
                        cursor.getColumnIndexOrThrow("url"));

                bookmarkIds.add(id);
                bookmarkList.add(title + "\n" + url);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void resetForm() {
        etTitle.setText("");
        etUrl.setText("");
        selectedId = -1;
        btnSave.setText("Add Bookmark");
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
