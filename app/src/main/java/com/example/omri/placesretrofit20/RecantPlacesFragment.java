package com.example.omri.placesretrofit20;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecantPlacesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecantPlacesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecantPlacesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RecantPlacesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecantPlacesFragment.
     */
    public static RecantPlacesFragment newInstance(String param1, String param2) {
        RecantPlacesFragment fragment = new RecantPlacesFragment();
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

    Context context;
    GridView gridView;
    ImageView img;
    TextView text;
    boolean exist;
    public ArrayList<PlaceHistory> placeHistories;
    String key;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recant_places, container, false);
        key = "AIzaSyC9cHqBkI3JwnRWcG1y1N7AaRe_38oT2wQ";

        context = view.getContext();
        gridView = view.findViewById(R.id.GridView);
        startPref(context);

        final ArrayAdapter<PlaceHistory> adapter = new ArrayAdapter<PlaceHistory>(context, R.layout.recant_places_item, MyDataManager.placeHistories) {
            @Override
            public int getCount() {
                return MyDataManager.placeHistories.size();
            }
            @Override
            public long getItemId(int position) {
                return 0;
            }

            // 4
            @Override
            public PlaceHistory getItem(int position) {
                return null;
            }
            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                final PlaceHistory place = MyDataManager.placeHistories.get(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.recant_places_item, null);
                }
                final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageview_place);
                final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_place);
                nameTextView.setText(place.getNameHistoryPlace());
                if (place.getPhotoPathHistoryPlace() != "") {
                    Picasso.with(view.getContext()).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=110" + "&photoreference="
                            + place.getPhotoPathHistoryPlace() + "&key=" + key).into(imageView);
                } else {
                    Picasso.with(view.getContext()).load(R.mipmap.ic_nopic).into(imageView);
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!MyDataManager.isConnected(context)) {
                            buildConnactionDialog(context).show();}
                        else {
                            Intent intent = new Intent(context, PlaceActivity.class);
                            intent.putExtra("HistoryPlace",position);
                            startActivity(intent);
                        }}
                });
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {
                        final CharSequence[] items = {MyDataManager.resources.getString(R.string.delete_all),
                                MyDataManager.resources.getString(R.string.Delete_only) +" "+MyDataManager.placeHistories.get(position).getNameHistoryPlace().toString()
                                        +" "+ MyDataManager.resources.getString(R.string.from_history), MyDataManager.resources.getString(R.string.cancel)};

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                        builder.setTitle(MyDataManager.resources.getString(R.string.select_the_action));
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item) {
                                    case 0:
                                        MyDataManager.placeHistories.clear();
                                        notifyDataSetChanged();
                                        break;
                                    case 1:
                                        MyDataManager.placeHistories.remove(position);
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
        gridView.setAdapter(adapter);
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

    public AlertDialog.Builder buildConnactionDialog(final Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
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

    private void startPref(Context c) {
        Context context = c;
        placeHistories = MyDataManager.readPlaces(context);
        if (MyDataManager.placeHistories.size() == 0) {
            MyDataManager.placeHistories.clear();
            try {
                for (int i = 0; i < placeHistories.size(); i++) {
                    MyDataManager.placeHistories.add(i, new PlaceHistory(placeHistories.get(i).getNameHistoryPlace(),
                            placeHistories.get(i).getPhotoPathHistoryPlace(), placeHistories.get(i).getPlaceId(), placeHistories.get(i).getAddress(),
                            placeHistories.get(i).getDistance(), placeHistories.get(i).getLat(), placeHistories.get(i).getLng()));
                }
            } catch (Exception e) {

            }
        }
    }

}
