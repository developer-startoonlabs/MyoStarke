package com.start.apps.pheezee.room.Entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mqtt_sync")
public class MqttSync {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String topic;
    private  String message;

    public MqttSync( String topic, String message) {
        this.topic = topic;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
