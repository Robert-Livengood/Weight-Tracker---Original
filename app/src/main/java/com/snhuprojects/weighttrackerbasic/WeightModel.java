package com.snhuprojects.weighttrackerbasic;

public class WeightModel {
    private int id;
    private String name;
    private String date;
    private int weight;
    private int delta;
    private int goalWeight;

    // constructor


    public WeightModel(int id, String name, String date, int weight) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.weight = weight;
        this.delta = 0;
        this.goalWeight = 0;
    }

    public WeightModel() {
    }

    public WeightModel(int id, String name, String date, int weight, int delta) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.weight = weight;
        this.delta = delta;
        this.goalWeight = 0;

    }

    // toString for printing and class object


    @Override
    public String toString() {
        return "WeightModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", weight=" + weight +
                ", delta=" + delta +
                ", goalWeight=" + goalWeight +
                '}';
    }

    // getters and setters

    public int getGoalWeight() {
        return goalWeight;
    }

    public void setGoalWeight(int goalWeight) {
        this.goalWeight = goalWeight;
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
