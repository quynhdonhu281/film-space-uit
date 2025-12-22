package com.example.filmspace_mobile.ui.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.filmspace_mobile.ui.main.HomeFragment.HomeFragment;
import com.example.filmspace_mobile.ui.main.ProfileFragment.ProfileFragment;
import com.example.filmspace_mobile.ui.main.SearchFragment.SearchFragment;
import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id=item.getItemId();
            if(id== R.id.home) {
                replaceFragment(new HomeFragment());
            } else if(id==R.id.search) {
                replaceFragment(new SearchFragment());
            } else if(id==R.id.favorite) {
                replaceFragment(new SearchFragment());
            } else if(id==R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}
