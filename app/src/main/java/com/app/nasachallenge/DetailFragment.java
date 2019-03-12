package com.app.nasachallenge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.nasachallenge.data.SearchItem;
import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment {

    private static final String TITLE_KEY = "TITLE_KEY";
    private static final String DESCRIPTION_KEY = "DESCRIPTION_KEY";
    private static final String IMAGE_URL_KEY = "IMAGE_URL_KEY";

    private String title;
    private String description;
    private String imageUrl;

    public static Bundle getArguments(SearchItem searchItem) {
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_KEY, searchItem.getTitle());
        bundle.putString(DESCRIPTION_KEY, searchItem.getDescription());
        bundle.putString(IMAGE_URL_KEY, searchItem.getImageUrl());
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
        Bundle bundle = getArguments();

        if (bundle != null) {
            populateDetailInfo(bundle);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.detail_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = view.findViewById(R.id.title);
        TextView descriptionView = view.findViewById(R.id.description);
        ImageView imageView = view.findViewById(R.id.thumbnail);

        titleView.setText(title);
        descriptionView.setText(description);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.with(view.getContext())
                    .load(imageUrl)
                    .into(imageView);
        }
    }

    private void populateDetailInfo(Bundle bundle) {
        title = bundle.getString(TITLE_KEY);
        description = bundle.getString(DESCRIPTION_KEY);
        imageUrl = bundle.getString(IMAGE_URL_KEY);
    }
}
