package com.startoonlabs.apps.pheezee.utils;

public class MuscleOperation {
    public static String[][] musle_names = {
            {"Select Muscle", "Biceps", "Brachialis", "Triceps","Brachioradialis", "Anconeus", "Infraspinatus", "Teres Minor", "Pectoralis Major",
                    "Pectoralis Minor", "Serratus Anterior", "Trapezius"},//elbow

            {"Select Muscle", "Rectus Femoris-Anterior", "Vastus Lateralis-Anterior", "Vastus Medialis -Anterior", "Vastus Intermedius-Anterior(Deep)",
                    "Sartorius-Anterior", "Gracilis-Medial", "Biceps Femoris-Posterior", "Semimembranosus -Posterior", "Semitendinosus-Posterior", "Gastrocnemius-Posterior",
                     }, //Knee

            {"Select Muscle", "Gastrocnemius-Posterior", "superficial-Part of Triceps Surae", "Soleus -Posterior", "Plantaris-Posterior",
                    "Flexor Digitorum Longus(Deep)", "Flexor Hallucis Longus(Deep)", "Tibialis Posterior(Deep)", "Tibialis Anterior-Anterior",
                    "Extensor Digitorum Longus-Anterior", "Extensor Hallucis Longus-Anterior", "Peroneus Tertius-Anterior",
                    "Peroneus Longus-Lateral", "Peroneus Brevis-Lateral"},    //Ankle

            {"Select Muscle", "Rectus Femoris  -Anterior", "Sartorius-Anterior", "Pectineus-Medial(Deep)", "Gracilis -Medial", "Gluteus Maximus-Gluteal",
                    "Gluteus Medius And Gluteus Minimus-Gluteal", "Tensor Fasciae Latae-Gluteal ", "Piriformis(Deep)", "Superior(Deep)", "Gemellus(Deep)",
                    "Obturator(Deep)", "Internus(Deep)", "Inferior Gemellus(Deep)", "Quadratus Femoris Obturator Externus(Deep)", "Biceps Femoris - Posterior",
                    "Semimembranosus-Posterior", "Semitendinosus-Posterior"},  //Hip //from 4 to 8 i have taken as different muscles as specified in the excell sheet

            {"Select Muscle", "Flexor Carpi Radialis", "Palmaris Longus", "Flexor Carpi Ulnaris", "Flexor Pollicis Longus (Deep)",
                    "Flexor Digitorum  Profundus (Deep)", "Pronator Quadratus (Deep)", "Extensor Carpi  Radialis Longus and Brevis", "Extensor Digiti Minimi",
                    "Extensor Carpi Ulnaris", "Supinator (Deep)", "Pronator Teres (Deep)", "Biceps"},  //Wrist

            {"Select Muscle", "Pectoralis Major", "Pectoralis Minor", "Serratus Anterior", "Trapezius",  "Latissimus Dorsi", "Deltoid",
                    "Teres Major",  "Teres Minor", "Long Head of Biceps", "Short head of biceps"},   //Shoulder
            {}
    };


    public static String[] getMusleNames(int postion){
        return musle_names[postion];
    }
}
