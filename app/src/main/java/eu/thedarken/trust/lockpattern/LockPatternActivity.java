/*
 *   Copyright 2012 Hai Bison
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */


package eu.thedarken.trust.lockpattern;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import eu.thedarken.trust.R;
import eu.thedarken.trust.lockpattern.LockPatternView.Cell;
import eu.thedarken.trust.lockpattern.LockPatternView.DisplayMode;

public class LockPatternActivity extends Activity {

    public static final String MODE = LPMode.class.getName();

    public static enum LPMode {
        CreatePattern,
        ComparePattern
    }

    
    public static final String PREFERENCE_FILE = "LockPatternPreferences";
    public static final String AutoSave = "auto_save";
    public static final String KEY = "general.protection.key";
    public static final int REQUEST_PATTERN_CHECK = 10;
    public static final int REQUEST_PATTERN_CREATION = 11;
    private SharedPreferences fPrefs;
    private LPMode fMode;
    private int fMaxRetry;
    private boolean fAutoSave;

    private TextView fTxtInfo;
    private LockPatternView fLockPatternView;
    private View fFooter;
    private Button fBtnCancel;
    private Button fBtnConfirm;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_pattern_activity);

        fPrefs = getSharedPreferences(PREFERENCE_FILE, 0);

        fMode = (LPMode) getIntent().getSerializableExtra(MODE);
        if (fMode == null)
            fMode = LPMode.CreatePattern;

        fMaxRetry = 3;
        fAutoSave = getIntent().getBooleanExtra(AutoSave, true);

        fTxtInfo = (TextView) findViewById(R.id.lpa_text_info);
        fLockPatternView = (LockPatternView) findViewById(R.id.lpa_lockPattern);

        fFooter = findViewById(R.id.lpa_layout_footer);
        fBtnCancel = (Button) findViewById(R.id.lpa_button_cancel);
        fBtnConfirm = (Button) findViewById(R.id.lpa_button_confirm);

        init();
    }// onCreate()

    private void init() {
        // haptic feedback
        boolean hapticFeedbackEnabled = false;
        try {
            hapticFeedbackEnabled = Settings.System.getInt(getContentResolver(),
                    Settings.System.HAPTIC_FEEDBACK_ENABLED, 0) != 0;
        } catch (Throwable t) {
            // ignore it
        }
        fLockPatternView.setTactileFeedbackEnabled(hapticFeedbackEnabled);

        fLockPatternView.setOnPatternListener(fPatternViewListener);

        switch (fMode) {
        case CreatePattern:
            fBtnCancel.setOnClickListener(fBtnCancelOnClickListener);
            fBtnConfirm.setOnClickListener(fBtnConfirmOnClickListener);

            fFooter.setVisibility(View.VISIBLE);
            fTxtInfo.setText(R.string.msg_draw_an_unlock_pattern);
            break;
        case ComparePattern:
            fFooter.setVisibility(View.GONE);
            fTxtInfo.setText(R.string.msg_draw_pattern_to_unlock);
            break;
        }

        setResult(RESULT_CANCELED);
    }// init()

    private int retryCount = 0;

    private void doComparePattern(String pattern) {
        if (pattern == null)
            return;

        String currentPattern = getIntent().getStringExtra(KEY);
        if (currentPattern == null)
            currentPattern = fPrefs.getString(KEY, null);

        if (pattern.equals(currentPattern)) {
            setResult(RESULT_OK);
            finish();
        } else {
            retryCount++;

            if (retryCount >= fMaxRetry) {
                setResult(RESULT_CANCELED);
                finish();
            } else {
                fLockPatternView.setDisplayMode(DisplayMode.Wrong);
                fTxtInfo.setText(R.string.msg_try_again);
            }
        }
    }// doComparePattern()

    private String lastPattern;

    private void doCreatePattern(List<Cell> pattern) {
        if (pattern.size() < 4) {
            fLockPatternView.setDisplayMode(DisplayMode.Wrong);
            fTxtInfo.setText(R.string.msg_connect_4dots);
            return;
        }

        if (lastPattern == null) {
            lastPattern = LockPatternUtils.patternToSha1(pattern);
            fTxtInfo.setText(R.string.msg_pattern_recorded);
            fBtnConfirm.setEnabled(true);
        } else {
            if (lastPattern.equals(LockPatternUtils.patternToSha1(pattern))) {
                fTxtInfo.setText(R.string.msg_your_new_unlock_pattern);
                fBtnConfirm.setEnabled(true);
            } else {
                fTxtInfo.setText(R.string.msg_redraw_pattern_to_confirm);
                fBtnConfirm.setEnabled(false);
                fLockPatternView.setDisplayMode(DisplayMode.Wrong);
            }
        }
    }// doCreatePattern()

    private final LockPatternView.OnPatternListener fPatternViewListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            fLockPatternView.setDisplayMode(DisplayMode.Correct);

            if (fMode == LPMode.CreatePattern) {
                fTxtInfo.setText(R.string.msg_release_finger_when_done);
                fBtnConfirm.setEnabled(false);
                if (getString(R.string.cmd_continue).equals(fBtnConfirm.getText()))
                    lastPattern = null;
            }
        }

        @Override
        public void onPatternDetected(List<Cell> pattern) {
            switch (fMode) {
            case CreatePattern:
                doCreatePattern(pattern);
                break;
            case ComparePattern:
                doComparePattern(LockPatternUtils.patternToSha1(pattern));
                break;
            }
        }// onPatternDetected()

        @Override
        public void onPatternCleared() {
            fLockPatternView.setDisplayMode(DisplayMode.Correct);

            switch (fMode) {
            case CreatePattern:
                fBtnConfirm.setEnabled(false);
                if (getString(R.string.cmd_continue).equals(fBtnConfirm.getText())) {
                    lastPattern = null;
                    fTxtInfo.setText(R.string.msg_draw_an_unlock_pattern);
                } else
                    fTxtInfo.setText(R.string.msg_redraw_pattern_to_confirm);
                break;
            case ComparePattern:
                fTxtInfo.setText(R.string.msg_draw_pattern_to_unlock);
                break;
            }
        }// onPatternCleared()

        @Override
        public void onPatternCellAdded(List<Cell> pattern) {
            // TODO Auto-generated method stub
        }
    };// fPatternViewListener

    private final View.OnClickListener fBtnCancelOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            setResult(RESULT_CANCELED);
            finish();
        }
    };// fBtnCancelOnClickListener

    private final View.OnClickListener fBtnConfirmOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (getString(R.string.cmd_continue).equals(fBtnConfirm.getText())) {
                fLockPatternView.clearPattern();
                fTxtInfo.setText(R.string.msg_redraw_pattern_to_confirm);
                fBtnConfirm.setText(R.string.cmd_confirm);
                fBtnConfirm.setEnabled(false);
            } else {
                if (fAutoSave)
                    fPrefs.edit().putString(KEY, lastPattern).commit();

                Intent i = new Intent();
                i.putExtra(KEY, lastPattern);
                setResult(RESULT_OK, i);
                finish();
            }
        }
    };// fBtnConfirmOnClickListener
}
