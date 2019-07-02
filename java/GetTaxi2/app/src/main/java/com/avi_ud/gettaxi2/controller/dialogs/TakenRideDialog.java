package com.avi_ud.gettaxi2.controller.dialogs;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avi_ud.gettaxi2.R;
import com.avi_ud.gettaxi2.model.backend.BackEndFactory;
import com.avi_ud.gettaxi2.model.backend.IAction;
import com.avi_ud.gettaxi2.model.entities.Ride;
import com.avi_ud.gettaxi2.utils.Alerter;
import com.avi_ud.gettaxi2.utils.Communication;

import java.util.ArrayList;
import java.util.Objects;

public class TakenRideDialog extends BottomSheetDialogFragment{
    final private int PERMISSION_REQUEST_CODE_WRITE_CONTACTS = 2;
    static boolean hasPermission = false;
    boolean isRideTaken = false;
    private Ride mRide;
    private float rideLength;
    public TakenRideDialog(){}

    public static TakenRideDialog newInstance(Ride ride,float rideLength) {
        TakenRideDialog d = new TakenRideDialog();
        d.mRide = ride;
        d.rideLength = rideLength;
        return d;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.taken_ride_details, container,
                false);

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView phone = (TextView) view.findViewById(R.id.phone);
        TextView source = (TextView) view.findViewById(R.id.from);
        TextView target = (TextView) view.findViewById(R.id.to);
        TextView datetime = (TextView) view.findViewById(R.id.datetime);
        TextView comments = (TextView) view.findViewById(R.id.comments);
        TextView length = (TextView) view.findViewById(R.id.ride_length);
        final FloatingActionButton fab = view.findViewById(R.id.fab);
        ImageButton emailBottom = (ImageButton) view.findViewById(R.id.email_button);
        ImageButton smsBottom = (ImageButton) view.findViewById(R.id.sms_button);
        ImageButton callBottom = (ImageButton) view.findViewById(R.id.phone_button);
        ImageButton addPersonBottom = (ImageButton) view.findViewById(R.id.save_number_button);
        final ProgressBar fabPg =(ProgressBar) view.findViewById(R.id.fabProgress);

        name.setText(String.format("%s %s", mRide.getPassenger().getFirstName(),
                mRide.getPassenger().getLastName()));
        phone.setText(mRide.getPassenger().getPhoneNumber());
        source.setText(mRide.getStartLocation().getAddress());
        target.setText(mRide.getEndLocation().getAddress());
        datetime.setText(mRide.getTimeStart());
        comments.setText(mRide.getComments());
        length.setText(String.format("%.1f",rideLength ) + " km");
        emailBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Communication.sendEmail(mRide.getPassenger().getEmail(),getActivity());
            }});
        smsBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Communication.sendSms(mRide.getPassenger().getPhoneNumber(),getActivity());
            }});
        callBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Communication.call(mRide.getPassenger().getPhoneNumber(),getActivity());
            }});
        addPersonBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasPermission ) {
                    if(getPermission())
                        saveContact();
                }else{
                    saveContact();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRideTaken){
                    BackEndFactory.backEnd.cancelRide(mRide.getKey(),
                            new IAction<Boolean>() {
                                @Override
                                public void onSuccess(Boolean obj) {
                                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_done_24px));
                                    isRideTaken = false;
                                }

                                @Override
                                public void onFailure(Exception exception) {

                                }

                                @Override
                                public void onPreExecute() {
                                    fabPg.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onPostExecute() {
                                    fabPg.setVisibility(View.GONE);
                                }
                            });
                }else {
                    BackEndFactory.backEnd.takeRide(mRide.getKey(),
                            new IAction<Boolean>() {
                                @Override
                                public void onSuccess(Boolean obj) {
                                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_close_24px));
                                    isRideTaken = true;
                                }

                                @Override
                                public void onFailure(Exception exception) {
                                    Alerter.showErrorDialog(getContext(),exception.getMessage());
                                }

                                @Override
                                public void onPreExecute() {
                                    fabPg.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onPostExecute() {
                                    fabPg.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });
        return view;

    }
    private boolean getPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()).getApplicationContext(),
                android.Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
            return true;
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    PERMISSION_REQUEST_CODE_WRITE_CONTACTS);
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_WRITE_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    hasPermission = true;
                    saveContact();
                } else {
                    // permission denied, boo!
                }
                return;
            }
        }
    }

    private void saveContact(){
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        //INSERT NAME
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        String.format("%s %s", mRide.getPassenger().getFirstName(),
                        mRide.getPassenger().getLastName())) // Name of the person
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, mRide.getPassenger().getLastName()) // Name of the person
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, mRide.getPassenger().getFirstName()) // Name of the person
                .build());
        //INSERT MOBILE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mRide.getPassenger().getPhoneNumber()) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        //INSERT EMAIL
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, mRide.getPassenger().getEmail())
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());
        //PUSH EVERYTHING TO CONTACTS
        try
        {
            ContentProviderResult[] res = getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (res != null && res[0] != null) {
                Toast.makeText(getActivity(), "New contact was created", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(getActivity(), "Error while adding new contact", Toast.LENGTH_SHORT).show();

        }

    }
}
