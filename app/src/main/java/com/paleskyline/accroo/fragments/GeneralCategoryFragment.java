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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.paleskyline.accroo.R;
import com.paleskyline.accroo.model.GeneralCategory;
import com.paleskyline.accroo.services.DataProvider;
import com.paleskyline.accroo.services.InputService;


public class GeneralCategoryFragment extends Fragment {

    private FragmentInteractionListener fragmentListener;
    private ImageView icon;
    private EditText categoryName;
    private RadioGroup radioGroup;
    private RadioButton incomeRadioButton;
    private RadioButton expenseRadioButton;
    private Button submit;
    private String iconName = "i0";
    private final static String DEFAULT_ICON = "i0";
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

        existingCategory = getActivity().getIntent().getParcelableExtra("generalCategory");

        if (existingCategory != null) {

            editing = true;
            int iconID = getActivity().getResources().getIdentifier("@drawable/" + existingCategory.getIconFile(),
                    null, getActivity().getPackageName());
            icon.setImageResource(iconID);
            categoryName.setText(existingCategory.getCategoryName());
            iconName = existingCategory.getIconFile();

            if (existingCategory.getRootCategory().equals("Income")) {
                incomeRadioButton.setChecked(true);
            } else if (existingCategory.getRootCategory().equals("Expense")) {
                expenseRadioButton.setChecked(true);
            }

            submit.setText(getResources().getString(R.string.save));
        }

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentListener.onIconClicked();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isValidCategoryName()) {
                    return;
                }

                if (!isIconSelected()) {
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

                String formattedName = InputService.capitaliseAndTrim(categoryName.getText().toString());

                if (editing) {
                    existingCategory.setIconFile(iconName);
                    existingCategory.setCategoryName(formattedName);
                    existingCategory.setRootCategory(rootCategory);
                    fragmentListener.updateGeneralCategory(existingCategory);
                } else {
                    GeneralCategory generalCategory = new GeneralCategory(formattedName,
                            rootCategory, iconName);
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
                    + " must implement AdapterInteractionListener");
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

    public void toggleEditing() {
        editable = !editable;
        icon.setEnabled(editable);
        categoryName.setEnabled(editable);
        radioGroup.setEnabled(editable);
        incomeRadioButton.setEnabled(editable);
        expenseRadioButton.setEnabled(editable);
        submit.setEnabled(editable);
    }

    private boolean isValidCategoryName() {
        String categoryString = categoryName.getText().toString();
        if (categoryString.length() > 0) {
            if (!editing) {
                String category = InputService.capitaliseAndTrim(categoryName.getText().toString());
                if (DataProvider.checkDuplicateGeneralCategory(category)) {
                    Toast.makeText(getActivity(), R.string.general_category_already_exists, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        }
        Toast.makeText(getActivity(), R.string.enter_category_name, Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean isIconSelected() {
        if (iconName.equals(DEFAULT_ICON)) {
            Toast.makeText(getActivity(), R.string.select_category_icon, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public interface FragmentInteractionListener {
        void onIconClicked();
        void createGeneralCategory(GeneralCategory generalCategory);
        void updateGeneralCategory(GeneralCategory generalCategory);
    }

}
