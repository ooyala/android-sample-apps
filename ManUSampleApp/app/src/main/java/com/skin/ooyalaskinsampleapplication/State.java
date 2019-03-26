package com.skin.ooyalaskinsampleapplication;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Copyright (c) 2016 HCL Technologies Ltd
 * All Rights Reserved
 * Created by venkatraog on 7/10/2016.
 */
public class State implements Serializable {
    //    quiz
    public int score = 0;
    public int currentQuizIndex = 1;
    public boolean currentQuizDone = false;
    public boolean isCurrectAnswer = false;
    public boolean isQuizDone = false;
    public ArrayList<String> selectedAnswerId = new ArrayList<>();

    //poll
    public int userSelectedIndex = 0;
    public boolean showPercentage = false;
    public boolean showProgressBar = false;
    public ArrayList<Integer> percentages = new ArrayList<>();

    //video
    public boolean startAutoPlaying = false;
    public boolean videoCompleted = false;
    public boolean isPaused = false;


}
