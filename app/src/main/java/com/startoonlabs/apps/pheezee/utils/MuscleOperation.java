package com.startoonlabs.apps.pheezee.utils;

public class MuscleOperation {
    private final static String[][] musle_names = {
            {"Select Muscle*", "Biceps", "Brachialis(Deep)", "Triceps","Brachioradialis", "Anconeus", "Infraspinatus", "Teres Minor", "Teres Major", "Pectoralis Major",
                    "Pectoralis Minor", "Serratus Anterior", "Trapezius", "Others"},//elbow

            {"Select Muscle*", "Rectus Femoris-Anterior", "Vastus Lateralis-Anterior", "Vastus Medialis -Anterior", "Vastus Intermedius-Anterior(Deep)",
                    "Sartorius-Anterior", "Gracilis-Medial", "Biceps Femoris-Posterior", "Semimembranosus -Posterior", "Semitendinosus-Posterior",
                    "Gastrocnemius-Posterior", "Gastrocnemius-medial", "Gastrocnemius-lateral", "Others"
            }, //Knee

            {"Select Muscle*", "Gastrocnemius-Posterior", /*"superficial-Part of Triceps Surae",*/ "Soleus -Posterior", "Plantaris-Posterior",
                    "Flexor Digitorum Longus(Deep)", "Flexor Hallucis Longus(Deep)", "Tibialis Posterior(Deep)", "Tibialis Anterior",
                    "Extensor Digitorum Longus-Anterior", "Extensor Hallucis Longus-Anterior", "Peroneus Tertius-Anterior",
                    "Peroneus Longus-Lateral", "Peroneus Brevis-Lateral", "Others"},    //Ankle

            {"Select Muscle*", "Rectus Femoris-Anterior", "Sartorius-Anterior", "Pectineus-Medial(Deep)", "Gracilis -Medial", "Gluteus Maximus-Gluteal",
                    "Gluteus Medius And Gluteus Minimus-Gluteal", "Tensor Fasciae Latae-Gluteal ", "Piriformis(Deep)", "Superior(Deep)", "Gemellus(Deep)",
                    "Obturator(Deep)", "Internus(Deep)", "Inferior Gemellus(Deep)", "Quadratus Femoris Obturator Externus(Deep)", "Biceps Femoris-Posterior",
                    "Semimembranosus-Posterior", "Semitendinosus-Posterior", "Others"},  //Hip //from 4 to 8 i have taken as different muscles as specified in the excell sheet

            {"Select Muscle*", "Flexor Carpi Radialis", "Palmaris Longus", "Flexor Carpi Ulnaris", "Flexor Pollicis Longus(Deep)",
                    "Flexor Digitorum  Profundus(Deep)", "Pronator Quadratus(Deep)", "Extensor Carpi Radialis Longus and Brevis", "Extensor Digiti Minimi",
                    "Extensor Carpi Ulnaris", "Supinator(Deep)", "Pronator Teres(Deep)", "Others"},  //Wrist

            {"Select Muscle*", "Pectoralis Major", "Pectoralis Minor", "Serratus Anterior", "Trapezius",  "Latissimus Dorsi", "Deltoid",
                    "Teres Major",  "Teres Minor", "Biceps", "Others"},   //Shoulder
            {"Select Muscle*","Others"}
    };


    public static String[] getMusleNames(int postion){
        return musle_names[postion];
    }


    private final static String[][] exercise_names = {
            {"Select Exercise*", "Flexion", "Extension", "Medial rotation", "Lateral Rotation", "Pronation", "Supination", "Isometric"},//elbow

            {"Select Exercise*", "Flexion", "Extension", "Medial Rotation", "Lateral Rotation", "Isometric"}, //Knee

            {"Select Exercise*", "Dorsiflexion", "Plantarflexion", "Inversion", "Eversion", "Isometric"},    //Ankle

            {"Select Exercise*", "Flexion", "Extension", "Abduction", "Adduction", "Isometric"},  //Hip

            {"Select Exercise*", "Flexion", "Extension", "Radial deviation", "Ulnar deviation", "Isometric"},  //Wrist

            {"Select Exercise*", "Flexion", "Extension", "Abduction", "Adduction", "Protraction", "Retraction", "Elevation",
                    "Depression", "Others"},   //Shoulder
            {"Select Exercise*","Others"}
    };

    public static String[] getExerciseNames(int postion){
        return exercise_names[postion];
    }

}
