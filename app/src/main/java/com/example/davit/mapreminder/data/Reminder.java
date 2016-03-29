package com.example.davit.mapreminder.data;

/**
 * Class Reminder
 */
public class Reminder {

    /** private fields */
    private long id;
    private String reminderName;
//    private int imageResource;
    private double distanceToReminderRadius;
    private double distance;
    private int isActive;
    private String type;
    private String description;
    private double targetCordLat;
    private double targetCordLng;
    private String created;
    private Long fromDate;
    private Long fromTime;
    private Long toDate;
    private Long toTime;
    private int scheduleOption;

    /**
     * default constructor
     */
    public Reminder(){

    }

    /** getters and setters */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReminderName() {
        return reminderName;
    }

    public void setReminderName(String reminderName) {
        this.reminderName = reminderName;
    }

//    public int getImageResource() {
//        return imageResource;
//    }
//
//    public void setImageResource(int imageResource) {
//        this.imageResource = imageResource;
//    }

    public double getDistance() {
        return distance;
    }

    public String getDistanceAsString() {
        return Double.toString(distance);
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTargetCordLat() {
        return targetCordLat;
    }

    public void setTargetCordLat(double targetCordLat) {
        this.targetCordLat = targetCordLat;
    }

    public double getTargetCordLng() {
        return targetCordLng;
    }

    public void setTargetCordLng(double targetCordLng) {
        this.targetCordLng = targetCordLng;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getFromTime() {
        return fromTime;
    }

    public void setFromTime(Long fromTime) {
        this.fromTime = fromTime;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    public Long getToTime() {
        return toTime;
    }

    public void setToTime(Long toTime) {
        this.toTime = toTime;
    }

    public int getScheduleOption() {
        return scheduleOption;
    }

    public void setScheduleOption(int scheduleOption) {
        this.scheduleOption = scheduleOption;
    }

    public double getDistanceToReminderRadius() {
        return distanceToReminderRadius;
    }

    public String getDistanceToReminderRadiusAsString() {
        return Double.toString(distanceToReminderRadius);
    }

    public void setDistanceToReminderRadius(double distanceToReminderRadius) {
        this.distanceToReminderRadius = distanceToReminderRadius;
    }


    @Override
    public String toString() {
        return getReminderName();
    }



}
