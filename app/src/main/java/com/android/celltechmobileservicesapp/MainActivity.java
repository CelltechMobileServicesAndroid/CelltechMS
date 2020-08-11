package com.android.celltechmobileservicesapp;

import android.Manifest;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.WindowManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Context mContext;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        requestAdminPermision();

        //Log.d("Start" , new StartJsonModel(mContext).createJson());
        //Log.d("Start" , new OutJsonModel(mContext,"data_wipe").createJson());

    }

    private void loadHome() {
        Fragment fragment = null;
        Class fragmentClass = MainFragment.class;
        String title = "HOME";
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(title).replace(R.id.content_frame, fragment, title).commit();
        setTitle(title);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public DevicePolicyManager mDPM;
    public ComponentName mDeviceAdmin;

    private void requestAdminPermision() {
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(this, MyDeviceAdminReceiver.class);
        if (!mDPM.isAdminActive(mDeviceAdmin)) {
            Intent activateDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
            activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "");
            startActivityForResult(activateDeviceAdmin, 15);
            //startActivity(activateDeviceAdmin);
        } else {
            if (Build.VERSION.SDK_INT >= 23 && !hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            } else {
                loadHome();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
            } else {
                super.onBackPressed();
                try {
                    String title = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
                    setTitle(title);
                } catch (Exception e) {
                    setTitle("");
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 15) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= 23 && !hasPermissions(mContext, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                } else {
                    loadHome();
                }
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }

        if (requestCode == 7) {
            TestFragment fragmentObject = (TestFragment) getSupportFragmentManager().findFragmentByTag("TEST");
            fragmentObject.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ALL) {
            HashMap<String, Integer> permissionResults = new HashMap<>();
            int deniedCount = 0;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }
            if (deniedCount == 0) {
                //all permissions granted
                loadHome();
            } else {
                //at least one permission denied
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permisiuni necesare");
                builder.setCancelable(true);
                builder.setMessage("Nu ati dat accept la toate permisiunile. Mergeti in setari si acceptati-le manual. Fara ele app nu poate functiona.");
                builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = TestFragment.class;
        String fragmentTag = item.getTitle().toString();
        setTitle(fragmentTag);

        if (id == R.id.nav_wipe) {
            fragmentClass = MainFragment.class;
        } else if (id == R.id.nav_test) {
            fragmentClass = TestFragment.class;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment mFragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (mFragment == null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_right);
            transaction.addToBackStack(fragmentTag).replace(R.id.content_frame, fragment, fragmentTag).commit();
        } else {
            fragmentManager.popBackStackImmediate(fragmentClass.getSimpleName(), 0);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class MyDeviceAdminReceiver extends DeviceAdminReceiver {
        void showToast(Context context, String msg) {
            String status = "admin_receiver_status";
            //Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            //showToast(context, "admin_receiver_status_enabled");
        }

        @Override
        public CharSequence onDisableRequested(Context context, Intent intent) {
            return "admin_receiver_status_disable_warning";
        }

        @Override
        public void onDisabled(Context context, Intent intent) {
            //showToast(context, "admin_receiver_status_disabled");
        }

        @Override
        public void onPasswordChanged(Context context, Intent intent) {
            //showToast(context, "admin_receiver_status_pw_changed");
        }


    }
}
