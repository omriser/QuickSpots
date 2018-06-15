package com.example.omri.placesretrofit20;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecantSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecantSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecantSearchFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RecantSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecantSearchFragment.
     */
    public static RecantSearchFragment newInstance(String param1, String param2) {
        RecantSearchFragment fragment = new RecantSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;
    ListView recantSearches;
    LinearLayoutManager linearLayoutManager;
    Context context;
    MyrecantSearchAdapter myrecantSearchAdapter;
    TextView queryText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recant_search, container, false);
        recantSearches = view.findViewById(R.id.recant_query_list);
        context = view.getContext();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.recant_search_query_item,MyDataManager.suggestionArryList){
            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.recant_search_query_item, null);
                }
                queryText = convertView.findViewById(R.id.query_text_view);
                queryText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_history_black_24dp,0);
                final String query = getItem(position);
                queryText.setText(query.toString());
                notifyDataSetChanged();
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!MyDataManager.isConnected(context)) {
                            buildConnactionDialog(context).show();}
                        else {
                            Intent intent = new Intent(context, SearchResultActivity.class);
                            intent.putExtra("main_screen_query", query);
                            startActivity(intent);
                        }
                    }
                });


                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {
                        final CharSequence[] items = {MyDataManager.resources.getString(R.string.delete_all),MyDataManager.resources.getString(R.string.delete) +MyDataManager.suggestionArryList.get(position).toString() + getString(R.string.from_history), MyDataManager.resources.getString(R.string.cancel)};

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                        builder.setTitle(MyDataManager.resources.getString(R.string.select_the_action));
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0:
                                        MyDataManager.suggestionArryList.clear();
                                        notifyDataSetChanged();
                                        break;
                                    case 1:
                                        MyDataManager.suggestionArryList.remove(position);
                                        notifyDataSetChanged();
                                        break;
                                    case 2:
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        builder.show();
                        return true ;
                    }
                });
                return convertView;

            }

        };
        if(MyDataManager.suggestionArryList != null) {
            recantSearches.setAdapter(adapter);
        }



        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public android.support.v7.app.AlertDialog.Builder buildConnactionDialog(final Context c) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(c);
        builder.setTitle(MyDataManager.resources.getString(R.string.no_internet));
        builder.setMessage(MyDataManager.resources.getString(R.string.connaction_dialog_msg));

        builder.setPositiveButton(MyDataManager.resources.getString(R.string.try_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDataManager.isConnected(c);
            }
        });
        builder.setNegativeButton(MyDataManager.resources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder;
    }

}
