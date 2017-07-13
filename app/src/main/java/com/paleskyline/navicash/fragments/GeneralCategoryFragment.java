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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.model.GeneralCategory;


public class GeneralCategoryFragment extends Fragment {

    private FragmentInteractionListener fragmentListener;

    private ImageView icon;
    private EditText categoryName;
    private RadioGroup radioGroup;
    private RadioButton incomeRadioButton;
    private RadioButton expenseRadioButton;
    private Button submit;
    private String iconName = "i0";

    private GeneralCategory existingCategory;
    private boolean editing = false;
    private boolean editable = true;

    public GeneralCategoryFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        final View fragmentView = inflater.inflate(R.layout.fragment_general_category, container, false);

        icon = (ImageView) fragmentView.findViewById(R.id.select_icon);
        categoryName = (EditText) fragmentView.findViewById(R.id.general_category_name_field);
        radioGroup = (RadioGroup) fragmentView.findViewById(R.id.root_category_type);
        incomeRadioButton = (RadioButton) fragmentView.findViewById(R.id.income_category);
        expenseRadioButton = (RadioButton) fragmentView.findViewById(R.id.expense_category);
        submit = (Button) fragmentView.findViewById(R.id.submit_general_category_button);

        expenseRadioButton.setChecked(true);


        existingCategory = getActivity().getIntent().getParcelableExtra("category");

        if (existingCategory != null) {

            editing = true;
            int iconID = getActivity().getResources().getIdentifier("@drawable/" + existingCategory.getIconFile(),
                    null, getActivity().getPackageName());
            icon.setImageResource(iconID);
            categoryName.setText(existingCategory.getCategoryName());
            iconName = existingCategory.getIconFile();

            if (existingCategory.getRootCategory().equals("Income")) {
                incomeRadioButton.setSelected(true);
            } else if (existingCategory.getRootCategory().equals("Expense")) {
                expenseRadioButton.setSelected(true);
            }

            submit.setText("SAVE");
            toggleEditing();
        }

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ICON CLICKED");
                fragmentListener.onIconClicked();
            }
        });

        submit = (Button) fragmentView.findViewById(R.id.submit_general_category_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isValidCategoryName()) {
                    return;
                }

                int radioID = radioGroup.getCheckedRadioButtonId();
                View radioButton = radioGroup.findViewById(radioID);
                int radioButtonIndex = radioGroup.indexOfChild(radioButton);
                String rootCategory = "";

                if (radioButtonIndex == 0) {
                    rootCategory = "Income";
                } else if (radioButtonIndex == 1) {
                    rootCategory = "Expense";
                }

                if (editing) {
                    existingCategory.setIconFile(iconName);
                    existingCategory.setCategoryName(categoryName.getText().toString());
                    existingCategory.setRootCategory(rootCategory);
                    fragmentListener.updateGeneralCategory(existingCategory);
                } else {
                    GeneralCategory generalCategory = new GeneralCategory(
                            categoryName.getText().toString(), rootCategory, iconName);
                    fragmentListener.createGeneralCategory(generalCategory);
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
                    + " must implement FragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void updateIcon(int iconID, String iconName) {
        icon.setImageResource(iconID);
        this.iconName = iconName;
    }

    private void toggleEditing() {
        editable = !editable;
        icon.setEnabled(editable);
        categoryName.setEnabled(editable);
        radioGroup.setEnabled(editable);
    }

    private boolean isValidCategoryName() {
        // TODO: check that there isn't an existing category with the same name
        // Raise toast if fails
        return true;
    }

    public interface FragmentInteractionListener {
        void onIconClicked();
        void createGeneralCategory(GeneralCategory generalCategory);
        void updateGeneralCategory(GeneralCategory generalCategory);
        void deleteGeneralCategory(GeneralCategory generalCategory);
    }

}
