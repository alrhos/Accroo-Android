package com.paleskyline.navicash.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.adapters.CategoryOverviewAdapter;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.other.DividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoryOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryOverviewFragment extends Fragment implements CategoryOverviewAdapter.AdapterInteractionListener {

    private CategoryOverviewAdapter categoryOverviewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FragmentInteractionListener fragmentListener;

    public CategoryOverviewFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryOverviewFragment newInstance(String param1, String param2) {
        CategoryOverviewFragment fragment = new CategoryOverviewFragment();
        /*
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        */
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryOverviewAdapter = new CategoryOverviewAdapter(getActivity(), this);
        /*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_category_overview, container, false);
        RecyclerView rv = (RecyclerView) fragmentView.findViewById(R.id.category_overview_recycler_view);
        rv.addItemDecoration(new DividerItemDecoration(fragmentView.getContext()));
        rv.setHasFixedSize(true);
        rv.setAdapter(categoryOverviewAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.category_overview_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fragmentListener.onCategorySwipeRefresh();
            }
        });

        return fragmentView;

        //return inflater.inflate(R.layout.fragment_category_overview, container, false);
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (fragmentListener != null) {
//            fragmentListener.onFragmentInteraction(uri);
//        }
//    }

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("CATEGORY FRAG - ON ACTIVITY CREATED");
        if (savedInstanceState != null) {

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        System.out.println("CATEGORY FRAG - saving instance");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface FragmentInteractionListener {
        void onGeneralCategoryClicked(GeneralCategory generalCategory);
        void onSubCategoryClicked(SubCategory subCategory);
        void onCategorySwipeRefresh();
    }

    @Override
    public void onGeneralCategoryClicked(GeneralCategory generalCategory) {
        fragmentListener.onGeneralCategoryClicked(generalCategory);
    }

    @Override
    public void onSubCategoryClicked(SubCategory subCategory) {
        fragmentListener.onSubCategoryClicked(subCategory);
    }

    public void refreshAdapter() {
        categoryOverviewAdapter.refreshDataSource();
    }

    public void setRefreshStatus(boolean status) {
        swipeRefreshLayout.setRefreshing(status);
    }

}
