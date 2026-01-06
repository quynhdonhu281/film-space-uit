package com.example.filmspace_mobile.ui.movie;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filmspace_mobile.R;
import com.example.filmspace_mobile.data.local.UserSessionManager;
import com.example.filmspace_mobile.data.model.movie.Movie;
import com.example.filmspace_mobile.data.model.movie.Review;
import com.example.filmspace_mobile.data.repository.RepositoryCallback;
import com.example.filmspace_mobile.data.repository.ReviewRepository;
import com.example.filmspace_mobile.ui.adapters.ReviewAdapter;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MovieReviewFragment extends Fragment {

    // --- Views ---
    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;

    // Header Views (Thống kê & Filter)
    private TextView tvAverageRating;
    private TextView tvTotalReviews;
    private Spinner spinnerFilter;

    // Footer Views (Nhập liệu)
    private LinearLayout submitReviewSection;
    private TextView loginPrompt;

    // [MỚI] Views cho Slider
    private TextView tvRatingValue;
    private Slider sliderRating;

    private EditText reviewCommentInput;
    private Button btnSubmitReview;

    // --- Data ---
    // Lưu danh sách gốc để khi filter không bị mất dữ liệu
    private List<Review> originalReviewList = new ArrayList<>();

    @Inject
    UserSessionManager sessionManager;

    @Inject
    ReviewRepository reviewRepository;

    public static MovieReviewFragment newInstance() {
        return new MovieReviewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupFilterSpinner();
        setupSubmitReview();
        loadReviews();
    }

    private void initViews(View view) {
        // RecyclerView (List)
        rvReviews = view.findViewById(R.id.rvReviews);

        // Header Section (Top)
        tvAverageRating = view.findViewById(R.id.tvAverageRating);
        tvTotalReviews = view.findViewById(R.id.tvTotalReviews);
        spinnerFilter = view.findViewById(R.id.spinnerFilter);

        // Footer Section (Bottom)
        submitReviewSection = view.findViewById(R.id.submitReviewSection);
        loginPrompt = view.findViewById(R.id.loginPrompt);

        // Input Controls
        tvRatingValue = view.findViewById(R.id.tvRatingValue);
        sliderRating = view.findViewById(R.id.sliderRating);
        reviewCommentInput = view.findViewById(R.id.reviewCommentInput);
        btnSubmitReview = view.findViewById(R.id.btnSubmitReview);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvReviews.setLayoutManager(layoutManager);
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        rvReviews.setAdapter(reviewAdapter);
    }

    private void setupFilterSpinner() {
        // Tạo danh sách các tùy chọn lọc
        String[] filters = {"Mới nhất", "Cũ nhất", "Điểm cao nhất", "Điểm thấp nhất"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, filters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupSubmitReview() {
        if (sessionManager.isLoggedIn()) {
            submitReviewSection.setVisibility(View.VISIBLE);
            loginPrompt.setVisibility(View.GONE);

            // [MỚI] Lắng nghe sự kiện kéo Slider
            sliderRating.addOnChangeListener((slider, value, fromUser) -> {
                // Cập nhật text hiển thị số điểm (VD: 8/10)
                int rating = (int) value;
                tvRatingValue.setText(rating + "/10");
            });

            btnSubmitReview.setOnClickListener(v -> submitReview());
        } else {
            submitReviewSection.setVisibility(View.GONE);
            loginPrompt.setVisibility(View.VISIBLE);

        }
    }

    private void loadReviews() {
        if (!isAdded() || getActivity() == null) return;

        MovieDetailActivity activity = (MovieDetailActivity) getActivity();
        if (activity == null || activity.getMovie() == null) return;

        Movie movie = activity.getMovie();
        List<Review> reviews = movie.getReviews();

        if (reviews != null) {
            // Sao chép dữ liệu để xử lý cục bộ
            originalReviewList = new ArrayList<>(reviews);

            // 1. Tính toán hiển thị lên Header
            updateHeaderStats(originalReviewList);

            // 2. Sắp xếp mặc định (Mới nhất)
            applyFilter(spinnerFilter.getSelectedItemPosition());
        }
    }

    /**
     * Tính toán điểm trung bình và tổng số review
     */
    private void updateHeaderStats(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            tvAverageRating.setText("0.0");
            // rbAverageDisplay.setRating(0); // <-- XÓA DÒNG NÀY
            tvTotalReviews.setText("(0 reviews)");
            return;
        }

        float totalRating = 0;
        for (Review r : reviews) {
            totalRating += r.getRating();
        }

        float average = totalRating / reviews.size();

        // Hiển thị điểm số (VD: 8.5)
        tvAverageRating.setText(String.format("%.1f", average));

        // rbAverageDisplay.setRating(average); // <-- XÓA DÒNG NÀY

        // Hiển thị tổng số lượng
        tvTotalReviews.setText("(" + reviews.size() + " reviews)");
    }
    /**
     * Logic sắp xếp danh sách
     */
    private void applyFilter(int position) {
        if (originalReviewList == null || originalReviewList.isEmpty()) return;

        List<Review> sortedList = new ArrayList<>(originalReviewList);

        switch (position) {
            case 0: // Mới nhất (ID giảm dần)
                Collections.sort(sortedList, (r1, r2) -> Integer.compare(r2.getId(), r1.getId()));
                break;
            case 1: // Cũ nhất (ID tăng dần)
                Collections.sort(sortedList, (r1, r2) -> Integer.compare(r1.getId(), r2.getId()));
                break;
            case 2: // Điểm cao nhất (Rating giảm dần)
                Collections.sort(sortedList, (r1, r2) -> Double.compare(r2.getRating(), r1.getRating()));
                break;
            case 3: // Điểm thấp nhất (Rating tăng dần)
                Collections.sort(sortedList, (r1, r2) -> Double.compare(r1.getRating(), r2.getRating()));
                break;
        }

        reviewAdapter.updateData(sortedList);
    }

    private void submitReview() {
        MovieDetailActivity activity = (MovieDetailActivity) getActivity();
        if (activity == null || activity.getMovie() == null) return;

        String comment = reviewCommentInput.getText().toString().trim();

        // [MỚI] Lấy giá trị từ Slider
        int rating = (int) sliderRating.getValue();

        // Validate
        if (comment.isEmpty()) {
            reviewCommentInput.setError("Hãy nhập nội dung đánh giá");
            reviewCommentInput.requestFocus();
            return;
        }

        // Disable nút để tránh spam
        btnSubmitReview.setEnabled(false);
        btnSubmitReview.setText("Đang gửi...");

        // Gọi API
        reviewRepository.createReview(activity.getMovie().getId(), sessionManager.getUserId(), rating, comment, new RepositoryCallback<Review>() {
            @Override
            public void onSuccess(Review review) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Snackbar.make(requireView(), "Đánh giá thành công!", Snackbar.LENGTH_SHORT).show();

                        // Reset Form
                        reviewCommentInput.setText("");

                        // [MỚI] Reset Slider về 10
                        sliderRating.setValue(10f);
                        tvRatingValue.setText("10/10");

                        btnSubmitReview.setEnabled(true);
                        btnSubmitReview.setText("Gửi đánh giá");

                        // [QUAN TRỌNG] Cập nhật UI ngay lập tức
                        originalReviewList.add(0, review); // Thêm review mới vào đầu
                        updateHeaderStats(originalReviewList); // Tính lại điểm TB
                        applyFilter(spinnerFilter.getSelectedItemPosition()); // Sort lại list
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                        btnSubmitReview.setEnabled(true);
                        btnSubmitReview.setText("Gửi đánh giá");
                    });
                }
            }
        });
    }
}