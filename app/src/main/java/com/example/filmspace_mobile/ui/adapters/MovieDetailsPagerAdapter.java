package com.example.filmspace_mobile.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.ui.movie.MovieAboutFragment;
import com.example.filmspace_mobile.ui.movie.MovieEpisodeFragment;
import com.example.filmspace_mobile.ui.movie.MovieReviewFragment;

public class MovieDetailsPagerAdapter extends FragmentStateAdapter {

    private Movie movie;

    public MovieDetailsPagerAdapter(@NonNull FragmentActivity fragmentActivity, Movie movie) {
        super(fragmentActivity);
        this.movie = movie;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                MovieAboutFragment aboutFragment = MovieAboutFragment.newInstance(movie);
                aboutFragment.setMovie(movie);
                return aboutFragment;
            case 1:
                return MovieEpisodeFragment.newInstance();
            case 2:
                return MovieReviewFragment.newInstance();
            default:
                return MovieAboutFragment.newInstance(movie);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Episodes, About, Review
    }
}