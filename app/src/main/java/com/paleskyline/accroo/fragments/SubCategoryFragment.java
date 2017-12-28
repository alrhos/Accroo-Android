package com.paleskyline.accroo.fragments;

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

import com.paleskyline.accroo.R;
import com.paleskyline.accroo.model.GeneralCategory;
import com.paleskyline.accroo.model.SubCategory;
import com.paleskyline.accroo.services.DataProvider;
import com.paleskyline.accroo.services.InputService;

public class SubCategoryFragment extends Fragment {

    private FragmentInteractionListener fragmentListener;
    private ImageView icon;
    private TextView generalCategoryName;
    private EditText subCategoryName;
    private Button submit;
    private SubCategory existingCategory;
    private GeneralCategory generalCategory;
    private boolean editing = false;
    private boolean editable = true;

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

        icon = fragmentView.findViewById(R.id.general_category_icon);
        subCategoryName = fragmentView.findViewById(R.id.sub_category_name);
        generalCategoryName = fragmentView.findViewById(R.id.general_category_name);
        submit = fragmentView.findViewById(R.id.submit_sub_category_button);

        existingCategory = getActivity().getIntent().getParcelableExtra("subCategory");

        if (existingCategory != null) {

            editing = true;
            generalCategory = ((GeneralCategory) existingCategory.getParent());
            int iconID = getActivity().getResources().getIdentifier("@drawable/" +
                            generalCategory.getIconFile(), null, getActivity().getPackageName());
            icon.setImageResource(iconID);
            generalCategoryName.setText(generalCategory.getCategoryName());
            subCategoryName.setText(existingCategory.getCategoryName());
            submit.setText(getResources().getString(R.string.save));

        }

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentListener.selectGeneralCategory();
            }
        });

        generalCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentListener.selectGeneralCategory();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isGeneralCategorySelected()) {
                    return;
                }

                if (!isValidSubCategory()) {
                    return;
                }

                String formattedName = InputService.capitaliseAndTrim(subCategoryName.getText().toString());

                if (editing) {
                    existingCategory.setCategoryName(formattedName);
                    existingCategory.setGeneralCategoryID(generalCategory.getId());
                    fragmentListener.updateSubCategory(existingCategory);
                } else {
                    System.out.println(generalCategory.toString());
                    SubCategory subCategory = new SubCategory(formattedName, generalCategory.getId());
                    System.out.println(subCategory.toString());
                    fragmentListener.createSubCategory(subCategory);
                }
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
        void updateSubCategory(SubCategory subCategory);
        void selectGeneralCategory();
    }

    private boolean isGeneralCategorySelected() {
        if (generalCategory != null) {
            return true;
        }
        Toast.makeText(getActivity(), R.string.select_general_category, Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean isValidSubCategory() {
        if (subCategoryName.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), R.string.enter_category_name, Toast.LENGTH_SHORT).show();
            return false;
        }

        String category = InputService.capitaliseAndTrim(subCategoryName.getText().toString());

        if (!editing) {
            if (DataProvider.checkDuplicateSubCategory(category)) {
                Toast.makeText(getActivity(), R.string.category_exists, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    public void toggleEditing() {
        editable = !editable;
        icon.setEnabled(editable);
        subCategoryName.setEnabled(editable);
        generalCategoryName.setEnabled(editable);
        submit.setEnabled(editable);
    }

    public void clearFields() {
        subCategoryName.setText("");
    }

}
