package com.panashematsaudza.books;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final EditText etAuthor = findViewById(R.id.etAuthor);
        final  EditText etPublisher = findViewById(R.id.etPublisher);
        final EditText etTitle = findViewById(R.id.etTitle);
        final EditText etIsbn  = findViewById(R.id.etIsbn);
        final Button   btnSearch = findViewById(R.id.btnSearch);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  author  = etAuthor.getText().toString().trim();
                String title = etTitle.getText().toString().trim();
                String publisher = etPublisher.getText().toString().trim();
                String isbn = etIsbn.getText().toString().trim();

                if( author.isEmpty() && title.isEmpty() && publisher.isEmpty() && isbn.isEmpty()){

                    String message = getString(R.string.no_search_data);
                    Toast.makeText(SearchActivity.this, message, Toast.LENGTH_LONG).show();

                }else {

                    URL q = ApiUtil.buildUrl(title,author,publisher,isbn);
                   String fd  = q.toString();


                   //shared pre
                    Context context = getApplicationContext();
                    int position = SpUtil.getPreferenceInt(context ,SpUtil.POSITION);
                    if (position == 0 || position == 5){
                        position = 1;
                    }else {
                        position++;
                    }

                    String key = SpUtil.QUERY + String.valueOf(position);
                    String value = title + "," + author + "," + publisher + "," + isbn;
                    SpUtil.setPreferenceString(context,key,value);
                    SpUtil.setPreferenceInt(context,SpUtil.POSITION,position);

                     Intent i = new Intent(getApplicationContext() ,BookListActivity.class);
                    i.putExtra("q" ,fd);
                    startActivity(i);

                }
            }
        });



    }
}
