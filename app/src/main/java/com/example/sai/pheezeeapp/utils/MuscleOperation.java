package com.example.sai.pheezeeapp.utils;

import com.example.sai.pheezeeapp.activities.SessionReportActivity;

public class MuscleOperation {
    public static String[][] musle_names = {
            {"Long Head of Biceps", "Short head of biceps", "Brachialis", "Lateral Head of Triceps","Long Head of Triceps", "Medial Head of Triceps","Brachioradialis", "Anconeus"},
            {},
            {},
            {},
            {},
            {},
            {}
    };


    public static String[] getMusleNames(int postion){
        return musle_names[postion];
    }
}
