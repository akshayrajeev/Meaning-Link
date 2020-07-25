package com.meaninglink;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Word {
    HashMap<String, HashSet<String>> meaning;
    HashMap<String, HashSet<String>> example;

    Word() {
        meaning = new HashMap<>();
        example = new HashMap<>();
    }

    void addMeaning(String key, String value) {
        if(meaning.containsKey(key)) {
            meaning.get(key).add(value);
        }
        else {
            meaning.put(key, new HashSet<>(Collections.singletonList(value)));
        }
    }

    void addExample(String key, String value) {
        if(example.containsKey(key)) {
            example.get(key).add(value);
        }
        else {
            example.put(key, new HashSet<>(Collections.singletonList(value)));
        }
    }

    SpannableStringBuilder getMessage() {
        SpannableStringBuilder message = new SpannableStringBuilder();
        for(String heading : meaning.keySet()) {
            SpannableString spanHeading = new SpannableString(heading);
            spanHeading.setSpan(new ForegroundColorSpan(Color.BLUE), 0, heading.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanHeading.setSpan(new StyleSpan(Typeface.BOLD), 0 , heading.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanHeading.setSpan(new RelativeSizeSpan(1.3f), 0, heading.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            message.append("\n").append(spanHeading).append("\n");
            for(String meaning : meaning.get(heading)) {
                SpannableString spanMeaning = new SpannableString(meaning);
                spanMeaning.setSpan(new StyleSpan(Typeface.BOLD), 0 , meaning.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanMeaning.setSpan(new RelativeSizeSpan(1.1f), 0, meaning.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                message.append("\n").append(spanMeaning).append("\n");
                if(example.containsKey(meaning)) {
                    for(String example : example.get(meaning)) {
                        SpannableString spanExample = new SpannableString(example);
                        spanExample.setSpan(new BulletSpan(40, Color.BLACK), 0, example.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        message.append("\n").append(spanExample).append("\n");
                    }
                }
                message.append("\n");
                //message.append("--------------------------------------------------------------------\n");
            }
            message.append("*******************************************\n");
        }
        return message;
    }

    boolean isNull() {
        return meaning.size() == 0 && example.size() == 0;
    }
}
