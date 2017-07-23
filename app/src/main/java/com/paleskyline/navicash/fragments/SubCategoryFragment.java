package com.paleskyline.navicash.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.services.DataProvider;
import com.paleskyline.navicash.services.InputService;

public class SubCategoryFragment extends Fragment {

    private FragmentInteractionListener fragmentListener;

    private ImageView icon;
    private TextView generalCategoryName;
    private EditText subCategoryName;
    private Button submit;
    private GeneralCategory generalCategory;

    public SubCategoryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        final View fragmentView = inflater.inflate(R.layout.fragment_sub_category, container, false);

        icon = (ImageView) fragmentView.findViewById(R.id.general_category_icon);
        subCategoryName = (EditText) fragmentView.findViewById(R.id.sub_category_name);

        generalCategoryName = (TextView) fragmentView.findViewById(R.id.general_category_name);
        generalCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentListener.selectGeneralCategory();
            }
        });

        submit = (Button) fragmentView.findViewById(R.id.submit_sub_category_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isValidGeneralCategory()) {
                    return;
                }

                if (!isValidSubCategory()) {
                    return;
                }

                String formattedName = InputService.capitaliseAndTrim(subCategoryName.getText().toString());
                SubCategory subCategory = new SubCategory(formattedName, generalCategory.getId());
                fragmentListener.createSubCategory(subCategory);
            }
        });

        return fragmentView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            fragmentListener = (FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AdapterInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentListener = null;
    }

    public void setGeneralCategory(GeneralCategory generalCategory) {
        this.generalCategory = generalCategory;
        generalCategoryName.setText(generalCategory.getCategoryName());
        int iconId = getActivity().getResources().getIdentifier("@drawable/" +
                generalCategory.getIconFile(), null, getActivity().getPackageName());
        icon.setImageResource(iconId);
    }

    public interface FragmentInteractionListener {
        void createSubCategory(SubCategory subCategory);
        void selectGeneralCategory();
    }

    private boolean isValidGeneralCategory() {
        if (generalCategory != null) {
            return true;
        }
        Toast.makeText(getActivity(), "Select a general category", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean isValidSubCategory() {

        System.out.println(subCategoryName.getText().toString().length());

        if (subCategoryName.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Enter a sub category name", Toast.LENGTH_SHORT).show();
            return false;
        }

        String category = InputService.capitaliseAndTrim(subCategoryName.getText().toString());

        if (DataProvider.checkDuplicateSubCategory(category)) {
            Toast.makeText(getActivity(), "Category already exists", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
