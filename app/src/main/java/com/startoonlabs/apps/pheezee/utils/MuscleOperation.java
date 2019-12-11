package com.startoonlabs.apps.pheezee.utils;

public class MuscleOperation {
    private final static String[][] musle_names = {
            {"Select Muscle*", "Bicep", "Brachialis (deep)","Brachioradialis", "Tricep", "Anconeus", "Others"},//elbow

            {"Select Muscle*", "Sartorius - Anterior", "Gracilis -Medial", "Biceps Femoris - Posterior", "Semimembranosus -Posterior",
                    "Semitendinosus -Posterior", "Popliteus(Posterior, deep)", "Gastrocnemius-Posterior", "Rectus Femoris - Anterior",
                    "Vastus Lateralis - Anterior", "Vastus Medialis - Anterior","Vastus Intermedius - Anterior (Deep)", "Others"
            }, //Knee

            {"Select Muscle*", "Gastrocnemius-Posterior", /*"superficial-Part of Triceps Surae",*/ "Soleus -Posterior", "Plantaris-Posterior",
                    "Flexor Digitorum Longus(Deep)", "Flexor Hallucis Longus(Deep)", "Tibialis Posterior(Deep)", "Tibialis Anterior",
                    "Extensor Digitorum Longus-Anterior", "Extensor Hallucis Longus-Anterior", "Peroneus Tertius-Anterior",
                    "Peroneus Longus-Lateral", "Peroneus Brevis-Lateral", "Others"},    //Ankle

            {"Select Muscle*", "Rectus Femoris-Anterior", "Sartorius-Anterior", "Pectineus-Medial(Deep)",
                    "Gluteus Medius And Gluteus Minimus-Gluteal", "Tensor Fasciae Latae-Gluteal ", "Adductor Longus -Medial",
                    "Adductor Brevis -Medial","Adductor Magnus - Medial","Psoas Major","Iliacus","Gluteus Maximus -Gluteal","Biceps Femoris - Posterior",
                    "Semimembranosus - Posterior", "Semitendinosus -Posterior", "Gracilis -Medial","Piriformis(Deep)",
                    "Superior Gemellus(Deep)", "Obturator Internus(Deep)","Inferior Gemellus (Deep)", "Quadratus Femoris Obturator Externus (Deep)", "Others"},  //Hip //from 4 to 8 i have taken as different muscles as specified in the excell sheet

            {"Select Muscle*", "Flexor Carpi Radialis", "Palmaris Longus", "Flexor Carpi Ulnaris", "Flexor Pollicis Longus(Deep)",
                    "Flexor Digitorum Superficialis ( Intermediate )", "Flexor Digitorum  Profundus(Deep)", "Extensor Carpi Radialis Longus and Brevis",
                    "Extensor Digitorum", "Extensor Carpi Ulnaris", "Extensor Digiti Minimi", "Others"},  //Wrist

            {"Select Muscle*", "Pectoralis Major", "Latissimus Dorsi", "Teres Major", "Subscapularis", "Deltoid",  "Biceps",
                    "Coracobrachialis",  "Pectoralis Minor", "Serratus Anterior", "Infraspinatus", "Teres Minor", "Trapezius","Others"},   //Shoulder

            {"Select Muscle*","Bicep","Supinator (Deep)","Pronator Quadratus (Deep)", "Pronator Teres (Deep)"}, //forearm
            {"Select Muscle*","Quadratus lumborum","Spinalis thoracis","Longissimus thoracis", "Iliocostalis thoracis","Iliocostalis lumborum"}, //Spine
            {"Select Muscle*","Others"}
    };


    public static String[] getMusleNames(int postion){
        return musle_names[postion];
    }


    private final static String[][] exercise_names = {
            {"Select Exercise*", "Flexion", "Extension", "Isometric"},//elbow

            {"Select Exercise*", "Flexion", "Extension",  "Isometric"}, //Knee

            {"Select Exercise*",  "Plantarflexion", "Dorsiflexion", "Inversion", "Eversion", "Isometric"},    //Ankle

            {"Select Exercise*", "Flexion", "Extension",  "Adduction", "Abduction", "Medial Rotation","Lateral Rotation", "Isometric"},  //Hip

            {"Select Exercise*", "Flexion", "Extension", "Radial deviation", "Ulnar deviation", "Isometric"},  //Wrist

            {"Select Exercise*", "Adduction","Abduction", "Flexion", "Extension", "Medial rotation", "Lateral Rotation",
//                    "Protraction", "Retraction", "Elevation", "Depression",
                    "Isometric"},   //Shoulder
            {"Select Exercise*","Supination", "Pronation","Isometric"},//forearm
            {"Select Exercise*","Flexion", "Extension","Lateral Flexion","Rotation","Isometric"},//Spine
            {"Select Exercise*","Others"}
    };

    public static String[] getExerciseNames(int postion){
        return exercise_names[postion];
    }

}
