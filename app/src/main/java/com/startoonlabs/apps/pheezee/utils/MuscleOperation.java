package com.startoonlabs.apps.pheezee.utils;

public class MuscleOperation {
    public static String[][] musle_names = {
            {"Select Muscle", "Long Head of Biceps", "Short head of biceps", "Brachialis", "Lateral Head of Triceps","Long Head of Triceps",
                    "Medial Head of Triceps","Brachioradialis", "Anconeus"},//elbow

            {"Select Muscle", "Quadratus Femoris Obturator Externus", "Biceps Femoris-Posterior", "Semimembranosus-Posterior", "Semitendinosus-Posterior",
                    "Rectus Femoris-Anterior", "Vastus Lateralis-Anterior", "Vastus Medialis -Anterior", "Vastus Intermedius-Anterior",
                    "Sartorius Anterior", "Pectineus-Medial" }, //Knee

            {"Select Muscle", "Extensor Digitorum Brevis","Abductor Hallucis", "Flexor Digitorum Brevis", "Abductor Digiti Minimi", "Quadratus Plantae", "Lumbricals(4)",
                    "Flexor Hallucis Brevis", "Adductor Hallucis", "Flexor Digiti Minimi Brevis", "Dorsal Interossei (4)", "Plantar Interossei (3)",
                    "Gastrocnemius-Posterior", "Soleus -Posterior", "Plantaris-Posterior", "Popliteus-Posterior", "Flexor Digitorum Longus",
                    "Flexor Hallucis Longus", "Tibialis Posterior-Posterior", "Tibialis Anterior -Anterior", "Extensor Digitorum Longus -Anterior",
                    "Extensor Hallucis Longus-Anterior", "Peroneus Tertius-Anterior", "Peroneus Longus-Lateral", "Peroneus Brevis-Lateral"},    //Ankle

            {"Select Muscle", "Gracilis -Medial", "Gluteus Maximus-Gluteal", "Gluteus Medius And Gluteus Minimus-Gluteal", "Tensor Fasciae Latae-Gluteal ",
                    "Piriformis", "Superior", "Gemellus", "Obturator", "Internus", "Inferior Gemellus", "Adductor Longus-Medial", "Adductor Brevis -Medial", "Adductor Magnus - Medial"},  //Hip //from 4 to 8 i have taken as different muscles as specified in the excell sheet

            {"Select Muscle", "Flexor Carpi Radialis", "Palmaris Longus", "Flexor Carpi Ulnaris", "Flexor Digitorum Superficialis", "Flexor Pollicis Longus", "Flexor Digitorum  Profundus",
                    "Pronator Quadratus"},  //Wrist

            {"Select Muscle", "Pectoralis Major", "Pectoralis Minor", "Serratus Anterior", "Trapezius", "Rhomboids", "Levator Scapulae", "Latissimus Dorsi", "Deltoid",
                    "Teres Major", "Coracobrachialis", "Subscapularis", "Supraspinatus", "Infraspinatus", "Teres Minor"},   //Shoulder
            {}
    };


    public static String[] getMusleNames(int postion){
        return musle_names[postion];
    }
}
