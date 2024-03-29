package io.accroo.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import io.accroo.android.R;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.SubCategory;
import io.accroo.android.other.Utils;
import io.accroo.android.services.DataProvider;

public class SubCategoryFragment extends Fragment {

    private FragmentInteractionListener fragmentListener;
    private ImageView icon;
    private TextView generalCategoryName;
    private EditText subCategoryName;
    private Button submit;
    private SubCategory existingCategory;
    private GeneralCategory generalCategory;
    private boolean editing = false;

    public SubCategoryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        final View fragmentView = inflater.inflate(R.layout.fragment_sub_category, container, false);

        icon = fragmentView.findViewById(R.id.general_category_icon);
        subCategoryName = fragmentView.findViewById(R.id.sub_category_name);
        generalCategoryName = fragmentView.findViewById(R.id.general_category_name);
        submit = fragmentView.findViewById(R.id.submit_sub_category_button);

        existingCategory = requireActivity().getIntent().getParcelableExtra("subCategory");

        if (existingCategory != null) {
            editing = true;
            generalCategory = ((GeneralCategory) existingCategory.getParent());
            int iconID = requireActivity().getResources().getIdentifier("@drawable/" +
                            generalCategory.getIconFile(), null, requireActivity().getPackageName());
            icon.setImageResource(iconID);
            icon.setFocusableInTouchMode(true);
            icon.requestFocus();
            generalCategoryName.setText(generalCategory.getCategoryName());
            subCategoryName.setText(existingCategory.getCategoryName());
        } else {
            subCategoryName.setFocusableInTouchMode(true);
            subCategoryName.requestFocus();
            Utils.showSoftKeyboard(requireActivity());
        }

        icon.setOnClickListener(view -> fragmentListener.selectGeneralCategory());

        generalCategoryName.setOnClickListener(view -> fragmentListener.selectGeneralCategory());

        submit.setOnClickListener(view -> {
            if (!isGeneralCategorySelected()) {
                return;
            }

            if (!isValidSubCategory()) {
                return;
            }

            String formattedName = Utils.capitaliseAndTrim(subCategoryName.getText().toString());

            if (editing) {
                existingCategory.setCategoryName(formattedName);
                existingCategory.setGeneralCategoryId(generalCategory.getId());
                fragmentListener.updateSubCategory(existingCategory);
            } else {
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
        int iconId = requireActivity().getResources().getIdentifier("@drawable/" +
                generalCategory.getIconFile(), null, requireActivity().getPackageName());
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
        Toast.makeText(requireActivity(), R.string.select_general_category, Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean isValidSubCategory() {
        if (subCategoryName.getText().toString().length() == 0) {
            Toast.makeText(requireActivity(), R.string.enter_category_name, Toast.LENGTH_SHORT).show();
            return false;
        }

        String category = Utils.capitaliseAndTrim(subCategoryName.getText().toString());

        if (!editing) {
            if (DataProvider.checkDuplicateSubCategory(category)) {
                Toast.makeText(requireActivity(), R.string.category_exists, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    public void clearFields() {
        subCategoryName.setText("");
    }

}
