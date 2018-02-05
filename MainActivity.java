package com.example.android.myquizz;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    int questionCounter = -1;
    int score = 0;
    boolean theEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Animate and make clickable intro message
        introAnim();
    }

    //End programm by directing to relevant video
    public void watchYoutubeVideo() {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:hH_5wEcQ2Rw"));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=hH_5wEcQ2Rw"));

        if ((appIntent.resolveActivity(getPackageManager()) != null))
            startActivity(appIntent);
        else
            startActivity(webIntent);
    }

    //Quizz is triggered by main button
    public void questions(View view) {
        questionCounter++;
        int type = 0;
        //When question counter exceeds number of available questions
        if (theEnd == true) {
            watchYoutubeVideo();
            finish();
        }

        Resources res = getResources();
        String[] allQuestions = res.getStringArray(R.array.allquestions);
        String[] allAnswers = res.getStringArray(R.array.allanswers);
        String[] allTypes = res.getStringArray(R.array.questionType);

        //Questions - answers association
        if (questionCounter < allQuestions.length) {
            String question = "";
            String answer1 = "";
            String answer2 = "";
            String answer3 = "";
            type = Integer.valueOf(allTypes[questionCounter]);
            question = allQuestions[questionCounter];
            if (type == 1 || type == 2) {

                answer1 = allAnswers[(questionCounter + 1) * 3 - 3];
                answer2 = allAnswers[(questionCounter + 1) * 3 - 2];
                answer3 = allAnswers[(questionCounter + 1) * 3 - 1];
            }

            //Sends information for all views to be displayed
            updateDisplay(question, answer1, answer2, answer3, "", "NEXT", type);
        }
        //Sends final result message to be displayed marks end of quizz
        if (questionCounter == allQuestions.length) {
            updateDisplay(showResult(), "", "", "", "", "EXIT", type);

            theEnd = true;

            //Fade out background image
            ImageView img = findViewById(R.id.background);
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(2000);

            img.startAnimation(fadeOut);
            img.setVisibility(View.INVISIBLE);
        }
    }

    //Generates final result message based on score
    public String showResult() {
        Resources resources = getResources();
        String[] resultMessages = resources.getStringArray(R.array.resultMessages);
        String resultMessage = "";

        if (score == 0)
            resultMessage = resultMessages[0];
        if (score == 1)
            resultMessage = resultMessages[1];
        if (score > 1)
            resultMessage = resultMessages[2].replace("YOURSCORE", String.valueOf(score));

        return resultMessage;
    }

    //Read chosen answer and compare to correct answer, disable radio buttons accordingly
    public void CheckAnswer(View view) {
        RadioGroup radioGroup2 = findViewById(R.id.radioGroup2);
        RadioButton radioButton1 = findViewById(R.id.answer1);
        RadioButton radioButton2 = findViewById(R.id.answer2);
        RadioButton radioButton3 = findViewById(R.id.answer3);
        CheckBox check1 = findViewById(R.id.check1);
        CheckBox check2 = findViewById(R.id.check2);
        CheckBox check3 = findViewById(R.id.check3);
        EditText editBox = findViewById(R.id.editBox);

        String note = "";
        Resources resources = getResources();
        int type = 0;
        type = Integer.valueOf(resources.getStringArray(R.array.questionType)[questionCounter]);
        String[] correctAnswers = resources.getStringArray(R.array.correctAnswers);
        String[] explanations = resources.getStringArray(R.array.explanations);
        TextView text5 = findViewById(R.id.text5);

        if (type == 1) {
            int correctAnswer = Integer.parseInt(correctAnswers[questionCounter]);
            int actualAnswer = 0;
            radioButton1.setEnabled(false);
            radioButton2.setEnabled(false);
            radioButton3.setEnabled(false);
            switch (view.getId()) {
                case (R.id.answer1):
                    actualAnswer = Integer.parseInt("1");
                    break;
                case (R.id.answer2):
                    actualAnswer = Integer.parseInt("2");
                    break;
                case (R.id.answer3):
                    actualAnswer = Integer.parseInt("3");
                    break;
            }
            if (actualAnswer == correctAnswer) {
                note = "CORRECT - " + explanations[questionCounter];
                score++;
            } else {
                note = "INCORRECT - " + explanations[questionCounter];
            }
        }
        if (type == 2) {
            int correctAnswer = Integer.parseInt(correctAnswers[questionCounter]);

            check1.setEnabled(false);
            check2.setEnabled(false);
            check3.setEnabled(false);

            boolean actualAnswer1 = false;
            boolean actualAnswer2 = false;
            boolean actualAnswer3 = false;
            boolean correctAnswer1 = false;
            boolean correctAnswer2 = false;
            boolean correctAnswer3 = false;

            int bit1 = Integer.valueOf(String.valueOf(correctAnswer).substring(0, 1));
            int bit2 = Integer.valueOf(String.valueOf(correctAnswer).substring(1, 2));
            int bit3 = Integer.valueOf(String.valueOf(correctAnswer).substring(2, 3));

            if (bit1 == 1)
                correctAnswer1 = true;
            if (bit2 == 1)
                correctAnswer2 = true;
            if (bit3 == 1)
                correctAnswer3 = true;

            if (check1.isChecked() == true)
                actualAnswer1 = true;
            if (check2.isChecked() == true)
                actualAnswer2 = true;
            if (check3.isChecked() == true)
                actualAnswer3 = true;

            if (correctAnswer1 == actualAnswer1 && correctAnswer2 == actualAnswer2 && correctAnswer3 == actualAnswer3) {
                note = "CORRECT - " + explanations[questionCounter];
                score++;
            } else
                note = "INCORRECT - " + explanations[questionCounter];

        }
        if (type == 3) {
            String userAnswer = editBox.getText().toString();
            String correctAnswerLiteral = correctAnswers[questionCounter];
            editBox.setEnabled(false);
            if (userAnswer.toLowerCase().equals(correctAnswerLiteral.toLowerCase())) {
                note = "CORRECT - " + explanations[questionCounter];
                score++;
            } else
                note = "INCORRECT - " + explanations[questionCounter];
        }

        //Display explanation
        text5.setText(note);
    }

    //Controls visibility and values of all views
    public void updateDisplay(String text1value, String text2value, String text3value, String text4value, String text5value, String text6value, int type) {
        TextView text1 = findViewById(R.id.text1);
        text1.setEnabled(false);
        text1.setVisibility(View.VISIBLE);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton radioButton1 = findViewById(R.id.answer1);
        RadioButton radioButton2 = findViewById(R.id.answer2);
        RadioButton radioButton3 = findViewById(R.id.answer3);

        RadioGroup radioGroup2 = findViewById(R.id.radioGroup2);
        CheckBox check1 = findViewById(R.id.check1);
        CheckBox check2 = findViewById(R.id.check2);
        CheckBox check3 = findViewById(R.id.check3);

        RadioGroup radioGroup3 = findViewById(R.id.radioGroup3);
        EditText editBox = findViewById(R.id.editBox);

        TextView text5 = findViewById(R.id.text5);
        Button button = findViewById(R.id.button);
        button.setText(text6value);
        text1.setText(text1value);

        if (type == 0) {
            radioGroup.setVisibility(View.GONE);
            radioGroup2.setVisibility(View.GONE);
            radioGroup3.setVisibility(View.GONE);
            text5.setVisibility(View.GONE);
        }

        if (type == 1) {
            radioGroup.setVisibility(View.VISIBLE);
            radioGroup2.setVisibility(View.GONE);
            radioGroup3.setVisibility(View.GONE);

            radioButton1.setEnabled(true);
            radioButton1.setChecked(false);

            radioButton2.setEnabled(true);
            radioButton2.setChecked(false);

            radioButton3.setEnabled(true);
            radioButton3.setChecked(false);

            radioButton1.setText(text2value);
            radioButton2.setText(text3value);
            radioButton3.setText(text4value);
        }
        if (type == 2) {
            radioGroup.setVisibility(View.GONE);
            radioGroup2.setVisibility(View.VISIBLE);
            radioGroup3.setVisibility(View.GONE);

            check1.setEnabled(true);
            check2.setEnabled(true);
            check3.setEnabled(true);
            check1.setText(text2value);
            check2.setText(text3value);
            check3.setText(text4value);
        }
        if (type == 3) {
            radioGroup.setVisibility(View.GONE);
            radioGroup2.setVisibility(View.GONE);
            radioGroup3.setVisibility(View.VISIBLE);
            editBox.setVisibility(View.VISIBLE);
            editBox.setEnabled(true);
            editBox.setHint("type your answer here");
        }


        text5.setText(text5value);
    }

    //Animates welcome message with variable delays for characters and sentences
    public void welcomeAnim(View view) {
        TextView intro = findViewById(R.id.intro);
        intro.setVisibility(View.GONE);

        final TextView text1 = findViewById(R.id.text1);
        text1.clearComposingText();
        text1.setEnabled(false);
        Resources resources = getResources();
        String welcomeText = resources.getString(R.string.welcome);
        text1.setTextSize(24);
        text1.setGravity(Gravity.NO_GRAVITY);

        final int welcomeTextLenght = welcomeText.length();

        final char[] letters = welcomeText.toCharArray();
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            int counter = 0;
            int pause = 0;
            String currentDisplay = "";

            @Override
            public void run() {

                if (counter < welcomeTextLenght && (letters[counter] == '!' || letters[counter] == '.'))
                    pause = 1;

                if (counter < welcomeTextLenght && pause == 0) {
                    currentDisplay += letters[counter];
                    text1.setText(currentDisplay);
                    counter++;
                } else if (counter < welcomeTextLenght && pause == 1) {
                    currentDisplay += letters[counter];
                    text1.setText(currentDisplay);
                    counter++;
                    pause = 2;
                } else if (pause > 1) {
                    pause++;
                    if (pause == 50)
                        pause = 0;
                } else {
                    Button button = findViewById(R.id.button);
                    button.setVisibility(View.VISIBLE);
                }
                handler.postDelayed(this, 15);
            }
        });
    }

    //Animates intro message with variable delays for characters and words
    public void introAnim() {
        Resources resources = getResources();
        final TextView intro = findViewById(R.id.intro); //Using separate view for this to avoid problems with being unable to reset onClick value

        intro.setEnabled(false); //Prevent cutting short of intro message

        final String[] words = resources.getStringArray(R.array.intro);
        final int totalWords = words.length;

        final Handler handler = new Handler();

        handler.post(new Runnable() {
            String thisWord = "";
            int currentWord = 0;
            int wordCount = 0;
            int direction = 0;
            int peak = 0;
            int cutoff = 0;
            String currentDisplay = "";
            int thisLetterNumber = -1;

            @Override
            public void run() {
                //Prevent index out of range
                if (currentWord < words.length) {
                    thisWord = words[currentWord];

                    int thisWordLenght = thisWord.length();
                    char[] letters = thisWord.toCharArray();
                    //Writing text from left to right
                    if (direction == 0) {
                        thisLetterNumber++;
                        currentDisplay += letters[thisLetterNumber];
                        intro.setText(currentDisplay);
                        if (thisLetterNumber == thisWordLenght - 1)
                            direction = 1;
                    }
                    //Recognise the word is complete
                    else if (direction == 1) {
                        peak++;
                        intro.setText(currentDisplay);
                        if (peak == 20)
                            direction = 2;
                    }
                    //Deleting text from right to left
                    else if (direction == 2 && currentWord < totalWords - 1) {
                        cutoff++;
                        currentDisplay = currentDisplay.substring(0, thisWordLenght - cutoff);
                        intro.setText(currentDisplay);
                        if (cutoff == thisWordLenght)
                            direction = 3;
                    }
                    //Cycle complete, reset for next word
                    else if (direction == 3) {
                        currentWord++;
                        direction = 0;
                        cutoff = 0;
                        peak = 0;
                        thisLetterNumber = -1;
                    } else
                        intro.setEnabled(true); //Make clickable only when intro complete

                }
                handler.postDelayed(this, 50);
            }
        });
    }
}
