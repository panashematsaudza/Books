package com.panashematsaudza.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity  implements  SearchView.OnQueryTextListener{
private RecyclerView rvBooks;
    private ProgressBar mloadingProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mloadingProgressBar = findViewById(R.id.pb_loading);
        rvBooks = findViewById(R.id.rv_Books);
        LinearLayoutManager booksLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);;
        rvBooks.setLayoutManager(booksLayoutManager);
        Intent intent = getIntent();
        String query = intent.getStringExtra("q");

        URL bookUrl;


try {

    if (query == null || query.isEmpty()){

        bookUrl = ApiUtil.buildUrl("big data");

    }else {

         bookUrl = new URL(query);
        Log.d("resuly", query);
    }

    new  BooksQueryTask().execute(bookUrl);

}catch (Exception e){

    Log.d("error", e.getMessage());


}


    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        try{
            URL bookUrl = ApiUtil.buildUrl(s);
            new BooksQueryTask().execute(bookUrl);
            

        }catch (Exception e){

            Log.d("error", e.getMessage());

        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }


    public  class BooksQueryTask extends AsyncTask<URL ,Void ,String>{

        @Override
        protected String doInBackground(URL... urls) {

            URL searchUrl = urls[0];
            String result = null;
            try {
                result = ApiUtil.getJson(searchUrl);
            }catch (IOException e){
                Log.d("error", e.getMessage());

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            mloadingProgressBar.setVisibility(View.INVISIBLE);

            TextView tvError = findViewById(R.id.tv_error);

            if(result == null){
                tvError.setVisibility(View.VISIBLE);
                rvBooks.setVisibility(View.INVISIBLE);
            }else {

                tvError.setVisibility(View.INVISIBLE);
                rvBooks.setVisibility(View.VISIBLE);
                ArrayList<Book> books = ApiUtil.getBooksFromJson(result);
                BooksAdapter adapter =  new BooksAdapter(books);
                rvBooks.setAdapter(adapter);
            }




        }

        @Override
        protected void onPreExecute() {

            mloadingProgressBar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.book_list_menu,menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(this);

        ArrayList<String> recentList = SpUtil.getQueryList(getApplicationContext());

        int itemNum = recentList.size();
        MenuItem recentMenu;
        for (int i = 0 ; i<itemNum ; i++){
            recentMenu =  menu.add(Menu.NONE , i , Menu.NONE ,recentList.get(i));

        }
         return  true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_advanced_search:
                Intent in = new Intent(this ,SearchActivity.class);
                startActivity(in);
                return  true;

                default:

                    int position  = item.getItemId() +  1;
                    String preferenceName = SpUtil.QUERY + String.valueOf(position);
                    String query = SpUtil.getPreferenceString(getApplicationContext(), preferenceName);
                    String[] prefParams = query.split("\\,");
                    String[] queryParams = new String[4];
                    for (int i = 0 ; i<prefParams.length; i++){
                        queryParams[i] = prefParams[i];

                    }

                    URL bookUrl = ApiUtil.buildUrl(
                            (queryParams[0] == null ? "" :queryParams[0])  ,
                            (queryParams[1] == null ? "" :queryParams[1]),
                    (queryParams[2] == null ? "" :queryParams[2]),
                    (queryParams[3] == null ? "" :queryParams[3])

                    );

                    new BooksQueryTask().execute(bookUrl);
                    return super.onOptionsItemSelected(item);
        }

    }
}
