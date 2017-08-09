package com.tatait.turtleedu.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.tatait.turtleedu.BuildConfig;
import com.tatait.turtleedu.R;
import com.tencent.bugly.beta.Beta;

/**
 * AboutActivity
 * Created by Lynn on 2015/12/27.
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getFragmentManager().beginTransaction().replace(R.id.ll_fragment_container, new AboutFragment()).commit();
    }

    public static class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        private Preference mVersion;
        private Preference mUpdate;
        private Preference mShare;
        private Preference mFork;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_about);

            mVersion = findPreference("version");
            mUpdate = findPreference("update");
            mShare = findPreference("share");
            mFork = findPreference("fork");

            mVersion.setSummary("V " + BuildConfig.VERSION_NAME);
            setListener();
        }

        private void setListener() {
            mUpdate.setOnPreferenceClickListener(this);
            mShare.setOnPreferenceClickListener(this);
            mFork.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mUpdate) {
                Beta.checkUpgrade();
                return true;
            } else if (preference == mShare) {
                share();// openUrl(preference.getSummary().toString());
                return true;
            } else if (preference == mFork) {
                openUrl(preference.getSummary().toString());
                return true;
            }
            return false;
        }

        private void share() {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app, getString(R.string.app_name)));
            startActivity(Intent.createChooser(intent, getString(R.string.share)));
        }

        private void openUrl(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }
}