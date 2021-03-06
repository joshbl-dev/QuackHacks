package com.hackathon.quackhacks.widget;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.hackathon.quackhacks.R;
import com.hackathon.quackhacks.backend.Recipe;
import com.hackathon.quackhacks.views.BaseView;
import com.hackathon.quackhacks.views.RecipeView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class PostBox extends BaseView {

    private int layoutID;
    private String creator;
    private Recipe recipe;
    private boolean owner;
    private BaseView view;

    public PostBox(Context context) {
        super(context);
    }

    public PostBox(Context context, BaseView view, int layoutID, String creator, Recipe recipe, boolean owner) {
        super(context);

        this.layoutID = layoutID;
        this.creator = creator;
        this.recipe = recipe;
        this.owner = owner;
        this.view = view;

        reload();
    }

    @Override
    public void reload() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.post_box, null, false);

        ((TextView) layout.findViewById(R.id.timestamp_postbox)).setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(new Date(recipe.timestamp)));
        ((TextView) layout.findViewById(R.id.recipe_postbox)).setText(recipe.title);

        Button deleteBtn = layout.findViewById(R.id.deleteBtn);
        if (!owner) {
            ((TextView) layout.findViewById(R.id.usertag_postbox)).setText(creator);
            deleteBtn.setVisibility(INVISIBLE);
        } else {
            deleteBtn.setOnClickListener(onclick -> delete(creator, recipe));
        }

        layout.findViewById(R.id.shareBtn).setOnClickListener(onClick -> share());

        LinearLayout linear = activity.findViewById(layoutID);
        layout.findViewById(R.id.view_postbox).setOnClickListener(onclick -> activity.changeView(new RecipeView(activity, recipe)));
        linear.addView(layout);
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, recipe.toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        activity.startActivity(shareIntent);
    }

    private void delete(String user, Recipe recipe) {
        Map<String, Recipe> recipes = activity.getDatabase().getUser(user).getRecipes();
        recipes.remove(recipe.getTitle());
        activity.getDatabase().setValue(recipes, "users", user, "recipes");

        view.reload();
    }
}
